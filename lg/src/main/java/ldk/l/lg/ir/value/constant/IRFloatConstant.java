package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;

public final  class IRFloatConstant extends IRConstant{
    public final float value;
    public IRFloatConstant(float value) {
        this.value = value;
    }
    @Override
    public IRType getType() {
        return IRType.getFloatType();
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return "float "+ value;
    }
}
