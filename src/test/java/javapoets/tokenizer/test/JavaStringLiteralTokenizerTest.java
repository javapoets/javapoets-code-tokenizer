package javapoets.tokenizer.test;

import java.util.List;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.core.Token;
import javapoets.tokenizer.core.TokenType;
import javapoets.tokenizer.core.Tokenizer;
import javapoets.tokenizer.language.JavaLanguageDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class JavaStringLiteralTokenizerTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavaStringLiteralTokenizerTest.class);

    @Test
    void should_handle_string_literals() {
        log.trace("should_handle_string_literals()");
        
        String code = "String s = \"Hello World\";";

        Tokenizer tokenizer = new Tokenizer(new JavaLanguageDefinition());
        List<Token> tokens = tokenizer.tokenize(code);

        assertThat(tokens)
            .anyMatch(t -> t.type() == TokenType.STRING_LITERAL &&
                           t.lexeme().equals("\"Hello World\""));
    }
}