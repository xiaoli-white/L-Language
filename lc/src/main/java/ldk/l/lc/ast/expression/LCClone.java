package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCClone extends LCExpression {
    public LCExpression expression;

    public LCClone(LCExpression expression, Position pos) {
        this(expression, pos, false);
    }

    public LCClone(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitClone(this, additional);
    }

    @Override
    public String toString() {
        return "LCClone{" +
                "expression=" + expression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
