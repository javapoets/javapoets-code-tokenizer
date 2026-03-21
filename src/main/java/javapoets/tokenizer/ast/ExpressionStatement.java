package javapoets.tokenizer.ast;

public record ExpressionStatement(Expression expression) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}