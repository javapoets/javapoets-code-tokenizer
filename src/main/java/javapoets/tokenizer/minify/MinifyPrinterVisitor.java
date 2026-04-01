package javapoets.tokenizer.minify;

import javapoets.tokenizer.ast.*;

public class MinifyPrinterVisitor implements AstVisitor<String> {

    @Override
    public String visitLiteral(AstNode.LiteralExpression node) {
        return String.valueOf(node.value());
    }

    @Override
    public String visitIdentifier(AstNode.IdentifierExpression node) {
        return node.name();
    }

    @Override
    public String visitBinary(AstNode.BinaryExpression node) {
        return "(" + node.left().accept(this) + node.operator() + node.right().accept(this) + ")";
    }

    @Override
    public String visitFunctionCall(FunctionCallExpression node) {
        String args = node.arguments().stream()
            .map(arg -> arg.accept(this))
            .reduce((a, b) -> a + "," + b)
            .orElse("");

        return node.callee().accept(this) + "(" + args + ")";
    }

    @Override
    public String visitMemberAccess(MemberAccessExpression node) {
        return node.object().accept(this) + "." + node.property();
    }

    @Override
    public String visitAssignment(AssignmentExpression node) {
        return node.target().accept(this) + node.operator() + node.value().accept(this);
    }

    @Override
    public String visitVariableDeclaration(VariableDeclaration node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.keyword()).append(" ").append(node.name());
        if (node.initializer() != null) {
            sb.append("=").append(node.initializer().accept(this));
        }
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitExpressionStatement(ExpressionStatement node) {
        return node.expression().accept(this) + ";";
    }

    @Override
    public String visitBlock(BlockStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Statement stmt : node.statements()) {
            sb.append(stmt.accept(this));
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        return "function " + node.name()
            + "(" + String.join(",", node.parameters()) + ")"
            + node.body().accept(this);
    }

    @Override
    public String visitReturn(ReturnStatement node) {
        return node.expression() == null
            ? "return;"
            : "return " + node.expression().accept(this) + ";";
    }

    @Override
    public String visitIf(IfStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("if(").append(node.condition().accept(this)).append(")");
        sb.append(node.thenBranch().accept(this));
        if (node.elseBranch() != null) {
            sb.append("else").append(node.elseBranch().accept(this));
        }
        return sb.toString();
    }
}