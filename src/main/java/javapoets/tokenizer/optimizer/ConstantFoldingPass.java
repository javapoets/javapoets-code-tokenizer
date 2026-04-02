package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.Statement;

import java.util.List;

public class ConstantFoldingPass implements AstOptimizationPass {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConstantFoldingPass.class);

    @Override
    public String name() {
        return "ConstantFolding";
    }

    /*
    @Override
    public List<Statement> apply(List<Statement> statements) {
        OptimizerVisitor visitor = new OptimizerVisitor();

        return statements.stream()
            .map(stmt -> (Statement) stmt.accept(visitor))
            .toList();
    }
    */
    public List<Statement> apply(List<Statement> statements) {
        ConstantFoldingVisitor visitor = new ConstantFoldingVisitor();

        log.debug("USING ConstantFoldingVisitor");

        return statements.stream()
            .map(stmt -> (Statement) stmt.accept(visitor))
            .toList();
    }
}