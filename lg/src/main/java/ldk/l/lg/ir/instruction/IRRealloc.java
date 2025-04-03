package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;

public class IRRealloc extends IRInstruction {
    public final IROperand ptr;
    public final IROperand size;
    public final IRVirtualRegister result;

    public IRRealloc(IROperand ptr, IROperand size, IRVirtualRegister result) {
        this.ptr = ptr;
        this.size = size;
        this.result = result;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitRealloc(this, additional);
    }

    @Override
    public String toString() {
        return result + " = realloc " + ptr + ", " + size;
    }
}
