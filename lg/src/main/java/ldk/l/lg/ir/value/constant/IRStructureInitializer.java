package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRStructureType;
import ldk.l.lg.ir.type.IRType;

import java.util.List;

public final class IRStructureInitializer extends IRConstant {
    public IRStructureType type;
    public List<IRConstant> elements;

    public IRStructureInitializer(IRStructureType type, List<IRConstant> elements) {
        this.type = type;
        this.elements = elements;
    }

    @Override
    public IRType getType() {
        return type;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitStructureInitializer(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("constant ");
        sb.append(type).append(" {");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i));
            if (i < elements.size() - 1) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
