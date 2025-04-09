package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public final class LCNullptrLiteral extends LCLiteral<Object> {
    public LCNullptrLiteral(Position pos) {
        this(pos, false);
    }

    public LCNullptrLiteral(Position pos, boolean isErrorNode) {
        super(null, pos, isErrorNode);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNullptrLiteral(this, additional);
    }

    @Override
    public LCNullptrLiteral clone() throws CloneNotSupportedException {
        return new LCNullptrLiteral(this.position.clone(), isErrorNode);
    }
}
