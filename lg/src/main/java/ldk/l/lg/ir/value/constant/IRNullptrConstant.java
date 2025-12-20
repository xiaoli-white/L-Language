package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;

public final class IRNullptrConstant extends IRConstant {
    public IRPointerType type;

    public IRNullptrConstant(IRPointerType type) {
        this.type = type;
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
        return type + " nullptr";
    }
}
