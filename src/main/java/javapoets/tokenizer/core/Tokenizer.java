package javapoets.tokenizer.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Tokenizer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Tokenizer.class);

    private final LanguageDefinition languageDefinition;
    private final boolean emitWhitespace;
    private final boolean emitComments;
    private final OperatorTrie operatorTrie;
    private boolean canStartRegex = true;

    public Tokenizer(LanguageDefinition languageDefinition) {
        this(languageDefinition, false, false);
        //log.trace("(languageDefinition)");
    }

    public Tokenizer(LanguageDefinition languageDefinition, boolean emitWhitespace, boolean emitComments) {
        //log.trace("(languageDefinition, emitWhitespace, emitComments)");
        log.trace("({}, {}, {})", languageDefinition, emitWhitespace, emitComments);
        this.languageDefinition = languageDefinition;
        this.emitWhitespace = emitWhitespace;
        this.emitComments = emitComments;
        this.operatorTrie = new OperatorTrie(this.languageDefinition.operators());
    }

    public List<Token> tokenize(String input) {
        //log.trace("tokenize(input)");
        log.trace("tokenize('{}')", input);
        
        CharReader charReader = new CharReader(input);
        List<Token> tokens = new ArrayList<>();

        while (!charReader.isAtEnd()) {

            Token token = nextToken(charReader);
            //Token token = tryReadRegex(charReader);
            //log.debug("token = '{}'", token);
            //if (regex != null) return regex;
            log.info("Token: {} '{}'", token.type(), token.lexeme());
            //log.debug(token.toString());

            if (token != null) {
                if (shouldEmit(token)) {
                    tokens.add(token);
                }
            }
        }

        SourcePosition eof = charReader.position();
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

    /*
    private Token nextToken_V1(CharReader charReader) {
        //log.trace("nextToken(charReader)");

        //log.info("Reading next token at position {}", charReader.position());

        char ch = charReader.peek();

        if (Character.isWhitespace(ch)) {
            return readWhitespace(charReader);
        }

        Token comment = tryReadComment(charReader);
        if (comment != null) return comment;

        if (languageDefinition.isIdentifierStart(ch)) {
            return readIdentifierOrKeyword(charReader);
        }

        if (Character.isDigit(ch)) {
            return readNumber(charReader);
        }

        if (ch == '"' || (ch == '\'' && languageDefinition.supportsCharLiterals()) || (ch == '`' && languageDefinition.supportsBacktickStrings())) {
            return readStringLike(charReader);
        }

        Token operator = tryReadOperator(charReader);
        if (operator != null) return operator;

        if (languageDefinition.punctuation().contains(ch)) {
            return readPunctuation(charReader);
        }

        return readUnknown(charReader);
    }
    */
    private Token nextToken(CharReader charReader) {
        //log.trace("nextToken(charReader)");

        log.info("Reading next token at position {}", charReader.position());

        char ch = charReader.peek();

        // 1. whitespace
        if (Character.isWhitespace(ch)) {
            return readWhitespace(charReader);
        }

        // 2. comments
        Token comment = tryReadComment(charReader);
        if (comment != null) return comment;

        // 3. REGEX (must come BEFORE operator parsing)
        Token regex = tryReadRegex(charReader);
        if (regex != null) return regex;

        // 4. identifier / keyword
        if (languageDefinition.isIdentifierStart(ch)) {
            return readIdentifierOrKeyword(charReader);
        }

        // 5. number
        if (Character.isDigit(ch)) {
            return readNumber(charReader);
        }

        // 6. string
        if (ch == '"' || ch == '\'' || ch == '`') {
            return readStringLike(charReader);
        }

        // 7. operator (this includes "/")
        Token op = tryReadOperator(charReader);
        if (op != null) return op;

        // 8. punctuation
        if (languageDefinition.punctuation().contains(ch)) {
            return readPunctuation(charReader);
        }

        return readUnknown(charReader);
    }

    private Token readWhitespace(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();

        while (!charReader.isAtEnd() && Character.isWhitespace(charReader.peek())) {
            charReader.advance();
        }

        return new Token(
              TokenType.WHITESPACE
            , charReader.slice(startIdx, charReader.index())
            , start
            , charReader.position()
            , TokenChannel.HIDDEN
        );
    }

    private Token tryReadComment(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();

        if (languageDefinition.supportsLineComments() && charReader.startsWith("//")) {
            charReader.advance();
            charReader.advance();
            while (!charReader.isAtEnd() && charReader.peek() != '\n') {
                charReader.advance();
            }
            return new Token(
                TokenType.LINE_COMMENT,
                charReader.slice(startIdx, charReader.index()),
                start,
                charReader.position(),
                TokenChannel.HIDDEN
            );
        }

        if (languageDefinition.supportsBlockComments() && charReader.startsWith("/*")) {
            charReader.advance();
            charReader.advance();
            while (!charReader.isAtEnd() && !charReader.startsWith("*/")) {
                charReader.advance();
            }
            if (charReader.startsWith("*/")) {
                charReader.advance();
                charReader.advance();
            }
            return new Token(
                  TokenType.BLOCK_COMMENT
                , charReader.slice(startIdx, charReader.index())
                , start
                , charReader.position()
                , TokenChannel.HIDDEN
            );
        }

        if (languageDefinition.supportsHashComments() && charReader.peek() == '#') {
            charReader.advance();
            while (!charReader.isAtEnd() && charReader.peek() != '\n') {
                charReader.advance();
            }
            return new Token(
                  TokenType.LINE_COMMENT
                , charReader.slice(startIdx, charReader.index())
                , start
                , charReader.position()
                , TokenChannel.HIDDEN
            );
        }

        return null;
    }

    private Token readIdentifierOrKeyword(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();

        charReader.advance();
        while (!charReader.isAtEnd() && languageDefinition.isIdentifierPart(charReader.peek())) {
            charReader.advance();
        }

        String lexeme = charReader.slice(startIdx, charReader.index());

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

        return new Token(type, lexeme, start, charReader.position(), TokenChannel.DEFAULT);
    }

    private Token readNumber(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();
        boolean isFloat = false;

        while (Character.isDigit(charReader.peek())) {
            charReader.advance();
        }

        if (charReader.peek() == '.' && Character.isDigit(charReader.peek(1))) {
            isFloat = true;
            charReader.advance(); // dot
            while (Character.isDigit(charReader.peek())) {
                charReader.advance();
            }
        }

        if (charReader.peek() == 'e' || charReader.peek() == 'E') {
            isFloat = true;
            charReader.advance();
            if (charReader.peek() == '+' || charReader.peek() == '-') {
                charReader.advance();
            }
            while (Character.isDigit(charReader.peek())) {
                charReader.advance();
            }
        }

        return new Token(
              isFloat ? TokenType.FLOAT_LITERAL : TokenType.INTEGER_LITERAL
            , charReader.slice(startIdx, charReader.index())
            , start
            , charReader.position()
            , TokenChannel.DEFAULT
        );
    }

    private Token readStringLike(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();
        char quote = charReader.advance();

        TokenType type = switch (quote) {
            case '\'' -> TokenType.CHAR_LITERAL;
            default -> TokenType.STRING_LITERAL;
        };

        while (!charReader.isAtEnd()) {
            char ch = charReader.advance();

            if (ch == '\\') {
                if (!charReader.isAtEnd()) charReader.advance();
                continue;
            }

            if (ch == quote) {
                break;
            }
        }

        return new Token(
              type
            , charReader.slice(startIdx, charReader.index())
            , start
            , charReader.position()
            , TokenChannel.DEFAULT
        );
    }

    /*
    private Token tryReadOperator_V1(CharReader charReader) {
        SourcePosition start = charReader.position();

        for (String op : languageDefinition.operators()) {
            if (charReader.startsWith(op)) {
                int startIdx = charReader.index();
                for (int i = 0; i < op.length(); i++) {
                    charReader.advance();
                }
                return new Token(
                      TokenType.OPERATOR
                    , charReader.slice(startIdx, charReader.index())
                    , start
                    , charReader.position()
                    , TokenChannel.DEFAULT
                );
            }
        }
        return null;
    }
    */
    private Token tryReadOperator(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();

        String op = operatorTrie.match(charReader);
        if (op == null) return null;

        return new Token(
            TokenType.OPERATOR,
            charReader.slice(startIdx, charReader.index()),
            start,
            charReader.position(),
            TokenChannel.DEFAULT
        );
    }

    private Token readPunctuation(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();
        charReader.advance();

        return new Token(
              TokenType.PUNCTUATION
            , charReader.slice(startIdx, charReader.index())
            , start
            , charReader.position()
            , TokenChannel.DEFAULT
        );
    }

    private Token readUnknown(CharReader charReader) {
        SourcePosition start = charReader.position();
        int startIdx = charReader.index();
        charReader.advance();

        return new Token(
              TokenType.UNKNOWN
            , charReader.slice(startIdx, charReader.index())
            , start
            , charReader.position()
            , TokenChannel.DEFAULT
        );
    }

    private void updateRegexContext(Token token) {
        log.trace("updateRegexContext(token)");
        log.trace("updateRegexContext({})", token);
        switch (token.type()) {
            case IDENTIFIER, INTEGER_LITERAL, FLOAT_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL, NULL_LITERAL -> canStartRegex = false;
            case PUNCTUATION -> {
                String lex = token.lexeme();
                canStartRegex = lex.equals("(") || lex.equals("{") || lex.equals("[") || lex.equals(",");
            }
            case OPERATOR -> canStartRegex = true;
            case KEYWORD -> canStartRegex = true;
            default -> canStartRegex = true;
        }
    }

    private Token tryReadRegex(CharReader charReader) {
        //log.trace("tryReadRegex(charReader)");
        //log.trace("tryReadRegex({})", charReader);

        if (!canStartRegex || charReader.peek() != '/') return null;

        SourcePosition start = charReader.position();
        int startIdx = charReader.index();

        charReader.advance(); // consume '/'

        boolean inEscape = false;

        while (!charReader.isAtEnd()) {
            char c = charReader.advance();

            if (inEscape) {
                inEscape = false;
                continue;
            }

            if (c == '\\') {
                inEscape = true;
                continue;
            }

            if (c == '/') {
                break;
            }
        }

        // flags
        while (Character.isLetter(charReader.peek())) {
            charReader.advance();
        }

        return new Token(
            TokenType.STRING_LITERAL, // or REGEX_LITERAL if you add it
            charReader.slice(startIdx, charReader.index()),
            start,
            charReader.position(),
            TokenChannel.DEFAULT
        );
    }

}