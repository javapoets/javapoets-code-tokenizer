package javapoets.tokenizer.ast;

public class EmptyStatement implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitEmptyStatement(this);
    }

    @Override
    public String toString() {
        return "EmptyStatement";
    }
}