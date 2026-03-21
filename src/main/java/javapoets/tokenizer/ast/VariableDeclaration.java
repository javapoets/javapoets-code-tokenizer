package javapoets.tokenizer.ast;

public record VariableDeclaration(
    String keyword, // int, let, const, var
    String name,
    Expression initializer
) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
}