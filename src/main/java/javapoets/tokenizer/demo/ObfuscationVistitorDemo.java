package javapoets.tokenizer.demo;

import java.util.List;
import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.core.*;
import javapoets.tokenizer.debug.*;
import javapoets.tokenizer.language.*;
import javapoets.tokenizer.obfuscation.*;
import javapoets.tokenizer.parser.*;
import javapoets.tokenizer.visitor.*;

public class ObfuscationVistitorDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TokenizerDemo.class);

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
            let total = 10 + price;
            let result = total + 5;
        """);

        LoggingTokenPrinter.print(tokens);

        TokenStream tokenStream = new TokenStream(tokens);
        Parser parser = new Parser(tokenStream);

        List<Statement> ast = parser.parseProgram();

        // Obfuscate
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
            //System.out.print(stmt.accept(printer));
            String output = stmt.accept(printer);
            //System.out.print(output);
            log.debug(output);
        }
    }
}