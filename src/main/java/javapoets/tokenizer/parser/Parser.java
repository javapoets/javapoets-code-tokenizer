package javapoets.tokenizer.parser;

import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.token.*;
import javapoets.tokenizer.stream.TokenStream;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Parser.class);

    private final TokenStream tokenStream;

    public Parser(TokenStream tokenStream) {
        log.trace("(tokenStream)");
        this.tokenStream = tokenStream;
    }

    private Token peek() {
        return tokenStream.peek(); // must return EOF token at end
    }

    private Token previous() {
        return tokenStream.previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token advance() {
        if (!isAtEnd()) {
            tokenStream.advance();
        }
        return previous();
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private boolean check(TokenType type, String lexeme) {
        if (isAtEnd()) return false;
        Token token = peek();
        return token.type() == type && token.lexeme().equals(lexeme);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean match(TokenType type, String lexeme) {
        if (check(type, lexeme)) {
            advance();
            return true;
        }
        return false;
    }

    private Token consume() {
        return tokenStream.consume();
    }

    private Token consume(TokenType tokenType, String message) {
        //log.debug("consume(tokenType, message)");
        log.debug("consume({}, '{}')", tokenType, message);

        if (check(tokenType)) return advance();

        throw error(peek(), message);
    }

    private Token consume(TokenType tokenType, String lexeme, String message) {
        log.debug("consume({}, {}, '{}')", lexeme, tokenType, message);

        if (check(tokenType, lexeme)) return advance();

        throw error(peek(), message + " Expected '" + lexeme + "'");
    }

    private RuntimeException error(Token token, String message) {
        return new RuntimeException(
            "[Parse Error] " + message +
            " at " + token.position() +
            " near '" + token.lexeme() + "'"
        );
    }

    // ENTRY POINT
    public List<Statement> parseProgram() {
        log.trace("parseProgram()");
        List<Statement> statements = new ArrayList<>();

        //while (!tokenStream.isAtEnd()) {
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }

        return statements;
    }

    private Expression parsePrimary() {
        log.trace("parsePrimary()");

        Expression expr = parseAtom();

        while (true) {
            //if (tokenStream.match(TokenType.PUNCTUATION, "(")) {
            if (match(TokenType.PUNCTUATION, "(")) {
                expr = finishFunctionCall(expr);
            //} else if (tokenStream.match(TokenType.PUNCTUATION, ".")) {
            } else if (match(TokenType.PUNCTUATION, ".")) {
                String property = tokenStream.expect(TokenType.IDENTIFIER).lexeme();
                //String property = expect(TokenType.IDENTIFIER).lexeme();
                expr = new MemberAccessExpression(expr, property);
            } else {
                break;
            }
        }

        return expr;
    }
    /*
    private Expression parsePrimary() {
        Token t = tokenStream.peek();

        switch (t.type()) {
            case INTEGER_LITERAL -> {
                tokenStream.consume();
                //return new LiteralExpression(Integer.parseInt(t.lexeme()));
                return new AstNode.LiteralExpression(Integer.parseInt(t.lexeme()));
            }
            case FLOAT_LITERAL -> {
                tokenStream.consume();
                //return new LiteralExpression(Double.parseDouble(t.lexeme()));
                return new AstNode.LiteralExpression(Double.parseDouble(t.lexeme()));
            }
            case STRING_LITERAL -> {
                tokenStream.consume();
                //return new LiteralExpression(t.lexeme());
                return new AstNode.LiteralExpression(t.lexeme());
            }
            case IDENTIFIER -> {
                tokenStream.consume();
                //return new IdentifierExpression(t.lexeme());
                return new AstNode.IdentifierExpression(t.lexeme());
            }
            case PUNCTUATION -> {
                if (tokenStream.match(TokenType.PUNCTUATION, "(")) {
                    Expression expr = parseExpression();
                    tokenStream.expect(TokenType.PUNCTUATION); // )
                    return expr;
                }
            }
        }
        throw new RuntimeException("Unexpected token: " + t);
    }
    */

    private Expression parseAtom() {
        log.trace("parseAtom()");

        //Token token = tokenStream.peek();
        Token token = peek();

        // Defensive guard - parseAtom() should NEVER see }
        if (check(TokenType.PUNCTUATION, "}")) {
            throw error(peek(), "Unexpected block terminator");
        }

        // BOOLEAN LITERAL
        if (match(TokenType.BOOLEAN_LITERAL)) {
            return new BooleanLiteralExpression(
                //Boolean.parseBoolean(token.lexeme()) // subtle bug: match() advances but we're using the token from peek()
                Boolean.parseBoolean(tokenStream.previous().lexeme())
            );
        }

        switch (token.type()) {
            case INTEGER_LITERAL -> {
                consume();
                return new AstNode.LiteralExpression(Integer.parseInt(token.lexeme()));
            }
            case FLOAT_LITERAL -> {
                consume();
                return new AstNode.LiteralExpression(Double.parseDouble(token.lexeme()));
            }
            case STRING_LITERAL -> {
                consume();
                return new AstNode.LiteralExpression(token.lexeme());
            }
            case IDENTIFIER -> {
                consume();
                return new AstNode.IdentifierExpression(token.lexeme());
            }
            case PUNCTUATION -> {
                if (match(TokenType.PUNCTUATION, "(")) {
                    Expression expr = parseExpression();
                    tokenStream.expect(TokenType.PUNCTUATION, ")");
                    //expect(TokenType.PUNCTUATION, ")");
                    return expr;
                }
            }
        }

        throw new RuntimeException("Unexpected token: " + token);
    }

    private Expression finishFunctionCall(Expression callee) {
        List<Expression> args = new ArrayList<>();

        if (!match(TokenType.PUNCTUATION, ")")) {
            do {
                args.add(parseExpression());
            } while (match(TokenType.PUNCTUATION, ","));

            tokenStream.expect(TokenType.PUNCTUATION, ")");
            //expect(TokenType.PUNCTUATION, ")");
        }

        return new FunctionCallExpression(callee, args);
    }

    // -------------------------
    // STATEMENTS
    // -------------------------

    private Statement parseStatement() {
        log.trace("parseStatement()");

        //Token token = tokenStream.peek();
        Token token = peek();
        log.debug("Parsing statement starting with: {}", token);
        log.trace("token.type() = " + token.type());

        log.trace("check(TokenType.KEYWORD, \"if\") = " + (check(TokenType.KEYWORD, "if")));

        if (check(TokenType.KEYWORD, "if")) {
            log.trace("check(TokenType.KEYWORD, \"if\") = true");
            advance(); // consume the 'if'
            return parseIfStatement();
        }

        if (check(TokenType.KEYWORD, "else")) {
            throw error(peek(), "Unexpected 'else' without matching 'if'");
        }

        if (token.type() == TokenType.KEYWORD) {
            String keyword = token.lexeme();
            if (keyword.equals("int") || keyword.equals("let") || keyword.equals("const") || keyword.equals("var")) {
                return parseVariableDeclaration();
            }
        }

        if (match(TokenType.PUNCTUATION, "{")) {
            return parseBlock();
        }

        return parseExpressionStatement();
    }

    /*
    private Statement parseIfStatement() {

        consume(TokenType.PUNCTUATION, "(");

        Expression condition = parseExpression();

        consume(TokenType.PUNCTUATION, ")");

        Statement thenBranch = parseStatement();

        Statement elseBranch = null;
        if (match(TokenType.KEYWORD, "else")) {
        //if (tokenStream.match(TokenType.KEYWORD, "else")) {
            elseBranch = parseStatement();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }
    */
    private Statement parseIfStatement() {
        consume(TokenType.PUNCTUATION, "(", "Expected '(' after 'if'");

        Expression condition = parseExpression();

        consume(TokenType.PUNCTUATION, ")", "Expected ')' after condition");

        // Parse THEN branch
        Statement thenBranch = parseStatement();

        // Immediately check for ELSE
        Statement elseBranch = null;
        if (match(TokenType.KEYWORD, "else")) {
            elseBranch = parseStatement();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement parseVariableDeclaration() {
        String keyword = tokenStream.consume().lexeme();

        Token nameToken = tokenStream.expect(TokenType.IDENTIFIER);
        String name = nameToken.lexeme();

        Expression initializer = null;

        if (tokenStream.match(TokenType.OPERATOR, "=")) {
            initializer = parseExpression();
        }

        tokenStream.expect(TokenType.PUNCTUATION); // ;

        return new VariableDeclaration(keyword, name, initializer);
    }

    /*
    private Statement parseBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!tokenStream.match(TokenType.PUNCTUATION, "}")) {
            statements.add(parseStatement());
        }

        return new BlockStatement(statements);
    }
    */
    private Statement parseBlock() {
        log.trace("parseBlock()");

        List<Statement> statements = new ArrayList<>();

        while (
            !check(TokenType.PUNCTUATION, "}") &&
            !isAtEnd()
        ) {
            statements.add(parseStatement());
        }

        consume(TokenType.PUNCTUATION, "}", "Expected '}' after block");

        return new BlockStatement(statements);
    }

    private Statement parseExpressionStatement() {
        log.trace("parseExpressionStatement()");

        Expression expr = parseExpression();
        tokenStream.expect(TokenType.PUNCTUATION); // ;

        return new ExpressionStatement(expr);
    }

    // -------------------------
    // EXPRESSIONS
    // -------------------------

    private Expression parseExpression() {
        log.trace("parseExpression()");

        //return parseBinaryExpression(0);
        return parseAssignmentExpression();
    }

    // Operator precedence table
    private int getPrecedence(String op) {
        return switch (op) {
            case "||" -> 1;
            case "&&" -> 2;
            case "==", "!=" -> 3;
            case "<", ">", "<=", ">=" -> 4;
            case "+", "-" -> 5;
            case "*", "/" -> 6;
            default -> -1;
        };
    }

    private Expression parseAssignmentExpression() {
        log.trace("parseAssignmentExpression()");

        Expression left = parseBinaryExpression(0);
        log.debug("Parsing assignment candidate starting with: {}", left);

        Token token = tokenStream.peek();
        if (token.type() == TokenType.OPERATOR && isAssignmentOperator(token.lexeme())) {
            String operator = tokenStream.consume().lexeme();
            log.debug("Assignment operator detected: {}", operator);

            if (!isAssignable(left)) {
                throw new RuntimeException("Invalid assignment target: " + left);
            }

            Expression right = parseAssignmentExpression(); // right-associative
            return new AssignmentExpression(left, operator, right);
        }

        return left;
    }

    private boolean isAssignmentOperator(String lexeme) {
        return switch (lexeme) {
            case "=", "+=", "-=", "*=", "/=", "%=" -> true;
            default -> false;
        };
    }

    private boolean isAssignable(Expression expr) {
        return expr instanceof AstNode.IdentifierExpression
            || expr instanceof MemberAccessExpression;
    }

    private Expression parseBinaryExpression(int minPrecedence) {
        Expression left = parsePrimary();

        while (true) {
            Token opToken = tokenStream.peek();

            if (opToken.type() != TokenType.OPERATOR) break;

            int precedence = getPrecedence(opToken.lexeme());
            if (precedence < minPrecedence) break;

            String operator = tokenStream.consume().lexeme();

            Expression right = parseBinaryExpression(precedence + 1);

            //left = new BinaryExpression(left, operator, right);
            left = new AstNode.BinaryExpression(left, operator, right);
        }

        return left;
    }

}