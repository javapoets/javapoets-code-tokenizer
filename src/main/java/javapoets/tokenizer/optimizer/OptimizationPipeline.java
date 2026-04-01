package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OptimizationPipeline {

    private static final Logger log = LoggerFactory.getLogger(OptimizationPipeline.class);

    private final List<AstOptimizationPass> passes = new ArrayList<>();

    public OptimizationPipeline addPass(AstOptimizationPass pass) {
        passes.add(pass);
        return this;
    }

    public List<Statement> optimize(List<Statement> statements) {
        List<Statement> current = statements;

        for (AstOptimizationPass pass : passes) {
            log.debug("Running optimization pass: {}", pass.name());
            current = pass.apply(current);
        }

        return current;
    }
}