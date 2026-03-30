package javapoets.tokenizer.ast;

import java.util.List;

public record FunctionDeclaration(
    String name,
    List<String> parameters,
    BlockStatement body
) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }
}