package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

public final class LCYield extends LCStatement {
    public LCExpression value;

    public LCYield(LCExpression value, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.value = value;
        this.value.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitYield(this, additional);
    }

    @Override
    public String toString() {
        return "LCYield{" +
                "value=" + value +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
