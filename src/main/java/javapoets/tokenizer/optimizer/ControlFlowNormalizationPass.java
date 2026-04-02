package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.*;

import java.util.List;
import java.util.stream.Collectors;

public class ControlFlowNormalizationPass implements AstOptimizationPass {

    @Override
    public String name() {
        return "ControlFlowNormalization";
    }

    @Override
    public List<Statement> apply(List<Statement> statements) {

        ControlFlowNormalizationVisitor visitor = new ControlFlowNormalizationVisitor();

        return statements.stream()
            .map(stmt -> (Statement) stmt.accept(visitor))
            // 🔥 Remove top-level empty statements
            .filter(stmt -> !(stmt instanceof EmptyStatement))
            .collect(Collectors.toList());
    }
}