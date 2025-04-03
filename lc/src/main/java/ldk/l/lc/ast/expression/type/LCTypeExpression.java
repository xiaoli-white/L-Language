package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public abstract class LCTypeExpression extends LCExpression {
    public LCTypeExpression(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public abstract String toTypeString();

    @Override
    public abstract LCTypeExpression clone() throws CloneNotSupportedException;
}