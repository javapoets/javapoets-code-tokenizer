package javapoets.tokenizer.ast;

public interface AstVisitor<R> {

    // Expressions
    R visitLiteral(AstNode.LiteralExpression node);
    R visitIdentifier(AstNode.IdentifierExpression node);
    R visitBinary(AstNode.BinaryExpression node);
    R visitFunctionCall(FunctionCallExpression node);
    R visitMemberAccess(MemberAccessExpression node);
    R visitAssignment(AssignmentExpression node);

    // Statements
    R visitVariableDeclaration(VariableDeclaration node);
    R visitExpressionStatement(ExpressionStatement node);
    R visitBlockStatement(BlockStatement node);

    R visitFunctionDeclaration(FunctionDeclaration node);
    R visitReturn(ReturnStatement node);
    R visitIfStatement(IfStatement node);

    R visitBooleanLiteralExpression(BooleanLiteralExpression expr);
    R visitEmptyStatement(EmptyStatement stmt);
}