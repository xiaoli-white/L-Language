package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCMalloc extends LCExpression {
    public LCExpression size;

    public LCMalloc(LCExpression size, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.size = size;
        this.size.parentNode = this;

        this.theType = SystemTypes.VOID_POINTER;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitMalloc(this, additional);
    }

    @Override
    public String toString() {
        return "LCMalloc{" +
                "size=" + size +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
