package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

import java.util.List;

public final class IRInvoke extends IRInstruction {
    @Deprecated
    public final IROperand address;
    @Deprecated
    public final IRType[] aargumentTypes;
    @Deprecated
    public final IROperand[] aarguments;
    @Deprecated
    public final IRVirtualRegister ttarget;
    public final IRType returnType;
    public final IRValue func;
    public final List<IRValue> arguments;
    public final IRRegister target;

    @Deprecated
    public IRInvoke(IRType returnType, IROperand address, IRType[] argumentTypes, IROperand[] arguments, IRVirtualRegister target) {
        this.returnType = returnType;
        this.address = address;
        this.aargumentTypes = argumentTypes;
        this.aarguments = arguments;
        this.ttarget = target;
        this.func = null;
        this.arguments = null;
        this.target = null;
    }

    public IRInvoke(IRType returnType, IRValue func, List<IRValue> arguments, IRRegister target) {
        this.returnType = returnType;
        this.func = func;
        this.arguments = arguments;
        this.target = target;
        if (target!=null) {
            target.def = this;
            target.type = returnType;
        }
        this.address = null;
        this.aargumentTypes = null;
        this.aarguments = null;
        this.ttarget = null;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitInvoke(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (target != null) builder.append("%").append(target.name).append(" = ");
        builder.append("invoke ").append(returnType).append(" ").append(func).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            builder.append(arguments.get(i));
            if (i < arguments.size() - 1) builder.append(", ");
        }
        builder.append(")");
        return builder.toString();
    }
}
