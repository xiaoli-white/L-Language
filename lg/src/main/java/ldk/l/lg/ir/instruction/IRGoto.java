package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRBasicBlock;

public final class IRGoto extends IRInstruction {
    @Deprecated
    public String ttarget;
    public IRBasicBlock target;

    public IRGoto(IRBasicBlock target) {
        this.target = target;
    }
    @Deprecated
    public IRGoto(String target) {
        this.ttarget = target;
        this.target = null;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGoto(this, additional);
    }

    @Override
    public String toString() {
        return "goto label " + target.name;
    }
}
