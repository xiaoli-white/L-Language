package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCArrayTypeExpression extends LCTypeExpression {
    public LCTypeExpression base;

    public LCArrayTypeExpression(LCTypeExpression base, Position pos) {
        this(base, pos, false);
    }

    public LCArrayTypeExpression(LCTypeExpression base, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;
        this.base.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitArrayTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCArrayTypeExpression{" +
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
    public LCArrayTypeExpression clone() throws CloneNotSupportedException {
        return new LCArrayTypeExpression(this.base.clone(), this.position.clone(), this.isErrorNode);
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "[]";
    }
}
