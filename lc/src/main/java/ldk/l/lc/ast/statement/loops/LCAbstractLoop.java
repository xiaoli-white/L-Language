package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;

public abstract sealed class LCAbstractLoop extends LCStatementWithScope permits LCDoWhile,LCFor,LCForeach,LCLoop,LCWhile{

    public LCAbstractLoop(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }
}
