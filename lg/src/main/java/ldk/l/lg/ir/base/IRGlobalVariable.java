package ldk.l.lg.ir.base;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.constant.IRConstant;

import java.util.List;

public final class IRGlobalVariable extends IRNode {
    public List<String> attributes;
    public boolean isExtern;
    public boolean isConstant;
    public IRType type;
    public String name;
    public IRConstant initializer;

    public IRGlobalVariable(List<String> attributes, boolean isConstant, String name, IRType type, IRConstant initializer) {
        this.attributes = attributes;
        this.isConstant = isConstant;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        isExtern = false;
    }

    public IRGlobalVariable(List<String> attributes, boolean isConstant, String name, IRConstant initializer) {
        this(attributes, isConstant, name, initializer == null ? null : initializer.getType(), initializer);
    }

    public IRGlobalVariable(List<String> attributes, boolean isConstant, String name, IRType type) {
        this.attributes = attributes;
        this.isConstant = isConstant;
        this.type = type;
        this.name = name;
        this.initializer = null;
        isExtern = true;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGlobalVariable(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String attribute : attributes) {
            sb.append("__attribute__(\"").append(attribute).append("\") ");
        }
        if (isExtern) sb.append("extern ");
        if (isConstant) sb.append("constant ");
        sb.append("global \"").append(name).append("\"");
        if (isExtern) sb.append(": ").append(type);
        else sb.append(" = ").append(initializer);
        return sb.toString();
    }

    public void setInitializer(IRConstant initializer) {
        this.initializer = initializer;
        type = initializer.getType();
    }
}
