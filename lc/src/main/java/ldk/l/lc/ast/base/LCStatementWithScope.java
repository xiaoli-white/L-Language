package ldk.l.lc.ast.base;

import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;

public abstract class LCStatementWithScope extends LCStatement {
    public Scope scope = null;

    public LCStatementWithScope(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }
}