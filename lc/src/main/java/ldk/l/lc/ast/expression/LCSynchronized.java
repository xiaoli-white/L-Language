package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCExpressionWithScope;
import ldk.l.lc.util.Position;

public final class LCSynchronized extends LCExpressionWithScope {
    public LCExpression lock;
    public LCBlock body;

    public LCSynchronized(LCExpression lock, LCBlock body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.lock = lock;
        this.lock.parentNode = this;

        this.body = body;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSynchronized(this, additional);
    }

    @Override
    public String toString() {
        return "LCSynchronized{" +
                "lock=" + lock +
                ", LCBlock=" + body +
                ", scope=" + scope +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
