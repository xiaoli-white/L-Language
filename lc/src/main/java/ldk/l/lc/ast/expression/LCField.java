package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCField extends LCExpression {
    public LCField(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitField(this, additional);
    }

    @Override
    public String toString() {
        return "LCField{" +
                "theType=" + theType +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
