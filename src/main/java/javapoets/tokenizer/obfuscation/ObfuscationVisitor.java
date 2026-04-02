package javapoets.tokenizer.obfuscation;

import javapoets.tokenizer.ast.*;

public class ObfuscationVisitor implements AstVisitor<AstNode> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ObfuscationVisitor.class);

    private final ScopeStack scopes = new ScopeStack();
    private final NameGenerator generator = new NameGenerator();

    public ObfuscationVisitor() {
        scopes.push(); // global scope
    }

    @Override
    public AstNode visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return expr;
    }

    @Override
    public AstNode visitEmptyStatement(EmptyStatement stmt) {
        return stmt;
    }
    
    // -------------------
    // HELPERS
    // -------------------

    private String declare(String original) {
        String obfuscated = generator.next();
        scopes.declare(original, obfuscated);

        log.debug("Declare '{}' -> '{}'", original, obfuscated);
        return obfuscated;
    }

    private String resolve(String name) {
        String resolved = scopes.resolve(name);

        if (resolved != null) {
            return resolved;
        }

        // fallback: treat as global (or external symbol)
        return name;
    }

    // -------------------
    // EXPRESSIONS
    // -------------------

    @Override
    public AstNode visitLiteral(AstNode.LiteralExpression node) {
        return node;
    }

    @Override
    public AstNode visitIdentifier(AstNode.IdentifierExpression node) {
        String resolved = resolve(node.name());

        log.debug("Resolve '{}' -> '{}'", node.name(), resolved);

        return new AstNode.IdentifierExpression(resolved);
    }

    @Override
    public AstNode visitBinary(AstNode.BinaryExpression node) {
        Expression left = (Expression) node.left().accept(this);
        Expression right = (Expression) node.right().accept(this);

        return new AstNode.BinaryExpression(left, node.operator(), right);
    }

    // -------------------
    // STATEMENTS
    // -------------------

    @Override
    public AstNode visitVariableDeclaration(VariableDeclaration node) {
        log.trace("visitVariableDeclaration(VariableDeclaration node)");

        String newName = declare(node.name());
        //String newName = obfuscateName(node.name());
        log.debug("Renaming variable '{}' -> '{}'", node.name(), newName);

        Expression newInitializer = null;
        if (node.initializer() != null) {
            newInitializer = (Expression) node.initializer().accept(this);
        }

        return new VariableDeclaration(
            node.keyword(),
            newName,
            newInitializer
        );
    }

    @Override
    public AstNode visitExpressionStatement(ExpressionStatement node) {
        Expression expr = (Expression) node.expression().accept(this);
        return new ExpressionStatement(expr);
    }

    @Override
    public AstNode visitBlockStatement(BlockStatement node) {

        log.debug("Entering new scope");
        scopes.push();

        var newStatements = node.statements().stream()
            .map(stmt -> (Statement) stmt.accept(this))
            .toList();

        scopes.pop();
        log.debug("Exiting scope");

        return new BlockStatement(newStatements);
    }

    @Override
    public AstNode visitFunctionDeclaration(FunctionDeclaration node) {

        // Function name declared in outer scope
        String newName = declare(node.name());

        log.debug("Function '{}' -> '{}'", node.name(), newName);

        // New function scope
        scopes.push();
        log.debug("Entering function scope");

        // Declare parameters
        java.util.List<String> newParams = node.parameters().stream()
            .map(param -> {
                String obf = declare(param);
                log.debug("Param '{}' -> '{}'", param, obf);
                return obf;
            })
            .toList();

        BlockStatement newBody =
            (BlockStatement) node.body().accept(this);

        scopes.pop();
        log.debug("Exiting function scope");

        return new FunctionDeclaration(newName, newParams, newBody);
    }

    @Override
    public AstNode visitReturn(ReturnStatement node) {
        if (node.expression() == null) {
            return node;
        }

        Expression expr = (Expression) node.expression().accept(this);
        return new ReturnStatement(expr);
    }

    @Override
    public AstNode visitAssignment(AssignmentExpression node) {

        log.debug("Transforming assignment target='{}' operator='{}'", node.target(), node.operator());
        
        Expression newTarget = (Expression) node.target().accept(this);
        Expression newValue = (Expression) node.value().accept(this);

        return new AssignmentExpression(newTarget, node.operator(), newValue);
    }

    @Override
    public AstNode visitIfStatement(IfStatement node) {

        Expression condition = (Expression) node.condition().accept(this);
        Statement thenBranch = (Statement) node.thenBranch().accept(this);

        Statement elseBranch = null;
        if (node.elseBranch() != null) {
            elseBranch = (Statement) node.elseBranch().accept(this);
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    @Override
    public AstNode visitFunctionCall(FunctionCallExpression node) {

        Expression callee = (Expression) node.callee().accept(this);

        var args = node.arguments().stream()
            .map(arg -> (Expression) arg.accept(this))
            .toList();

        return new FunctionCallExpression(callee, args);
    }

    @Override
    public AstNode visitMemberAccess(MemberAccessExpression node) {

        Expression object = (Expression) node.object().accept(this);

        // Property name is NOT obfuscated
        return new MemberAccessExpression(object, node.property());
    }
}