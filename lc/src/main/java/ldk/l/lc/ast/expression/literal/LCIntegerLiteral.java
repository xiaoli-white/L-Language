package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

import java.util.Objects;

public final class LCIntegerLiteral extends LCLiteral<Long> {
    public LCIntegerLiteral(long value, boolean isLong, Position pos) {
        this(value, isLong, pos, false);
    }

    public LCIntegerLiteral(long value, boolean isLong, Position pos, boolean isErrorNode) {
        super(value, pos, isErrorNode);
        if (isLong)
            this.theType = SystemTypes.LONG;
        else
            this.theType = SystemTypes.INT;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitIntegerLiteral(this, additional);
    }

    @Override
    public LCIntegerLiteral clone() throws CloneNotSupportedException {
        return new LCIntegerLiteral(this.value, this.theType.equals(SystemTypes.LONG), this.position.clone(), isErrorNode);
    }
}
