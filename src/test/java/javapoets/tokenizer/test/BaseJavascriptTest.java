package javapoets.tokenizer.test;

import java.util.List;
import javapoets.tokenizer.ast.Statement;
import javapoets.tokenizer.minify.MinifyPrinterVisitor;
import javapoets.tokenizer.language.JavascriptLanguageDefinition;
import javapoets.tokenizer.parser.Parser;
import javapoets.tokenizer.token.Token;
import javapoets.tokenizer.token.TokenType;
import javapoets.tokenizer.stream.TokenStream;
import javapoets.tokenizer.lexer.Tokenizer;

public abstract class BaseJavascriptTest {

    protected List<Token> tokenize(String code) {
        return new Tokenizer(new JavascriptLanguageDefinition())
            .tokenize(code);
    }

    protected List<Token> withoutEof(List<Token> tokens) {
        return tokens.stream()
            .filter(t -> t.type() != TokenType.EOF)
            .toList();
    }

    protected List<Token> tokenizeWithoutEof(String code) {
        return withoutEof(tokenize(code));
    }

    protected List<Statement> parse(String code) {
        var tokens = tokenize(code);
        return new Parser(new TokenStream(tokens)).parseProgram();
    }

    protected String minify(List<Statement> ast) {
        var printer = new MinifyPrinterVisitor();
        StringBuilder sb = new StringBuilder();

        for (Statement stmt: ast) {
            sb.append(stmt.accept(printer));
        }

        return sb.toString();
    }
}