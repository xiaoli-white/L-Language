package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;

public abstract class LCAbstractLoop extends LCStatementWithScope {

    public LCAbstractLoop(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    @Override
    public abstract LCAbstractLoop clone() throws CloneNotSupportedException;
}
