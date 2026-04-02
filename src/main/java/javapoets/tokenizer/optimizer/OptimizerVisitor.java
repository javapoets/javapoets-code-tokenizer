package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.*;

//public class OptimizerVisitor implements AstVisitor<AstNode> {
public class OptimizerVisitor extends BaseAstVisitor<AstNode> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OptimizerVisitor.class);

    @Override
    public AstNode visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return expr; // no-op
    }

    @Override
    public AstNode visitEmptyStatement(EmptyStatement stmt) {
        return stmt;
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
        return node;
    }

    @Override
    public AstNode visitBinary(AstNode.BinaryExpression node) {

        Expression left = (Expression) node.left().accept(this);
        Expression right = (Expression) node.right().accept(this);

        // CONSTANT FOLDING
        if (left instanceof AstNode.LiteralExpression l && right instanceof AstNode.LiteralExpression r) {
            Object result = fold(l.value(), node.operator(), r.value());

            if (result != null) {
                return new AstNode.LiteralExpression(result);
            }
        }

        return new AstNode.BinaryExpression(left, node.operator(), right);
    }

    private Object fold(Object left, String op, Object right) {

        if (left instanceof Number l && right instanceof Number r) {
            double a = l.doubleValue();
            double b = r.doubleValue();

            return switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> b != 0 ? a / b : null;
                case "%" -> b != 0 ? a % b : null;

                case ">" -> a > b;
                case "<" -> a < b;
                case ">=" -> a >= b;
                case "<=" -> a <= b;

                case "==" -> a == b;
                case "!=" -> a != b;

                default -> null;
            };
        }

        return null;
    }

    // -------------------
    // STATEMENTS
    // -------------------

    @Override
    public AstNode visitVariableDeclaration(VariableDeclaration node) {
        log.trace("visitVariableDeclaration(VariableDeclaration node)");

        Expression init = null;
        if (node.initializer() != null) {
            init = (Expression) node.initializer().accept(this);
        }
        return new VariableDeclaration(node.keyword(), node.name(), init);
    }

    @Override
    public AstNode visitExpressionStatement(ExpressionStatement node) {
        return new ExpressionStatement(
            (Expression) node.expression().accept(this)
        );
    }

    /*
    @Override
    public AstNode visitBlockStatement(BlockStatement node) {
        var statements = node.statements().stream()
            .map(stmt -> (Statement) stmt.accept(this))
            .toList();

        return new BlockStatement(statements);
    }
    */
    @Override
    public AstNode visitBlockStatement(BlockStatement block) {

        java.util.List<Statement> normalized = new java.util.ArrayList<>();

        for (Statement stmt : block.statements()) {

            Statement result = (Statement) stmt.accept(this);

            // 🔥 REMOVE EMPTY STATEMENTS HERE
            if (result instanceof EmptyStatement) continue;

            // 🔥 FLATTEN NESTED BLOCKS
            if (result instanceof BlockStatement nested) {
                normalized.addAll(nested.statements());
            } else {
                normalized.add(result);
            }
        }

        return new BlockStatement(normalized);
    }

    // DEAD CODE ELIMINATION (simple)
    @Override
    public AstNode visitIfStatement(IfStatement node) {

        Expression condition = (Expression) node.condition().accept(this);

        if (condition instanceof AstNode.LiteralExpression lit && lit.value() instanceof Boolean b) {
            if (b) {
                return node.thenBranch().accept(this);
            } else if (node.elseBranch() != null) {
                return node.elseBranch().accept(this);
            } else {
                return new BlockStatement(java.util.List.of());
            }
        }

        Statement thenBranch = (Statement) node.thenBranch().accept(this);
        Statement elseBranch = node.elseBranch() != null
            ? (Statement) node.elseBranch().accept(this)
            : null;

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    @Override
    public AstNode visitAssignment(AssignmentExpression node) {
        return new AssignmentExpression(
            (Expression) node.target().accept(this),
            node.operator(),
            (Expression) node.value().accept(this)
        );
    }

    @Override
    public AstNode visitFunctionCall(FunctionCallExpression node) {
        var args = node.arguments().stream()
            .map(arg -> (Expression) arg.accept(this))
            .toList();

        return new FunctionCallExpression(
            (Expression) node.callee().accept(this),
            args
        );
    }

    @Override
    public AstNode visitMemberAccess(MemberAccessExpression node) {
        return new MemberAccessExpression(
            (Expression) node.object().accept(this),
            node.property()
        );
    }

    @Override
    public AstNode visitFunctionDeclaration(FunctionDeclaration node) {
        return new FunctionDeclaration(
            node.name(),
            node.parameters(),
            (BlockStatement) node.body().accept(this)
        );
    }

    @Override
    public AstNode visitReturn(ReturnStatement node) {
        if (node.expression() == null) return node;

        return new ReturnStatement(
            (Expression) node.expression().accept(this)
        );
    }
}