package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.*;

public class AlgebraicSimplificationVisitor implements AstVisitor<AstNode> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlgebraicSimplificationVisitor.class);

    @Override
    public AstNode visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return expr;
    }

    @Override
    public AstNode visitEmptyStatement(EmptyStatement stmt) {
        return stmt;
    }
    
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

        if (right instanceof AstNode.LiteralExpression r) {
            if ("+".equals(node.operator()) && isZero(r.value())) return left;
            if ("-".equals(node.operator()) && isZero(r.value())) return left;
            if ("*".equals(node.operator()) && isOne(r.value())) return left;
            if ("*".equals(node.operator()) && isZero(r.value())) return new AstNode.LiteralExpression(0);
        }

        if (left instanceof AstNode.LiteralExpression l) {
            if ("+".equals(node.operator()) && isZero(l.value())) return right;
            if ("*".equals(node.operator()) && isOne(l.value())) return right;
            if ("*".equals(node.operator()) && isZero(l.value())) return new AstNode.LiteralExpression(0);
        }

        return new AstNode.BinaryExpression(left, node.operator(), right);
    }

    private boolean isZero(Object value) {
        return value instanceof Number n && n.doubleValue() == 0.0;
    }

    private boolean isOne(Object value) {
        return value instanceof Number n && n.doubleValue() == 1.0;
    }

    @Override
    public AstNode visitFunctionCall(FunctionCallExpression node) {
        return new FunctionCallExpression(
            (Expression) node.callee().accept(this),
            node.arguments().stream().map(arg -> (Expression) arg.accept(this)).toList()
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
    public AstNode visitAssignment(AssignmentExpression node) {
        return new AssignmentExpression(
            (Expression) node.target().accept(this),
            node.operator(),
            (Expression) node.value().accept(this)
        );
    }

    @Override
    public AstNode visitVariableDeclaration(VariableDeclaration node) {
        log.trace("visitVariableDeclaration(VariableDeclaration node)");
        return new VariableDeclaration(
            node.keyword(),
            node.name(),
            node.initializer() == null ? null : (Expression) node.initializer().accept(this)
        );
    }

    @Override
    public AstNode visitExpressionStatement(ExpressionStatement node) {
        return new ExpressionStatement((Expression) node.expression().accept(this));
    }

    @Override
    public AstNode visitBlockStatement(BlockStatement node) {
        return new BlockStatement(
            node.statements().stream()
                .map(stmt -> (Statement) stmt.accept(this))
                .toList()
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
        return node.expression() == null
            ? node
            : new ReturnStatement((Expression) node.expression().accept(this));
    }

    @Override
    public AstNode visitIfStatement(IfStatement node) {
        return new IfStatement(
            (Expression) node.condition().accept(this),
            (Statement) node.thenBranch().accept(this),
            node.elseBranch() == null ? null : (Statement) node.elseBranch().accept(this)
        );
    }
}