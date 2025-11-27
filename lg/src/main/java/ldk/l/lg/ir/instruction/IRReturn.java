package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.value.IRValue;

public final class IRReturn extends IRInstruction {
    public IRValue value;
    @Deprecated
    public final IROperand vvalue;

    public IRReturn() {
        this.vvalue = null;
        this.value = null;
    }

    @Deprecated
    public IRReturn(IROperand value) {
        this.vvalue = value;
        this.value = null;
    }

    public IRReturn(IRValue value) {
        this.vvalue = null;
        this.value = value;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitReturn(this, additional);
    }

    @Override
    public String toString() {
        return "return" + (value != null ? " " + value : "");
    }
}
