package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;

@Deprecated
public final class IRFree extends IRInstruction {
    public final IROperand ptr;

    public IRFree(IROperand ptr) {
        this.ptr = ptr;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitFree(this, additional);
    }

    @Override
    public String toString() {
        return "free " + ptr;
    }
}
