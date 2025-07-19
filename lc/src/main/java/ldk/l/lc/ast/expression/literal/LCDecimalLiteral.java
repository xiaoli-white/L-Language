package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCDecimalLiteral extends LCLiteral<Double> {
    public LCDecimalLiteral(double value, boolean isFloat, Position pos) {
        this(value, isFloat, pos, false);
    }

    public LCDecimalLiteral(double value, boolean isFloat, Position pos, boolean isErrorNode) {
        super(value, pos, isErrorNode);
        this.theType = isFloat ? SystemTypes.FLOAT : SystemTypes.DOUBLE;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitDecimalLiteral(this, additional);
    }

    @Override
    public LCDecimalLiteral clone() throws CloneNotSupportedException {
        return new LCDecimalLiteral(this.value, this.theType.equals(SystemTypes.FLOAT), this.position.clone(), isErrorNode);
    }
}
