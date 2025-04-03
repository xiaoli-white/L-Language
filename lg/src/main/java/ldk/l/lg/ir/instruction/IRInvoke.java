package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public class IRInvoke extends IRInstruction {
    public IRType returnType;
    public IROperand address;
    public IRType[] argumentTypes;
    public IROperand[] arguments;
    public IRVirtualRegister result;

    public IRInvoke(IRType returnType, IROperand address, IRType[] argumentTypes, IROperand[] arguments, IRVirtualRegister result) {
        this.returnType = returnType;
        this.address = address;
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
        this.result = result;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitInvoke(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (result != null) builder.append(result).append(" = ");
        builder.append("invoke ").append(returnType).append(" ").append(address);
        for (int i = 0; i < arguments.length; i++) {
            builder.append(", [").append(argumentTypes[i]).append(", ").append(arguments[i]).append("]");
        }
        return builder.toString();
    }
}
