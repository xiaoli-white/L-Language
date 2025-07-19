package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCCharLiteral extends LCLiteral<Character> {
    public LCCharLiteral(Character value, Position pos) {
        this(value, pos, false);
    }

    public LCCharLiteral(Character value, Position pos, boolean isErrorNode) {
        super(value, pos, isErrorNode);
        this.theType = SystemTypes.CHAR;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitCharLiteral(this, additional);
    }

    @Override
    public LCCharLiteral clone() throws CloneNotSupportedException {
        return new LCCharLiteral(this.value, this.position.clone(), isErrorNode);
    }
}
