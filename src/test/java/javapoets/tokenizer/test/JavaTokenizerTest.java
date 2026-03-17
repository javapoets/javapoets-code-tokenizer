package javapoets.tokenizer.test;

import java.util.List;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.core.Token;
import javapoets.tokenizer.core.TokenType;
import javapoets.tokenizer.core.Tokenizer;
import javapoets.tokenizer.language.JavaLanguageDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class JavaTokenizerTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavaTokenizerTest.class);

    @Test
    void should_tokenize_simple_java_statement() {
        log.trace("should_tokenize_simple_java_statement()");

        String code = "int x = 10;";

        Tokenizer tokenizer = new Tokenizer(new JavaLanguageDefinition());
        List<Token> tokens = tokenizer.tokenize(code);

        assertThat(tokens)
            .extracting(Token::type)
            .containsExactly(
                  TokenType.KEYWORD
                , TokenType.IDENTIFIER
                , TokenType.OPERATOR
                , TokenType.INTEGER_LITERAL
                , TokenType.PUNCTUATION
                , TokenType.EOF
            );
    }
}