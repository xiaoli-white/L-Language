package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;

public final class IRStringConstant extends IRConstant {
    public final IRType type;
    public final String value;
    public IRStringConstant(String value) {
        this.type = new IRPointerType(IRType.getCharType());
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
        return "string \""+ value+"\"";
    }
}
