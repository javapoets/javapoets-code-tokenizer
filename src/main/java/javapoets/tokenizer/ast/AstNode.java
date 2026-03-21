package javapoets.tokenizer.ast;

public interface AstNode {

    <R> R accept(AstVisitor<R> visitor);

    public record LiteralExpression(Object value) implements Expression {
        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    public record IdentifierExpression(String name) implements Expression {
        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitIdentifier(this);
        }
    }

    public record BinaryExpression(
        Expression left,
        String operator,
        Expression right
    ) implements Expression {
        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

}