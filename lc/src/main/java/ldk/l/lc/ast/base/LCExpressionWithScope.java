package ldk.l.lc.ast.base;

import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;

public abstract class LCExpressionWithScope extends LCExpression {
    public Scope scope = null;

    public LCExpressionWithScope(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    @Override
    public abstract LCExpressionWithScope clone() throws CloneNotSupportedException;
}
