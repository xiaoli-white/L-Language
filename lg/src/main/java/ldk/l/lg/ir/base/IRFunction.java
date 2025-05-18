package ldk.l.lg.ir.base;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.type.IRType;

import java.util.Arrays;

public final class IRFunction extends IRNode {
    // public final String[] attributes;
    public final IRType returnType;
    public final String name;
    public final long argumentCount;
    public final IRField[] fields;
    public final IRControlFlowGraph controlFlowGraph;

    public IRFunction(IRType returnType, String name, long argumentCount, IRField[] fields, IRControlFlowGraph controlFlowGraph) {
        this.returnType = returnType;
        this.name = name;
        this.argumentCount = argumentCount;
        this.fields = fields;
        this.controlFlowGraph = controlFlowGraph;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitFunction(this, additional);
    }

    @Override
    public String toString() {
        return "IRFunction{" +
                "returnType=" + returnType +
                ", name='" + name + '\'' +
                ", argumentsCount=" + argumentCount +
                ", fields=" + Arrays.toString(fields) +
                ", controlFlowGraph=" + controlFlowGraph +
                '}';
    }
}
