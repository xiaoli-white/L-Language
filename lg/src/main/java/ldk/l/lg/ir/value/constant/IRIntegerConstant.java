package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRIntegerType;
import ldk.l.lg.ir.type.IRType;

public final class IRIntegerConstant extends IRConstant {
    public final IRIntegerType type;
    public final long value;

    public IRIntegerConstant(IRIntegerType type, long value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public IRType getType() {
        return type;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}
