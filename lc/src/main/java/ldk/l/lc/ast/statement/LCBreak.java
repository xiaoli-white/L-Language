package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

public final class LCBreak extends LCStatement {
    public String label;

    public LCBreak(String label, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.label = label;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitBreak(this, additional);
    }

    @Override
    public String toString() {
        return "LCBreak{" +
                "label='" + label + '\'' +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
