package javapoets.tokenizer.ast;

import java.util.List;

/*
public record FunctionCallExpression(Expression callee, List<Expression> arguments) implements Expression {
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitFunctionCall(this);
    }
}
*/
public class BooleanLiteralExpression implements Expression {
    private final boolean value;

    public BooleanLiteralExpression(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    //public <R> R accept(ExpressionVisitor<R> visitor) {
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBooleanLiteralExpression(this);
    }
}