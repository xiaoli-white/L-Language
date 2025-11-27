package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRSetRegister extends IRInstruction {
    @Deprecated
    public final IROperand source;
    @Deprecated
    public final IRVirtualRegister ttarget;
    public IRValue value;
    public IRRegister target;

    @Deprecated
    public IRSetRegister(IROperand source, IRVirtualRegister target) {
        this.source = source;
        this.ttarget = target;
        this.value = null;
        this.target = null;
    }
    public IRSetRegister(IRValue value, IRRegister target) {
        this.source = null;
        this.ttarget = null;
        this.value = value;
        this.target = target;
        target.type = value.getType();
        target.def = this;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitSetRegister(this, additional);
    }

    @Override
    public String toString() {
        return "%"+target.name + " = " + value;
    }
}
