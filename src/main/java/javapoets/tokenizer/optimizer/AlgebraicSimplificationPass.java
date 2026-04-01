package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.Statement;

import java.util.List;

public class AlgebraicSimplificationPass implements AstOptimizationPass {

    @Override
    public String name() {
        return "AlgebraicSimplification";
    }

    @Override
    public List<Statement> apply(List<Statement> statements) {
        AlgebraicSimplificationVisitor visitor = new AlgebraicSimplificationVisitor();

        return statements.stream()
            .map(stmt -> (Statement) stmt.accept(visitor))
            .toList();
    }
}