package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.Statement;

import java.util.List;

public class DeadCodeEliminationPass implements AstOptimizationPass {

    @Override
    public String name() {
        return "DeadCodeElimination";
    }

    @Override
    public List<Statement> apply(List<Statement> statements) {
        DeadCodeEliminationVisitor visitor = new DeadCodeEliminationVisitor();

        return statements.stream()
            .map(stmt -> (Statement) stmt.accept(visitor))
            .toList();
    }
}