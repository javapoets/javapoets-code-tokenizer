package javapoets.tokenizer.core;

public record SourcePosition(
      int index
    , int line
    , int column
) {}