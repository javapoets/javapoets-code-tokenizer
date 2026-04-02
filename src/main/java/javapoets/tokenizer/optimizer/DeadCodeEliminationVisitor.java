package javapoets.tokenizer.optimizer;

import javapoets.tokenizer.ast.*;

import java.util.ArrayList;
import java.util.List;

public class DeadCodeEliminationVisitor implements AstVisitor<AstNode> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DeadCodeEliminationVisitor.class);

    @Override
    public AstNode visitBooleanLiteralExpression(BooleanLiteralExpression expr) {
        return expr;
    }

    @Override
    public AstNode visitEmptyStatement(EmptyStatement stmt) {
        return stmt;
    }
    
    @Override
    public AstNode visitLiteral(AstNode.LiteralExpression node) {
        return node;
    }

    @Override
    public AstNode visitIdentifier(AstNode.IdentifierExpression node) {
        return node;
    }

    @Override
    public AstNode visitBinary(AstNode.BinaryExpression node) {
        return new AstNode.BinaryExpression(
            (Expression) node.left().accept(this),
            node.operator(),
            (Expression) node.right().accept(this)
        );
    }

    @Override
    public AstNode visitFunctionCall(FunctionCallExpression node) {
        return new FunctionCallExpression(
            (Expression) node.callee().accept(this),
            node.arguments().stream()
                .map(arg -> (Expression) arg.accept(this))
                .toList()
        );
    }

    @Override
    public AstNode visitMemberAccess(MemberAccessExpression node) {
        return new MemberAccessExpression(
            (Expression) node.object().accept(this),
            node.property()
        );
    }

    @Override
    public AstNode visitAssignment(AssignmentExpression node) {
        return new AssignmentExpression(
            (Expression) node.target().accept(this),
            node.operator(),
            (Expression) node.value().accept(this)
        );
    }

    @Override
    public AstNode visitVariableDeclaration(VariableDeclaration node) {
        log.trace("visitVariableDeclaration(VariableDeclaration node)");
        
        return new VariableDeclaration(
            node.keyword(),
            node.name(),
            node.initializer() == null ? null : (Expression) node.initializer().accept(this)
        );
    }

    @Override
    public AstNode visitExpressionStatement(ExpressionStatement node) {
        return new ExpressionStatement((Expression) node.expression().accept(this));
    }

    @Override
    public AstNode visitBlockStatement(BlockStatement node) {
        List<Statement> optimized = new ArrayList<>();

        for (Statement stmt : node.statements()) {
            Statement transformed = (Statement) stmt.accept(this);

            if (transformed instanceof BlockStatement block && block.statements().isEmpty()) {
                continue;
            }

            optimized.add(transformed);
        }

        return new BlockStatement(optimized);
    }

    @Override
    public AstNode visitFunctionDeclaration(FunctionDeclaration node) {
        return new FunctionDeclaration(
            node.name(),
            node.parameters(),
            (BlockStatement) node.body().accept(this)
        );
    }

    @Override
    public AstNode visitReturn(ReturnStatement node) {
        return node.expression() == null
            ? node
            : new ReturnStatement((Expression) node.expression().accept(this));
    }

    /*
    @Override
    public AstNode visitIf(IfStatement node) {
        Expression condition = (Expression) node.condition().accept(this);

        if (condition instanceof AstNode.LiteralExpression literal && literal.value() instanceof Boolean b) {
            if (b) {
                return node.thenBranch().accept(this);
            }
            return node.elseBranch() != null
                ? node.elseBranch().accept(this)
                : new BlockStatement(List.of());
        }

        Statement thenBranch = (Statement) node.thenBranch().accept(this);
        Statement elseBranch = node.elseBranch() == null
            ? null
            : (Statement) node.elseBranch().accept(this);

        return new IfStatement(condition, thenBranch, elseBranch);
    }
    */
    @Override
    public AstNode visitIfStatement(IfStatement stmt) {

        Expression condition = (Expression) stmt.condition().accept(this);
        Statement thenBranch = (Statement) stmt.thenBranch().accept(this);

        Statement elseBranch = null;
        if (stmt.elseBranch() != null) {
            elseBranch = (Statement) stmt.elseBranch().accept(this);
        }

        log.debug("DCE: condition = {}", condition.getClass());

        // 🔥 THIS is the key logic
        if (condition instanceof BooleanLiteralExpression bool) {

            if (bool.value()) {
                return thenBranch; // eliminate IF
            } else {
                return elseBranch != null ? elseBranch : new EmptyStatement();
            }
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }
}