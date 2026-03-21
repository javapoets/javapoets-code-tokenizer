package javapoets.tokenizer.ast;

import java.util.List;

public record BlockStatement(List<Statement> statements) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBlock(this);
    }
}