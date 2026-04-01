package javapoets.tokenizer.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import javapoets.tokenizer.optimizer.*;
import javapoets.tokenizer.test.BaseJavascriptTest;

class OptimizationPipelineTest extends BaseJavascriptTest {

    @Test
    void should_fold_constants() {
        var ast = parse("let x = 10 + 5 * 2;");

        var pipeline = new OptimizationPipeline()
            .addPass(new ConstantFoldingPass());

        var optimized = pipeline.optimize(ast);

        String output = minify(optimized);

        assertThat(output).contains("20");
    }
}