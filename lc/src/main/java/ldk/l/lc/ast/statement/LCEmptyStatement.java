package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

import java.util.ArrayList;

public final class LCEmptyStatement extends LCStatement {
    public LCEmptyStatement(Position pos) {
        super(pos, false);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitEmptyStatement(this, additional);
    }

    @Override
    public String toString() {
        return "LCEmptyStatement";
    }
}