package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCAutoTypeExpression extends LCTypeExpression {
    public LCAutoTypeExpression(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    @Override
    public String toTypeString() {
        return "auto";
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitAutoTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCPredefinedTypeExpression{" +
                "shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", theType=" + theType +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCAutoTypeExpression clone() throws CloneNotSupportedException {
        return new LCAutoTypeExpression(this.position.clone(), this.isErrorNode);
    }
}
