package javapoets.tokenizer.visitor;

import javapoets.tokenizer.ast.*;

public class PrettyPrinterVisitor implements AstVisitor<String> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PrettyPrinterVisitor.class);

    private int indentLevel = 0;

    @Override
    public String visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return String.valueOf(expr.value());
    }

    @Override
    public String visitEmptyStatement(EmptyStatement stmt) {
        return "";
    }
    
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
        log.trace("visitVariableDeclaration(VariableDeclaration node)");

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
    public String visitBlockStatement(BlockStatement node) {

        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("{\n");
        indentLevel++;

        for (Statement stmt : node.statements()) {
            sb.append(stmt.accept(this));
            /*
            Statement result = (Statement) stmt.accept(this);
            if (result instanceof EmptyStatement) continue; // Remove empty statements here
            sb.append(result);
            */
        }

        indentLevel--;
        sb.append(indent()).append("}\n");

        return sb.toString();
    }

    @Override
    public String visitAssignment(AssignmentExpression node) {
        return node.target().accept(this)
            + " " + node.operator() + " "
            + node.value().accept(this);
    }

    @Override
    public String visitIfStatement(IfStatement node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent())
          .append("if (")
          .append(node.condition().accept(this))
          .append(") ");

        sb.append(node.thenBranch().accept(this));

        if (node.elseBranch() != null) {
            sb.append(indent())
              .append("else ")
              .append(node.elseBranch().accept(this));
        }

        return sb.toString();
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent())
          .append("function ")
          .append(node.name())
          .append("(");

        sb.append(String.join(", ", node.parameters()));
        sb.append(") ");

        sb.append(node.body().accept(this));

        return sb.toString();
    }

    @Override
    public String visitReturn(ReturnStatement node) {
        if (node.expression() == null) {
            return indent() + "return;\n";
        }

        return indent() + "return " + node.expression().accept(this) + ";\n";
    }

    @Override
    public String visitFunctionCall(FunctionCallExpression node) {
        String args = node.arguments().stream()
            .map(arg -> arg.accept(this))
            .reduce((a, b) -> a + ", " + b)
            .orElse("");

        return node.callee().accept(this) + "(" + args + ")";
    }

    @Override
    public String visitMemberAccess(MemberAccessExpression node) {
        return node.object().accept(this) + "." + node.property();
    }
}