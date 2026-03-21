package javapoets.tokenizer.visitor;

import javapoets.tokenizer.ast.*;

public class PrettyPrinterVisitor implements AstVisitor<String> {

    private int indentLevel = 0;

    private String indent() {
        return "  ".repeat(indentLevel);
    }

    // -------------------
    // EXPRESSIONS
    // -------------------

    @Override
    //public String visitLiteral(LiteralExpression node) {
    public String visitLiteral(AstNode.LiteralExpression node) {
        return node.value().toString();
    }

    @Override
    //public String visitIdentifier(IdentifierExpression node) {
    public String visitIdentifier(AstNode.IdentifierExpression node) {
        return node.name();
    }

    @Override
    //public String visitBinary(BinaryExpression node) {
    public String visitBinary(AstNode.BinaryExpression node) {
        return "(" +
            node.left().accept(this) +
            " " + node.operator() + " " +
            node.right().accept(this) +
            ")";
    }

    // -------------------
    // STATEMENTS
    // -------------------

    @Override
    public String visitVariableDeclaration(VariableDeclaration node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent())
          .append(node.keyword())
          .append(" ")
          .append(node.name());

        if (node.initializer() != null) {
            sb.append(" = ")
              .append(node.initializer().accept(this));
        }

        sb.append(";\n");

        return sb.toString();
    }

    @Override
    public String visitExpressionStatement(ExpressionStatement node) {
        return indent() + node.expression().accept(this) + ";\n";
    }

    @Override
    public String visitBlock(BlockStatement node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("{\n");
        indentLevel++;

        for (Statement stmt : node.statements()) {
            sb.append(stmt.accept(this));
        }

        indentLevel--;
        sb.append(indent()).append("}\n");

        return sb.toString();
    }
}