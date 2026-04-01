package javapoets.tokenizer.demo;

import java.util.List;
import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.token.*;
import javapoets.tokenizer.debug.*;
import javapoets.tokenizer.language.*;
import javapoets.tokenizer.lexer.Tokenizer;
import javapoets.tokenizer.obfuscation.*;
import javapoets.tokenizer.parser.*;
import javapoets.tokenizer.visitor.*;
import javapoets.tokenizer.stream.TokenStream;
import javapoets.tokenizer.optimizer.ConstantFoldingVisitor;

public class ObfuscationVisitorDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ObfuscationVisitorDemo.class);

    private static final String EMPTY = "";

    public static void main(String[] args) {
        log.trace("main(args)");

        /*
        Tokenizer jsTokenizer = new Tokenizer(new JavascriptLanguageDefinition(), false, false);
        List<Token> tokens = null;
        Parser parser = null;
        */

        Tokenizer tokenizer = new Tokenizer(new JavascriptLanguageDefinition());

        List<Token> tokens = tokenizer.tokenize("""
            let price = 5;
            let total = 10 + price;
            let result = total + 5;
        """);

        LoggingTokenPrinter.print(tokens);

        TokenStream tokenStream = new TokenStream(tokens);
        Parser parser = new Parser(tokenStream);

        List<Statement> ast = parser.parseProgram();

        // Obfuscate
        log.debug(EMPTY);
        log.debug("\\n=== ObfuscationVisitor ===");
        log.debug(EMPTY);
        ObfuscationVisitor obfuscator = new ObfuscationVisitor();
        List<Statement> obfuscatedAst = ast.stream()
            .map(stmt -> (Statement) stmt.accept(obfuscator))
            .toList();

        // Pretty print
        log.debug(EMPTY);
        log.debug("\\n=== PrettyPrinterVisitor ===");
        log.debug(EMPTY);

        PrettyPrinterVisitor printer = new PrettyPrinterVisitor();
        for (Statement stmt : obfuscatedAst) {
            String output = stmt.accept(printer);
            log.debug(output);
        }

        // Pretty print
        log.debug(EMPTY);
        log.debug("\\n=== ConstantFoldingVisitor ===");
        log.debug(EMPTY);
        ConstantFoldingVisitor optimizer = new ConstantFoldingVisitor();

        List<Statement> optimizedAst = ast.stream()
            .map(stmt -> (Statement) stmt.accept(optimizer))
            .toList();

        for (Statement stmt : optimizedAst) {
            String output = stmt.accept(printer);
            log.debug(output);
        }

    }
}