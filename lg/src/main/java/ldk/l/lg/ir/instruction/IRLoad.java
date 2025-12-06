package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRLoad extends IRInstruction {
    @Deprecated
    public final IROperand address;
    @Deprecated
    public final IRVirtualRegister ttarget;
    public IRValue ptr;
    public IRRegister target;

    @Deprecated
    public IRLoad(IRType type, IROperand address, IRVirtualRegister target) {
        this.address = address;
        this.ttarget = target;
        this.ptr = null;
        this.target = null;
    }
    public IRLoad(IRValue ptr, IRRegister target) {
        this.address = null;
        this.ttarget = null;
        this.ptr = ptr;
        this.target = target;
        target.type = ((IRPointerType)ptr.getType()).base;
        target.def = this;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitLoad(this, additional);
    }

    @Override
    public String toString() {
        return "%\""+target.name + "\" = load " + ptr;
    }
}
