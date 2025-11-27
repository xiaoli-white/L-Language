package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;

public final class IRPhi extends IROperand {
    public final IRType type;
    public final String[] labels;
    public final IROperand[] operands;

    public IRPhi(IRType type, String[] labels, IROperand[] operands) {
        this.type = type;
        this.labels = labels;
        this.operands = operands;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("phi ");
        builder.append(type).append(" ");
        for (int i = 0; i < labels.length; i++) {
            builder.append("[").append(labels[i]).append(", ").append(operands[i]).append("]");
            if (i < labels.length - 1)
                builder.append(", ");
        }
        return builder.toString();
    }
}
