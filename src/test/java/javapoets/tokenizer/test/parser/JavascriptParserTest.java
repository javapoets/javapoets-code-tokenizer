package javapoets.tokenizer.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.test.BaseJavascriptTest;

class JavascriptParserTest extends BaseJavascriptTest {

    @Test
    void should_parse_if_else_structure() {
        var ast = parse("""
            if (x > 10) { y = 1; } else { y = 2; }
        """);

        assertThat(ast).hasSize(1);
        assertThat(ast.get(0)).isInstanceOf(IfStatement.class);
    }
}