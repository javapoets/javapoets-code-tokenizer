package javapoets.tokenizer.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.debug.LoggingTokenPrinter;
import javapoets.tokenizer.test.BaseJavascriptTest;
import javapoets.tokenizer.token.Token;

class JavascriptTokenizerTest extends BaseJavascriptTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavascriptTokenizerTest.class);

    @Test
    void should_tokenize_function_keywords_and_identifiers() {

        //var tokens = tokenize("function add(a,b){ return a+b; }");
        var tokens = tokenizeWithoutEof("function add(a,b){ return a+b; }");

        assertThat(tokens)
            .extracting(Token::lexeme)
            .containsExactly(
                "function" , "add"
                , "(", "a", ",", "b", ")"
                , "{", "return", "a", "+", "b", ";", "}"
            );
    }

    @Test
    void should_tokenize_simple_js_function() {
        log.trace("should_tokenize_simple_js_function()");

        String code = "function add(a,b){ return a+b; }";

        //Tokenizer tokenizer = new Tokenizer(new JavascriptLanguageDefinition());
        //List<Token> tokens = tokenizer.tokenize(code);
        var tokens = tokenize(code);

        log.debug("\\n=== JavaScript Tokens ===");
        LoggingTokenPrinter.print(tokens);

        assertThat(tokens)
            //.filteredOn(token -> token.type() != TokenType.EOF) // filter out EOF in tests
            .extracting(Token::lexeme)
            .contains("function", "add", "a", "b", "return", "+");
    }

}