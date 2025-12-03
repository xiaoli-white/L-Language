package ldk.l.lg.ir.base;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.constant.IRConstant;

public final class IRGlobalVariable extends IRNode {
    public boolean isExtern;
    public boolean isConstant;
    public IRType type;
    public String name;
    public IRConstant initializer;

    public IRGlobalVariable(boolean isConstant, String name, IRType type, IRConstant initializer) {
        this.isConstant = isConstant;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        isExtern = false;
    }

    public IRGlobalVariable(boolean isConstant, String name, IRConstant initializer) {
        this(isConstant, name, initializer.getType(), initializer);
    }

    public IRGlobalVariable(boolean isConstant, String name, IRType type) {
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
        return (isExtern ? "extern " : "") + (isConstant ? "constant " : "") + "global " + name + (isExtern ? ": " + type : " = " + initializer);
    }
}
