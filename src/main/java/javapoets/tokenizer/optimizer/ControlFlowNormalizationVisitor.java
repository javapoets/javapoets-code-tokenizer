package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.*;
import java.util.ArrayList;
import java.util.List;

public class ControlFlowNormalizationVisitor extends BaseAstVisitor<AstNode> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ControlFlowNormalizationVisitor.class);

    // -------------------------
    // EXPRESSIONS (mostly pass-through)
    // -------------------------

    @Override
    public AstNode visitLiteral(AstNode.LiteralExpression node) {
        return node;
    }

    @Override
    public AstNode visitIdentifier(AstNode.IdentifierExpression node) {
        return node;
    }

    @Override
    public AstNode visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return expr;
    }

    @Override
    public AstNode visitBinary(AstNode.BinaryExpression node) {
        Expression left = (Expression) node.left().accept(this);
        Expression right = (Expression) node.right().accept(this);
        return new AstNode.BinaryExpression(left, node.operator(), right);
    }

    /*
    @Override
    public AstNode visitAssignment(AssignmentExpression node) {
        Expression left = (Expression) node.left().accept(this);
        Expression right = (Expression) node.right().accept(this);
        return new AssignmentExpression(left, node.operator(), right);
    }
    */
    @Override
    public AstNode visitAssignment(AssignmentExpression node) {
        Expression target = (Expression) node.target().accept(this);
        Expression value = (Expression) node.value().accept(this);

        return new AssignmentExpression(target, node.operator(), value);
    }

    @Override
    public AstNode visitFunctionCall(FunctionCallExpression node) {
        Expression callee = (Expression) node.callee().accept(this);

        List<Expression> args = node.arguments().stream()
            .map(arg -> (Expression) arg.accept(this))
            .toList();

        return new FunctionCallExpression(callee, args);
    }

    @Override
    public AstNode visitMemberAccess(MemberAccessExpression node) {
        Expression object = (Expression) node.object().accept(this);
        return new MemberAccessExpression(object, node.property());
    }

    // -------------------------
    // STATEMENTS
    // -------------------------

    @Override
    public AstNode visitVariableDeclaration(VariableDeclaration node) {
        log.trace("visitVariableDeclaration(VariableDeclaration node)");

        Expression initializer = node.initializer() != null
            ? (Expression) node.initializer().accept(this)
            : null;

        return new VariableDeclaration(node.keyword(), node.name(), initializer);
    }

    @Override
    public AstNode visitExpressionStatement(ExpressionStatement node) {
        Expression expr = (Expression) node.expression().accept(this);
        return new ExpressionStatement(expr);
    }

    @Override
    public AstNode visitReturn(ReturnStatement node) {
        Expression value = node.expression() != null
            ? (Expression) node.expression().accept(this)
            : null;

        return new ReturnStatement(value);
    }

    @Override
    public AstNode visitFunctionDeclaration(FunctionDeclaration node) {

        /*
        List<Statement> body = node.body().stream()
            .map(stmt -> (Statement) stmt.accept(this))
            .toList();
        return new FunctionDeclaration(
            node.name(),
            node.parameters(),
            new BlockStatement(body)
        );
        */
        BlockStatement bodyBlock = (BlockStatement) node.body().accept(this);

        return new FunctionDeclaration(
            node.name(),
            node.parameters(),
            bodyBlock
        );

    }

    // -------------------------
    // IF STATEMENT
    // -------------------------

    /*
    @Override
    public AstNode visitIfStatement(IfStatement stmt) {

        Expression condition = (Expression) stmt.condition().accept(this);

        Statement thenBranch = (Statement) stmt.thenBranch().accept(this);
        Statement elseBranch = stmt.elseBranch() != null
            ? (Statement) stmt.elseBranch().accept(this)
            : null;

        // Normalize branches to blocks
        thenBranch = ensureBlock(thenBranch);

        if (elseBranch != null) {
            elseBranch = ensureBlock(elseBranch);
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }
    */
    @Override
    public AstNode visitIfStatement(IfStatement stmt) {

        Expression condition = (Expression) stmt.condition().accept(this);

        Statement thenBranch = (Statement) stmt.thenBranch().accept(this);
        Statement elseBranch = stmt.elseBranch() != null
            ? (Statement) stmt.elseBranch().accept(this)
            : null;

        // Normalize branches
        thenBranch = ensureBlock(thenBranch);

        if (elseBranch != null) {
            elseBranch = ensureBlock(elseBranch);
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    // -------------------------
    // BLOCK
    // -------------------------

    /*
    @Override
    public AstNode visitBlockStatement(BlockStatement block) {

        List<Statement> normalized = new ArrayList<>();

        for (Statement stmt : block.statements()) {

            Statement result = (Statement) stmt.accept(this);

            // Remove empty statements
            if (result instanceof EmptyStatement) continue;

            // Flatten nested blocks
            if (result instanceof BlockStatement nested) {
                normalized.addAll(nested.statements());
            } else {
                normalized.add(result);
            }
        }

        return new BlockStatement(normalized);
    }
    */

    @Override
    public AstNode visitBlockStatement(BlockStatement block) {

        List<Statement> normalized = new ArrayList<>();

        for (Statement stmt : block.statements()) {

            Statement result = (Statement) stmt.accept(this);

            // 🔥 Remove empty statements
            if (result instanceof EmptyStatement) continue;

            // 🔥 Flatten nested blocks
            if (result instanceof BlockStatement nested) {
                normalized.addAll(nested.statements());
            } else {
                normalized.add(result);
            }
        }

        return new BlockStatement(normalized);
    }

    @Override
    public AstNode visitEmptyStatement(EmptyStatement stmt) {
        return stmt;
    }

    // -------------------------
    // HELPERS
    // -------------------------

    private BlockStatement ensureBlock(Statement stmt) {
        if (stmt instanceof BlockStatement block) {
            return block;
        }
        return new BlockStatement(List.of(stmt));
    }
}
