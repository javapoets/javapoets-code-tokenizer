package javapoets.tokenizer.core;

public record Token(
      TokenType type
    , String lexeme
    , SourcePosition start
    , SourcePosition end
    , TokenChannel channel
) {}