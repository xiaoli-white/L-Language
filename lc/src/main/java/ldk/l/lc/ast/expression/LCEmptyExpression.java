package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCEmptyExpression extends LCExpression {
    public LCEmptyExpression(Position pos) {
        super(pos, false);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitEmptyExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCEmptyExpression{}";
    }
}
