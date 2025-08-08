package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;

public final class LCDoWhile extends LCAbstractLoop {
    public LCStatement body;
    public LCExpression condition;

    public LCDoWhile(LCStatement body, LCExpression condition, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.body = body;
        this.condition = condition;

        this.body.parentNode = this;
        this.condition.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitDoWhile(this, additional);
    }

    @Override
    public String toString() {
        return "LCDoWhile{" +
                "condition=" + condition +
                ", body=" + body +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
