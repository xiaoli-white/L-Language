package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;

public final class IRNop extends IRInstruction {
    public IRNop() {

    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitNop(this, additional);
    }

    @Override
    public String toString() {
        return "nop";
    }
}
