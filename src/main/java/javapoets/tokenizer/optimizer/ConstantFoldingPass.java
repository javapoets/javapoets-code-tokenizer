package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.Statement;

import java.util.List;

public class ConstantFoldingPass implements AstOptimizationPass {

    @Override
    public String name() {
        return "ConstantFolding";
    }

    @Override
    public List<Statement> apply(List<Statement> statements) {
        OptimizerVisitor visitor = new OptimizerVisitor();

        return statements.stream()
            .map(stmt -> (Statement) stmt.accept(visitor))
            .toList();
    }
}