package javapoets.tokenizer.test;

import java.util.List;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.core.Token;
import javapoets.tokenizer.core.Tokenizer;
import javapoets.tokenizer.language.JavascriptLanguageDefinition;
import javapoets.tokenizer.debug.*;

import static org.assertj.core.api.Assertions.assertThat;

class JavascriptTokenizerTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavascriptTokenizerTest.class);

    @Test
    void should_tokenize_simple_js_function() {
        log.trace("should_tokenize_simple_js_function()");

        String code = "function add(a,b){ return a+b; }";

        Tokenizer tokenizer = new Tokenizer(new JavascriptLanguageDefinition());
        List<Token> tokens = tokenizer.tokenize(code);

        log.debug("\\n=== JavaScript Tokens ===");
        LoggingTokenPrinter.print(tokens);

        assertThat(tokens)
            .extracting(Token::lexeme)
            .contains("function", "add", "a", "b", "return", "+");
    }

    @Test
    void should_handle_block_scopes() {
        String code = """
            {
              let x = 1;
            }
            let x = 2;
        """;

        Tokenizer tokenizer = new Tokenizer(new JavaScriptLanguageDefinition());
        var tokens = tokenizer.tokenize(code);

        var parser = new Parser(new TokenStream(tokens));
        var ast = parser.parseProgram();

        var obfuscator = new ObfuscationVisitor();

        var result = ast.stream()
            .map(stmt -> (Statement) stmt.accept(obfuscator))
            .toList();

        var printer = new PrettyPrinterVisitor();

        for (Statement stmt : result) {
            System.out.print(stmt.accept(printer));
        }
    }
}