package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class LCBlock extends LCExpressionWithScope {
    public List<LCStatement> statements;
    public Queue<LCStatement> statementQueue = new LinkedList<>();

    public LCBlock(List<LCStatement> statements, Position pos) {
        this(statements, pos, false);
    }

    public LCBlock(List<LCStatement> statements, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.statements = new ArrayList<>(statements);
        for (LCStatement statement : statements) statement.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitBlock(this, additional);
    }

    @Override
    public String toString() {
        return "LCBlock{" +
                "statements=" + statements +
                ", scope=" + scope +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}