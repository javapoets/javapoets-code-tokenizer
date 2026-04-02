package javapoets.tokenizer.token;

public enum TokenType {
    
      KEYWORD
    , IDENTIFIER

    , INTEGER_LITERAL
    , FLOAT_LITERAL
    , STRING_LITERAL
    //, TEMPLATE_LITERAL
    , CHAR_LITERAL
    , BOOLEAN_LITERAL
    , NULL_LITERAL

    , OPERATOR
    , PUNCTUATION

    , LINE_COMMENT
    , BLOCK_COMMENT
    , WHITESPACE

    , UNKNOWN
    , EOF
}