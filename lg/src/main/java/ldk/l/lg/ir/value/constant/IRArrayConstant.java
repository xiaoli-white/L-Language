package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRArrayType;
import ldk.l.lg.ir.type.IRType;

import java.util.List;

public final class IRArrayConstant extends IRConstant{
    public final IRArrayType type;
    public final List<IRConstant> values;
    public IRArrayConstant(IRArrayType type, List<IRConstant> values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public IRType getType() {
        return type;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitArrayConstant(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(type.toString());
        sb.append(" [");
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i != values.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
