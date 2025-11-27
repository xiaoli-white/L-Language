package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;

public final class IRDoubleConstant extends IRConstant {
    public double value;

    public IRDoubleConstant(double value) {
        this.value = value;
    }

    @Override
    public IRType getType() {
        return IRType.getDoubleType();
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return "double " + value;
    }
}
