package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRValue;

import java.util.List;

public final class IRAssembly extends IRInstruction {
    public final String asm;
    public final String constraints;
    public final List<IRValue> operands;
    @Deprecated
    public final IRType[] types;
    @Deprecated
    public final IROperand[] resources;
    @Deprecated
    public final String[] names;

    @Deprecated
    public IRAssembly(String code, IRType[] types, IROperand[] resources, String[] names) {
        this.asm = code;
        this.types = types;
        this.resources = resources;
        this.names = names;
        this.constraints = null;
        this.operands = null;
    }

    public IRAssembly(String asm, String constraints, List<IRValue> operands) {
        types = null;
        resources = null;
        names = null;
        this.asm = asm;
        this.constraints = constraints;
        this.operands = operands;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitAssembly(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("asm \"");
        builder.append(asm).append("\", \"").append(constraints).append("\"(");
        for (int i = 0; i < operands.size(); i++) {
            builder.append(operands.get(i));
            if (i < operands.size() - 1) builder.append(", ");
        }
        builder.append(')');
        return builder.toString();
    }
}
