package javapoets.tokenizer.ast;
    
public abstract class BaseAstVisitor<R> implements AstVisitor<R> {

    
    protected R defaultVisit(AstNode node) {
        throw new UnsupportedOperationException("Not implemented: " + node.getClass().getSimpleName());

        /*
         * Printer Visitor Type MUST override everything
         * Optimizer Visitor Type san safely default to identity
         */
        //return (R) node; // safe fallback for transformers
    }

    @Override
    public R visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return defaultVisit(expr);
    }

    @Override
    public R visitEmptyStatement(EmptyStatement stmt) {
        return defaultVisit(stmt);
    }

    // add defaults for all nodes
}