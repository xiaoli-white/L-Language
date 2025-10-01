package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;

public final class IRNoOperate extends IRInstruction {
    public IRNoOperate() {

    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitNoOperate(this, additional);
    }

    @Override
    public String toString() {
        return "nop";
    }
}
