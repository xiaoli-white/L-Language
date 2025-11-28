package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRStackAllocate extends IRInstruction {
    @Deprecated
    public final IROperand ssize;
    @Deprecated
    public final IRVirtualRegister ttarget;
    public IRType type;
    public IRValue size;
    public IRRegister target;

    @Deprecated
    public IRStackAllocate(IROperand size, IRVirtualRegister target) {
        this.ssize = size;
        this.ttarget = target;
        this.size = null;
        this.target = null;
    }

    public IRStackAllocate(IRType type, IRValue size, IRRegister target) {
        this.ssize = null;
        this.ttarget = null;
        this.type = type;
        this.size = size;
        this.target = target;
        target.type = new IRPointerType(type);
        target.def = this;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitStackAllocate(this, additional);
    }

    @Override
    public String toString() {
        return "%" + target.name + " = stack_alloc " + type + (size != null ? ", " + size : "");
    }
}
