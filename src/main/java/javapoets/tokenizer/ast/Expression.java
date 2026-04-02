package javapoets.tokenizer.ast;

public interface Expression extends AstNode {
    <R> R accept(AstVisitor<R> visitor);
}