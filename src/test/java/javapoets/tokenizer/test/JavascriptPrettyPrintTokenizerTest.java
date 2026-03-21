package javapoets.tokenizer.test;

import java.util.List;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.ast.Statement;
import javapoets.tokenizer.core.Token;
import javapoets.tokenizer.core.TokenStream;
import javapoets.tokenizer.debug.*;
import javapoets.tokenizer.core.Tokenizer;
import javapoets.tokenizer.language.JavascriptLanguageDefinition;
import javapoets.tokenizer.parser.Parser;
import javapoets.tokenizer.visitor.PrettyPrinterVisitor;

import static org.assertj.core.api.Assertions.assertThat;

class JavascriptPrettyPrintTokenizerTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavascriptTokenizerTest.class);

    @Test
    void should_parse_and_pretty_print() {
        log.trace("should_parse_and_pretty_print()");

        //String code = "function add(a,b){ return a+b; }";
        String code = "let x = 10 + 5 * 2;";

        Tokenizer tokenizer = new Tokenizer(new JavascriptLanguageDefinition());
        List<Token> tokens = tokenizer.tokenize(code);

        log.debug("\\n=== JavaScript Tokens ===");
        LoggingTokenPrinter.print(tokens);

        TokenStream stream = new TokenStream(tokens);
        Parser parser = new Parser(stream);
        List<Statement> ast = parser.parseProgram();

        PrettyPrinterVisitor printer = new PrettyPrinterVisitor();
        String output = ast.get(0).accept(printer);
        log.debug(output);

        assertThat(output).contains("let x = (10 + (5 * 2));");
    }

}