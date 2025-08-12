package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCGetAddress extends LCExpression {
    public LCExpression expression;

    public LCGetAddress(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos,isErrorNode);
        this.expression = expression;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitGetAddress(this, additional);
    }

    @Override
    public String toString() {
        return "LCGetAddress{" +
                "expression=" + expression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
