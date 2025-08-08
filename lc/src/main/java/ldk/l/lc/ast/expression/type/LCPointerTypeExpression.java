package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCPointerTypeExpression extends LCTypeExpression {
    public LCTypeExpression base;

    public LCPointerTypeExpression(LCTypeExpression base, Position pos) {
        this(base, pos, false);
    }

    public LCPointerTypeExpression(LCTypeExpression base, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;

        this.base.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitPointerTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCPointerTypeExpression{" +
                "base=" + base +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", theType=" + theType +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "*";
    }
}
