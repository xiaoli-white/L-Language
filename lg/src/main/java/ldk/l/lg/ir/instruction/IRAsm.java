package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;

public final class IRAsm extends IRInstruction {
    public final String code;
    public final IRType[] types;
    public final IROperand[] resources;
    public final String[] names;

    public IRAsm(String code, IRType[] types, IROperand[] resources, String[] names) {
        this.code = code;
        this.types = types;
        this.resources = resources;
        this.names = names;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitAsm(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("asm \"");
        builder.append(code).append("\"");
        for (int i = 0; i < types.length; i++) {
            builder.append(", ").append("[").append(types[i]).append(", ").append(resources[i]).append(", \"").append(names[i]).append("\"]");
        }
        return builder.toString();
    }
}
