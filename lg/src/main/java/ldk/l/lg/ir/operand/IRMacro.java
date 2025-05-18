package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;

public final class IRMacro extends IROperand {
    private IRType type = null;
    public final String name;
    public final String[] args;

    public IRMacro(String name, String[] args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitMacro(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("`").append(name).append("(");
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1)
                builder.append(", ");
        }
        builder.append(")");
        return builder.toString();
    }

    public boolean typeIsInitial() {
        return type != null;
    }

    public IRType type() {
        if (type == null)
            throw new NullPointerException("The field 'type' not be initial.");
        return type;
    }

    public void setType(IRType type) {
        if (this.type != null && !this.type.equals(type))
            throw new NullPointerException("The field 'type' has been initial.");
        this.type = type;
    }
}
