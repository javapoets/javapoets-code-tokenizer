package javapoets.tokenizer.lexer;

public record SourcePosition(
      int index
    , int line
    , int column
) {}