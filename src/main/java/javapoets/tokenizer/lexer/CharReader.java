package javapoets.tokenizer.lexer;

public final class CharReader {
    
    private final String input;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public CharReader(String input) {
        this.input = input == null ? "" : input;
    }

    public boolean isAtEnd() {
        return index >= input.length();
    }

    public char peek() {
        return peek(0);
    }

    public char peek(int offset) {
        int target = index + offset;
        if (target >= input.length()) return '\0';
        return input.charAt(target);
    }

    public boolean startsWith(String s) {
        if (s == null) return false;
        return input.startsWith(s, index);
    }

    public char advance() {
        if (isAtEnd()) return '\0';

        char ch = input.charAt(index++);
        if (ch == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return ch;
    }

    public SourcePosition position() {
        return new SourcePosition(index, line, column);
    }

    public String slice(int start, int end) {
        return input.substring(start, end);
    }

    public int index() {
        return index;
    }
}