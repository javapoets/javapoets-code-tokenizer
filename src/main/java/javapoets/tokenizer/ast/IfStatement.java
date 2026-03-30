package javapoets.tokenizer.ast;

public record IfStatement(
    Expression condition,
    Statement thenBranch,
    Statement elseBranch // nullable
) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitIf(this);
    }
}