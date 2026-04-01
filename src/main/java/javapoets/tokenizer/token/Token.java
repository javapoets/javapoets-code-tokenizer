package javapoets.tokenizer.token;

import javapoets.tokenizer.lexer.*;

public record Token(
      TokenType type
    , String lexeme
    , SourcePosition start
    , SourcePosition end
    , TokenChannel channel
) {

    /*
    public String toStringSimple() {
        return new StringBuilder()
            .append(getClass().getSimpleName())
            .append("(")
            .append(type())
            .append(")")
            .append("{ ")
            .append(lexeme())
            .append(" }")
            .toString();
    */

    /*
    public String toString() {
        return String.format(
            "%-18s %-12s (%d:%d)",
            type(),
            lexeme(),
            start().line(),
            start().column()
        );
    }
    //*/
    public String toString() {
        return String.format(
            "%s{%-18s %-12s (%d:%d)}"
            , getClass().getSimpleName()
            , type()
            , lexeme()
            , start().line()
            , start().column()
        );
    }

}