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
        this.tokenStream = tokenStream;
    }

    // ENTRY POINT
    public List<Statement> parseProgram() {
        List<Statement> statements = new ArrayList<>();

        while (!tokenStream.isAtEnd()) {
            statements.add(parseStatement());
        }

        return statements;
    }

    private Expression parsePrimary() {
        Expression expr = parseAtom();

        while (true) {
            if (tokenStream.match(TokenType.PUNCTUATION, "(")) {
                expr = finishFunctionCall(expr);
            } else if (tokenStream.match(TokenType.PUNCTUATION, ".")) {
                String property = tokenStream.expect(TokenType.IDENTIFIER).lexeme();
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
        Token t = tokenStream.peek();

        switch (t.type()) {
            case INTEGER_LITERAL -> {
                tokenStream.consume();
                return new AstNode.LiteralExpression(Integer.parseInt(t.lexeme()));
            }
            case FLOAT_LITERAL -> {
                tokenStream.consume();
                return new AstNode.LiteralExpression(Double.parseDouble(t.lexeme()));
            }
            case STRING_LITERAL -> {
                tokenStream.consume();
                return new AstNode.LiteralExpression(t.lexeme());
            }
            case IDENTIFIER -> {
                tokenStream.consume();
                return new AstNode.IdentifierExpression(t.lexeme());
            }
            case PUNCTUATION -> {
                if (tokenStream.match(TokenType.PUNCTUATION, "(")) {
                    Expression expr = parseExpression();
                    tokenStream.expect(TokenType.PUNCTUATION, ")");
                    return expr;
                }
            }
        }

        throw new RuntimeException("Unexpected token: " + t);
    }

    private Expression finishFunctionCall(Expression callee) {
        List<Expression> args = new ArrayList<>();

        if (!tokenStream.match(TokenType.PUNCTUATION, ")")) {
            do {
                args.add(parseExpression());
            } while (tokenStream.match(TokenType.PUNCTUATION, ","));

            tokenStream.expect(TokenType.PUNCTUATION, ")");
        }

        return new FunctionCallExpression(callee, args);
    }

    // -------------------------
    // STATEMENTS
    // -------------------------

    private Statement parseStatement() {

        Token token = tokenStream.peek();

        log.debug("Parsing statement starting with: {}", token);

        if (token.type() == TokenType.KEYWORD) {
            String kw = token.lexeme();

            if (kw.equals("int") || kw.equals("let") || kw.equals("const") || kw.equals("var")) {
                return parseVariableDeclaration();
            }
        }

        if (tokenStream.match(TokenType.PUNCTUATION, "{")) {
            return parseBlock();
        }

        return parseExpressionStatement();
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

    private Statement parseBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!tokenStream.match(TokenType.PUNCTUATION, "}")) {
            statements.add(parseStatement());
        }

        return new BlockStatement(statements);
    }

    private Statement parseExpressionStatement() {
        Expression expr = parseExpression();
        tokenStream.expect(TokenType.PUNCTUATION); // ;

        return new ExpressionStatement(expr);
    }

    // -------------------------
    // EXPRESSIONS
    // -------------------------

    private Expression parseExpression() {
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