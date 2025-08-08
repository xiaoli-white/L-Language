package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCDereference extends LCExpression {
    public LCExpression expression;

    public LCDereference(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitDereference(this, additional);
    }

    @Override
    public String toString() {
        return "LCDereference{" +
                "LCExpression=" + expression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
