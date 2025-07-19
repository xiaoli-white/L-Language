package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCNullableTypeExpression extends LCTypeExpression {
    public LCTypeExpression base;

    public LCNullableTypeExpression(LCTypeExpression base, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;
        this.base.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNullableTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCNullableTypeExpression{" +
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
    public String toTypeString() {
        return base.toTypeString() + "?";
    }

    @Override
    public LCNullableTypeExpression clone() throws CloneNotSupportedException {
        return new LCNullableTypeExpression(this.base.clone(), this.position.clone(), this.isErrorNode);
    }
}
