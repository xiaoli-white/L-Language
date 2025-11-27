package ldk.l.lg.ir.base;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.constant.IRConstant;

public final class IRGlobalVariable extends IRNode{
    public IRType type;
    public String name;
    public IRConstant initializer;
    public IRGlobalVariable(String name, IRConstant initializer) {
        this.type = initializer.getType();
        this.name = name;
        this.initializer = initializer;
    }
    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGlobalVariable(this, additional);
    }

    @Override
    public String toString() {
        return name+" = "+initializer;
    }
}
