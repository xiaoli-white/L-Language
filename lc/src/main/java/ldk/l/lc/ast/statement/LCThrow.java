package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

public final class LCThrow extends LCStatement {
    public LCExpression expression;

    public LCThrow(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;

        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitThrow(this, additional);
    }

    @Override
    public String toString() {
        return "LCThrow{" +
                "LCExpression=" + expression +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
