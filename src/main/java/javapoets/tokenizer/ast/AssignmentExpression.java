package javapoets.tokenizer.ast;

public record AssignmentExpression(
    Expression target,
    String operator,
    Expression value
) implements Expression {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitAssignment(this);
    }
}