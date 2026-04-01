package javapoets.tokenizer.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.ast.Statement;
import javapoets.tokenizer.obfuscation.ObfuscationVisitor;
import javapoets.tokenizer.optimizer.*;
import javapoets.tokenizer.test.BaseJavascriptTest;

class EndToEndJavascriptTest extends BaseJavascriptTest {

    @Test
    void should_optimize_obfuscate_and_minify() {
        
        var ast = parse("""
            let total = 10 + 5 * 2;
            if (true) {
                let result = total + 0;
            }
        """);

        var pipeline = new OptimizationPipeline()
            .addPass(new ConstantFoldingPass())
            .addPass(new AlgebraicSimplificationPass())
            .addPass(new DeadCodeEliminationPass());

        var optimized = pipeline.optimize(ast);

        var obfuscator = new ObfuscationVisitor();

        var obfuscated = optimized.stream()
            .map(stmt -> (Statement) stmt.accept(obfuscator))
            .toList();

        String output = minify(obfuscated);

        assertThat(output).isEqualTo("let a=20;{let b=a;}");
    }
}