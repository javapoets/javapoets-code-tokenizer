package javapoets.tokenizer.visitor;

import javapoets.tokenizer.ast.*;

public class AstPrinterVisitor implements AstVisitor<String> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AstPrinterVisitor.class);

    private int indentLevel = 0;

    @Override
    public String visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return "(bool " + expr.value() + ")";
    }

    @Override
    public String visitEmptyStatement(EmptyStatement stmt) {
        return "";
    }
    
    private String indent() {
        return "  ".repeat(indentLevel);
    }

    private String line(String text) {
        return indent() + text + "\n";
    }

    // -------------------
    // EXPRESSIONS
    // -------------------

    @Override
    public String visitLiteral(AstNode.LiteralExpression node) {
        return line("Literal(" + node.value() + ")");
    }

    @Override
    public String visitIdentifier(AstNode.IdentifierExpression node) {
        return line("Identifier(" + node.name() + ")");
    }

    @Override
    public String visitBinary(AstNode.BinaryExpression node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("BinaryExpression (" + node.operator() + ")"));

        indentLevel++;
        sb.append(node.left().accept(this));
        sb.append(node.right().accept(this));
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitFunctionCall(FunctionCallExpression node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("FunctionCall"));

        indentLevel++;
        sb.append(line("Callee:"));
        indentLevel++;
        sb.append(node.callee().accept(this));
        indentLevel--;

        sb.append(line("Arguments:"));
        indentLevel++;
        for (Expression arg : node.arguments()) {
            sb.append(arg.accept(this));
        }
        indentLevel--;

        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitMemberAccess(MemberAccessExpression node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("MemberAccess (" + node.property() + ")"));

        indentLevel++;
        sb.append(node.object().accept(this));
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitAssignment(AssignmentExpression node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("Assignment (" + node.operator() + ")"));

        indentLevel++;
        sb.append(line("Target:"));
        indentLevel++;
        sb.append(node.target().accept(this));
        indentLevel--;

        sb.append(line("Value:"));
        indentLevel++;
        sb.append(node.value().accept(this));
        indentLevel--;

        indentLevel--;

        return sb.toString();
    }

    // -------------------
    // STATEMENTS
    // -------------------

    @Override
    public String visitVariableDeclaration(VariableDeclaration node) {
        log.trace("visitVariableDeclaration(VariableDeclaration node)");

        StringBuilder sb = new StringBuilder();
        sb.append(line("VariableDeclaration (" + node.keyword() + " " + node.name() + ")"));
        if (node.initializer() != null) {
            indentLevel++;
            sb.append(line("Initializer:"));
            indentLevel++;
            sb.append(node.initializer().accept(this));
            indentLevel -= 2;
        }
        return sb.toString();
    }

    @Override
    public String visitExpressionStatement(ExpressionStatement node) {
        log.trace("visitExpressionStatement(ExpressionStatement node)");

        StringBuilder sb = new StringBuilder();
        sb.append(line("ExpressionStatement"));

        indentLevel++;
        sb.append(node.expression().accept(this));
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitBlockStatement(BlockStatement node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("Block"));

        indentLevel++;
        for (Statement stmt : node.statements()) {
            sb.append(stmt.accept(this));
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("FunctionDeclaration (" + node.name() + ")"));

        indentLevel++;
        sb.append(line("Parameters: " + node.parameters()));

        sb.append(line("Body:"));
        indentLevel++;
        sb.append(node.body().accept(this));
        indentLevel -= 2;

        return sb.toString();
    }

    @Override
    public String visitReturn(ReturnStatement node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("Return"));

        if (node.expression() != null) {
            indentLevel++;
            sb.append(node.expression().accept(this));
            indentLevel--;
        }

        return sb.toString();
    }

    @Override
    public String visitIfStatement(IfStatement node) {
        StringBuilder sb = new StringBuilder();

        sb.append(line("IfStatement"));

        indentLevel++;

        sb.append(line("Condition:"));
        indentLevel++;
        sb.append(node.condition().accept(this));
        indentLevel--;

        sb.append(line("Then:"));
        indentLevel++;
        sb.append(node.thenBranch().accept(this));
        indentLevel--;

        if (node.elseBranch() != null) {
            sb.append(line("Else:"));
            indentLevel++;
            sb.append(node.elseBranch().accept(this));
            indentLevel--;
        }

        indentLevel--;

        return sb.toString();
    }
}