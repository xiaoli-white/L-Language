package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;

public class IRSetVirtualRegister extends IRInstruction {
    public final IROperand source;
    public final IRVirtualRegister target;

    public IRSetVirtualRegister(IROperand source, IRVirtualRegister target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitSetVirtualRegister(this, additional);
    }

    @Override
    public String toString() {
        return target + " = " + source;
    }
}
