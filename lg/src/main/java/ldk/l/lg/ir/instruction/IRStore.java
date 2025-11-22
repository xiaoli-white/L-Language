package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRValue;

public final class IRStore extends IRInstruction {
    public final IRType type;
    @Deprecated
    public final IROperand address;
    public final IRValue ptr;
    public final IRValue value;
    @Deprecated
    public final IROperand vvalue;

    @Deprecated
    public IRStore(IRType type, IROperand address, IROperand value) {
        this.type = type;
        this.address = address;
        this.ptr = null;
        this.vvalue = value;
        this.value = null;
    }

    public IRStore(IRValue ptr, IRValue value) {
        this.type = null;
        this.address = null;
        this.ptr = ptr;
        this.vvalue = null;
        this.value = value;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitStore(this, additional);
    }

    @Override
    public String toString() {
        return "store "  + ptr + ", " + value;
    }
}
