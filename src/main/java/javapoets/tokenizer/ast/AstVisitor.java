package javapoets.tokenizer.ast;

public interface AstVisitor<R> {

    // Expressions
    //R visitLiteral(LiteralExpression node);
    R visitLiteral(AstNode.LiteralExpression node);

    //R visitIdentifier(IdentifierExpression node);
    R visitIdentifier(AstNode.IdentifierExpression node);

    //R visitBinary(BinaryExpression node);
    R visitBinary(AstNode.BinaryExpression node);

    // Statements
    R visitVariableDeclaration(VariableDeclaration node);
    R visitExpressionStatement(ExpressionStatement node);
    R visitBlock(BlockStatement node);

    R visitFunctionCall(FunctionCallExpression node);
    R visitMemberAccess(MemberAccessExpression node);

    R visitAssignment(AssignmentExpression node);

    R visitFunctionDeclaration(FunctionDeclaration node);
    R visitReturn(ReturnStatement node);

    R visitIf(IfStatement node);
}