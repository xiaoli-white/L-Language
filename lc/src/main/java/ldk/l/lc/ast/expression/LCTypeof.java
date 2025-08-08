package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCTypeof extends LCExpression {
    public LCExpression expression;

    public LCTypeof(LCExpression expression, Position pos) {
        this(expression, pos, false);
    }

    public LCTypeof(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTypeof(this, additional);
    }

    @Override
    public String toString() {
        return "LCTypeof{" +
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