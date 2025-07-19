package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCReferenceTypeExpression extends LCTypeExpression {
    public LCTypeExpression base;

    public LCReferenceTypeExpression(LCTypeExpression base, Position pos) {
        this(base, pos, false);
    }

    public LCReferenceTypeExpression(LCTypeExpression base, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;
        this.base.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitReferenceTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCReferenceTypeExpression{" +
                "base=" + base +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "&";
    }

    @Override
    public LCReferenceTypeExpression clone() throws CloneNotSupportedException {
        return new LCReferenceTypeExpression(this.base.clone(), this.position.clone(), this.isErrorNode);
    }
}
