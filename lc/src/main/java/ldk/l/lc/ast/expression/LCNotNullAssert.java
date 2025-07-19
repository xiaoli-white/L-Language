package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCNotNullAssert extends LCExpression {
    public LCExpression base;

    public LCNotNullAssert(LCExpression base, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;
        this.base.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNotNullAssert(this, additional);
    }

    @Override
    public String toString() {
        return "LCNotNullAssert{" +
                "base=" + base +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCNotNullAssert clone() throws CloneNotSupportedException {
        return new LCNotNullAssert(base.clone(), position.clone(), isErrorNode);
    }
}
