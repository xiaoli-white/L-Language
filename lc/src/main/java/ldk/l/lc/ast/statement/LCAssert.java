package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

public final class LCAssert extends LCStatement {
    public LCExpression condition;
    public LCExpression message;

    public LCAssert(LCExpression condition, LCExpression message, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);

        this.condition = condition;
        this.condition.parentNode = this;

        this.message = message;
        if (this.message != null) this.message.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitAssert(this, additional);
    }

    @Override
    public String toString() {
        return "LCAssert{" +
                "condition=" + condition +
                ", message=" + message +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
