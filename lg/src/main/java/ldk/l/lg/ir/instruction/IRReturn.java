package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;

public class IRReturn extends IRInstruction {
    public final IROperand value;

    public IRReturn(IROperand value) {
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
