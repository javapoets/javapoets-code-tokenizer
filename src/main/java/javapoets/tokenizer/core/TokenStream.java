package javapoets.tokenizer.core;

import java.util.List;

public final class TokenStream {

    private final List<Token> tokens;
    private int position = 0;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token peek() {
        return peek(0);
    }

    public Token peek(int offset) {
        int index = position + offset;
        if (index >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // EOF
        }
        return tokens.get(index);
    }

    public boolean match(TokenType type) {
        if (peek().type() == type) {
            consume();
            return true;
        }
        return false;
    }

    public boolean match(TokenType type, String lexeme) {
        Token t = peek();
        if (t.type() == type && t.lexeme().equals(lexeme)) {
            consume();
            return true;
        }
        return false;
    }

    public Token consume() {
        return tokens.get(position++);
    }

    public Token expect(TokenType type) {
        Token t = peek();
        if (t.type() != type) {
            throw new RuntimeException(
                "Expected " + type + " but found " + t.type() + " (" + t.lexeme() + ")"
            );
        }
        return consume();
    }

    public boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    public int position() {
        return position;
    }
}