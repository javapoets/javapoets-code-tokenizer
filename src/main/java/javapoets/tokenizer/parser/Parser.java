package javapoets.tokenizer.parser;

import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.core.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Parser.class);

    private final TokenStream tokens;

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
    }

    // ENTRY POINT
    public List<Statement> parseProgram() {
        List<Statement> statements = new ArrayList<>();

        while (!tokens.isAtEnd()) {
            statements.add(parseStatement());
        }

        return statements;
    }

    // -------------------------
    // STATEMENTS
    // -------------------------

    private Statement parseStatement() {

        Token token = tokens.peek();

        log.debug("Parsing statement starting with: {}", token);

        if (token.type() == TokenType.KEYWORD) {
            String kw = token.lexeme();

            if (kw.equals("int") || kw.equals("let") || kw.equals("const") || kw.equals("var")) {
                return parseVariableDeclaration();
            }
        }

        if (tokens.match(TokenType.PUNCTUATION, "{")) {
            return parseBlock();
        }

        return parseExpressionStatement();
    }

    private Statement parseVariableDeclaration() {
        String keyword = tokens.consume().lexeme();

        Token nameToken = tokens.expect(TokenType.IDENTIFIER);
        String name = nameToken.lexeme();

        Expression initializer = null;

        if (tokens.match(TokenType.OPERATOR, "=")) {
            initializer = parseExpression();
        }

        tokens.expect(TokenType.PUNCTUATION); // ;

        return new VariableDeclaration(keyword, name, initializer);
    }

    private Statement parseBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!tokens.match(TokenType.PUNCTUATION, "}")) {
            statements.add(parseStatement());
        }

        return new BlockStatement(statements);
    }

    private Statement parseExpressionStatement() {
        Expression expr = parseExpression();
        tokens.expect(TokenType.PUNCTUATION); // ;

        return new ExpressionStatement(expr);
    }

    // -------------------------
    // EXPRESSIONS
    // -------------------------

    private Expression parseExpression() {
        return parseBinaryExpression(0);
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

    private Expression parseBinaryExpression(int minPrecedence) {
        Expression left = parsePrimary();

        while (true) {
            Token opToken = tokens.peek();

            if (opToken.type() != TokenType.OPERATOR) break;

            int precedence = getPrecedence(opToken.lexeme());
            if (precedence < minPrecedence) break;

            String operator = tokens.consume().lexeme();

            Expression right = parseBinaryExpression(precedence + 1);

            //left = new BinaryExpression(left, operator, right);
            left = new AstNode.BinaryExpression(left, operator, right);
        }

        return left;
    }

    private Expression parsePrimary() {
        Token t = tokens.peek();

        switch (t.type()) {
            case INTEGER_LITERAL -> {
                tokens.consume();
                //return new LiteralExpression(Integer.parseInt(t.lexeme()));
                return new AstNode.LiteralExpression(Integer.parseInt(t.lexeme()));
            }
            case FLOAT_LITERAL -> {
                tokens.consume();
                //return new LiteralExpression(Double.parseDouble(t.lexeme()));
                return new AstNode.LiteralExpression(Double.parseDouble(t.lexeme()));
            }
            case STRING_LITERAL -> {
                tokens.consume();
                //return new LiteralExpression(t.lexeme());
                return new AstNode.LiteralExpression(t.lexeme());
            }
            case IDENTIFIER -> {
                tokens.consume();
                //return new IdentifierExpression(t.lexeme());
                return new AstNode.IdentifierExpression(t.lexeme());
            }
            case PUNCTUATION -> {
                if (tokens.match(TokenType.PUNCTUATION, "(")) {
                    Expression expr = parseExpression();
                    tokens.expect(TokenType.PUNCTUATION); // )
                    return expr;
                }
            }
        }

        throw new RuntimeException("Unexpected token: " + t);
    }
}