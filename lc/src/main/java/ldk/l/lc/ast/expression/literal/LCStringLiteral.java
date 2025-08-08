package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCStringLiteral extends LCLiteral<String> {
    public LCStringLiteral(String value, Position pos) {
        this(value, pos, false);
    }

    public LCStringLiteral(String value, Position pos, boolean isErrorNode) {
        super(value, pos, isErrorNode);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitStringLiteral(this, additional);
    }
}
