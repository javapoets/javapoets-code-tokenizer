package javapoets.tokenizer.ast;

public record ReturnStatement(Expression expression) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitReturn(this);
    }
}