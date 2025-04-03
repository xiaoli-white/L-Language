package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public class IRPop extends IRInstruction {
    public final IRType type;
    public final IRVirtualRegister target;

    public IRPop(IRType type, IRVirtualRegister target) {
        this.type = type;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitPop(this, additional);
    }

    @Override
    public String toString() {
        return target + " = pop " + type;
    }
}
