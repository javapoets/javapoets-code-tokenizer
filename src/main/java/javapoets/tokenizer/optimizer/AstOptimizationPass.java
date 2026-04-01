package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.Statement;

import java.util.List;

public interface AstOptimizationPass {
    String name();
    List<Statement> apply(List<Statement> statements);
}