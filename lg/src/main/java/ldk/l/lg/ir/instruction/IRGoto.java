package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;

public final class IRGoto extends IRInstruction {
    public String target;

    public IRGoto(String target) {
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGoto(this, additional);
    }

    @Override
    public String toString() {
        return "goto #" + target;
    }
}
