package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;

public class IRStackAllocate extends IRInstruction {
    public final IROperand size;
    public final IRVirtualRegister target;

    public IRStackAllocate(IROperand size, IRVirtualRegister target) {
        this.size = size;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitStackAllocate(this, additional);
    }

    @Override
    public String toString() {
        return target + " = stack_alloc " + size;
    }
}
