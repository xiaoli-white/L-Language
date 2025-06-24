package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;

public final class IRRealloc extends IRInstruction {
    public final IROperand ptr;
    public final IROperand size;
    public final IRVirtualRegister target;

    public IRRealloc(IROperand ptr, IROperand size, IRVirtualRegister target) {
        this.ptr = ptr;
        this.size = size;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitRealloc(this, additional);
    }

    @Override
    public String toString() {
        return target + " = realloc " + ptr + ", " + size;
    }
}
