package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCCharLiteral extends LCLiteral<Integer> {
    public LCCharLiteral(Integer value, Position pos) {
        this(value, pos, false);
    }

    public LCCharLiteral(Integer value, Position pos, boolean isErrorNode) {
        super(value, pos, isErrorNode);
        this.theType = SystemTypes.CHAR;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitCharLiteral(this, additional);
    }
}
