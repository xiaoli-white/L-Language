package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCBooleanLiteral extends LCLiteral<Boolean> {
    public LCBooleanLiteral(boolean value, Position pos) {
        this(value, pos, false);
    }

    public LCBooleanLiteral(boolean value, Position pos, boolean isErrorNode) {
        super(value, pos, isErrorNode);
        this.theType = SystemTypes.BOOLEAN;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitBooleanLiteral(this, additional);
    }

    @Override
    public LCBooleanLiteral clone() throws CloneNotSupportedException {
        return new LCBooleanLiteral(this.value, this.position.clone(), isErrorNode);
    }
}
