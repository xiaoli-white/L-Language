package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCNullLiteral extends LCLiteral<Object> {
    public LCNullLiteral(Position pos) {
        this(pos, false);
    }

    public LCNullLiteral(Position pos, boolean isErrorNode) {
        super(null, pos, isErrorNode);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNullLiteral(this, additional);
    }
}
