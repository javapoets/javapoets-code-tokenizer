package javapoets.tokenizer.ast;

import java.util.List;

public record FunctionCallExpression(
    Expression callee,
    List<Expression> arguments
) implements Expression {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitFunctionCall(this);
    }
}