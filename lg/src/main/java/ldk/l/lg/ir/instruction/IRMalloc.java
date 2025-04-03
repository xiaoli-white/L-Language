package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;

public class IRMalloc extends IRInstruction {
    public final IROperand size;
    public final IRVirtualRegister result;

    public IRMalloc(IROperand size, IRVirtualRegister result) {
        this.size = size;
        this.result = result;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitMalloc(this, additional);
    }

    @Override
    public String toString() {
        return result + " = malloc " + size;
    }
}
