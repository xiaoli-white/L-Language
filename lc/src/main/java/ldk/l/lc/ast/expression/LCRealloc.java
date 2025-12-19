package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

@Deprecated
public final class LCRealloc extends LCExpression {
    public LCExpression expression;
    public LCExpression size;

    public LCRealloc(LCExpression expression, LCExpression size, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
        this.size = size;
        this.size.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitRealloc(this, additional);
    }

    @Override
    public String toString() {
        return "LCRealloc{" +
                "expression=" + expression +
                ", size=" + size +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
