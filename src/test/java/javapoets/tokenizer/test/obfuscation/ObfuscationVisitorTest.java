package javapoets.tokenizer.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.ast.Statement;
import javapoets.tokenizer.obfuscation.ObfuscationVisitor;
import javapoets.tokenizer.test.BaseJavascriptTest;

class ObfuscationVisitorTest extends BaseJavascriptTest {

    @Test
    void should_rename_variables_in_function_scope() {
        var ast = parse("""
            function add(x, y) {
              let z = x + y;
              return z;
            }
        """);

        var obfuscator = new ObfuscationVisitor();

        var result = ast.stream()
            .map(stmt -> (Statement) stmt.accept(obfuscator))
            .toList();

        String output = minify(result);

        assertThat(output).contains("function");
        assertThat(output).doesNotContain("x", "y", "z");
    }
}