package javapoets.tokenizer.ast;

public record MemberAccessExpression(
    Expression object,
    String property
) implements Expression {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitMemberAccess(this);
    }
}