package javapoets.tokenizer.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Tokenizer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Tokenizer.class);

    private final LanguageDefinition languageDefinition;
    private final boolean emitWhitespace;
    private final boolean emitComments;

    public Tokenizer(LanguageDefinition languageDefinition) {
        this(languageDefinition, false, false);
        log.trace("(languageDefinition)");
    }

    public Tokenizer(LanguageDefinition languageDefinition, boolean emitWhitespace, boolean emitComments) {
        log.trace("(languageDefinition, emitWhitespace, emitComments)");
        this.languageDefinition = languageDefinition;
        this.emitWhitespace = emitWhitespace;
        this.emitComments = emitComments;
    }

    public List<Token> tokenize(String input) {
        log.trace("tokenize(input)");
        CharReader reader = new CharReader(input);
        List<Token> tokens = new ArrayList<>();

        while (!reader.isAtEnd()) {
            Token token = nextToken(reader);
            log.debug("Token: {} '{}'", token.type(), token.lexeme());
            if (token != null) {
                if (shouldEmit(token)) {
                    tokens.add(token);
                }
            }
        }

        SourcePosition eof = reader.position();
        tokens.add(new Token(TokenType.EOF, "", eof, eof, TokenChannel.DEFAULT));
        return tokens;
    }

    private boolean shouldEmit(Token token) {
        return switch (token.type()) {
            case WHITESPACE -> emitWhitespace;
            case LINE_COMMENT, BLOCK_COMMENT -> emitComments;
            default -> true;
        };
    }

    private Token nextToken(CharReader reader) {
        log.trace("nextToken(reader)");

        log.debug("Reading next token at position {}", reader.position());

        char ch = reader.peek();

        if (Character.isWhitespace(ch)) {
            return readWhitespace(reader);
        }

        Token comment = tryReadComment(reader);
        if (comment != null) return comment;

        if (languageDefinition.isIdentifierStart(ch)) {
            return readIdentifierOrKeyword(reader);
        }

        if (Character.isDigit(ch)) {
            return readNumber(reader);
        }

        if (ch == '"' || (ch == '\'' && languageDefinition.supportsCharLiterals()) || (ch == '`' && languageDefinition.supportsBacktickStrings())) {
            return readStringLike(reader);
        }

        Token operator = tryReadOperator(reader);
        if (operator != null) return operator;

        if (languageDefinition.punctuation().contains(ch)) {
            return readPunctuation(reader);
        }

        return readUnknown(reader);
    }

    private Token readWhitespace(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();

        while (!reader.isAtEnd() && Character.isWhitespace(reader.peek())) {
            reader.advance();
        }

        return new Token(
              TokenType.WHITESPACE
            , reader.slice(startIdx, reader.index())
            , start
            , reader.position()
            , TokenChannel.HIDDEN
        );
    }

    private Token tryReadComment(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();

        if (languageDefinition.supportsLineComments() && reader.startsWith("//")) {
            reader.advance();
            reader.advance();
            while (!reader.isAtEnd() && reader.peek() != '\n') {
                reader.advance();
            }
            return new Token(
                TokenType.LINE_COMMENT,
                reader.slice(startIdx, reader.index()),
                start,
                reader.position(),
                TokenChannel.HIDDEN
            );
        }

        if (languageDefinition.supportsBlockComments() && reader.startsWith("/*")) {
            reader.advance();
            reader.advance();
            while (!reader.isAtEnd() && !reader.startsWith("*/")) {
                reader.advance();
            }
            if (reader.startsWith("*/")) {
                reader.advance();
                reader.advance();
            }
            return new Token(
                  TokenType.BLOCK_COMMENT
                , reader.slice(startIdx, reader.index())
                , start
                , reader.position()
                , TokenChannel.HIDDEN
            );
        }

        if (languageDefinition.supportsHashComments() && reader.peek() == '#') {
            reader.advance();
            while (!reader.isAtEnd() && reader.peek() != '\n') {
                reader.advance();
            }
            return new Token(
                  TokenType.LINE_COMMENT
                , reader.slice(startIdx, reader.index())
                , start
                , reader.position()
                , TokenChannel.HIDDEN
            );
        }

        return null;
    }

    private Token readIdentifierOrKeyword(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();

        reader.advance();
        while (!reader.isAtEnd() && languageDefinition.isIdentifierPart(reader.peek())) {
            reader.advance();
        }

        String lexeme = reader.slice(startIdx, reader.index());

        TokenType type;
        if (languageDefinition.keywords().contains(lexeme)) {
            type = TokenType.KEYWORD;
        } else if (languageDefinition.booleanLiterals().contains(lexeme)) {
            type = TokenType.BOOLEAN_LITERAL;
        } else if (languageDefinition.nullLiterals().contains(lexeme)) {
            type = TokenType.NULL_LITERAL;
        } else {
            type = TokenType.IDENTIFIER;
        }

        return new Token(type, lexeme, start, reader.position(), TokenChannel.DEFAULT);
    }

    private Token readNumber(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();
        boolean isFloat = false;

        while (Character.isDigit(reader.peek())) {
            reader.advance();
        }

        if (reader.peek() == '.' && Character.isDigit(reader.peek(1))) {
            isFloat = true;
            reader.advance(); // dot
            while (Character.isDigit(reader.peek())) {
                reader.advance();
            }
        }

        if (reader.peek() == 'e' || reader.peek() == 'E') {
            isFloat = true;
            reader.advance();
            if (reader.peek() == '+' || reader.peek() == '-') {
                reader.advance();
            }
            while (Character.isDigit(reader.peek())) {
                reader.advance();
            }
        }

        return new Token(
              isFloat ? TokenType.FLOAT_LITERAL : TokenType.INTEGER_LITERAL
            , reader.slice(startIdx, reader.index())
            , start
            , reader.position()
            , TokenChannel.DEFAULT
        );
    }

    private Token readStringLike(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();
        char quote = reader.advance();

        TokenType type = switch (quote) {
            case '\'' -> TokenType.CHAR_LITERAL;
            default -> TokenType.STRING_LITERAL;
        };

        while (!reader.isAtEnd()) {
            char ch = reader.advance();

            if (ch == '\\') {
                if (!reader.isAtEnd()) reader.advance();
                continue;
            }

            if (ch == quote) {
                break;
            }
        }

        return new Token(
              type
            , reader.slice(startIdx, reader.index())
            , start
            , reader.position()
            , TokenChannel.DEFAULT
        );
    }

    private Token tryReadOperator(CharReader reader) {
        SourcePosition start = reader.position();

        for (String op : languageDefinition.operators()) {
            if (reader.startsWith(op)) {
                int startIdx = reader.index();
                for (int i = 0; i < op.length(); i++) {
                    reader.advance();
                }
                return new Token(
                      TokenType.OPERATOR
                    , reader.slice(startIdx, reader.index())
                    , start
                    , reader.position()
                    , TokenChannel.DEFAULT
                );
            }
        }

        return null;
    }

    private Token readPunctuation(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();
        reader.advance();

        return new Token(
              TokenType.PUNCTUATION
            , reader.slice(startIdx, reader.index())
            , start
            , reader.position()
            , TokenChannel.DEFAULT
        );
    }

    private Token readUnknown(CharReader reader) {
        SourcePosition start = reader.position();
        int startIdx = reader.index();
        reader.advance();

        return new Token(
              TokenType.UNKNOWN
            , reader.slice(startIdx, reader.index())
            , start
            , reader.position()
            , TokenChannel.DEFAULT
        );
    }
}