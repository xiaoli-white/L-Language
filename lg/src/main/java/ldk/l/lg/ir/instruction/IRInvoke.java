package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public final class IRInvoke extends IRInstruction {
    public final IRType returnType;
    public final IROperand address;
    public final IRType[] argumentTypes;
    public final IROperand[] arguments;
    public final IRVirtualRegister target;

    public IRInvoke(IRType returnType, IROperand address, IRType[] argumentTypes, IROperand[] arguments, IRVirtualRegister target) {
        this.returnType = returnType;
        this.address = address;
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitInvoke(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (target != null) builder.append(target).append(" = ");
        builder.append("invoke ").append(returnType).append(" ").append(address);
        for (int i = 0; i < arguments.length; i++) {
            builder.append(", [").append(argumentTypes[i]).append(", ").append(arguments[i]).append("]");
        }
        return builder.toString();
    }
}
