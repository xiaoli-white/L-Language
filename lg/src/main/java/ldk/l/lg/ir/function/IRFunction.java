package ldk.l.lg.ir.function;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.type.IRType;

import java.util.Arrays;
import java.util.List;

public final class IRFunction extends IRNode {
    public List<String> attributes;
    public boolean isExtern;
    public IRType returnType;
    public String name;
    public List<IRLocalVariable> args;
    public List<IRLocalVariable> locals;
    @Deprecated
    public final long argumentCount;
    @Deprecated
    public IRField[] fields;
    public IRControlFlowGraph controlFlowGraph;
    public transient long registerCount = 0;

    @Deprecated
    public IRFunction(IRType returnType, String name, long argumentCount, IRField[] fields, IRControlFlowGraph controlFlowGraph) {
        this(List.of(), returnType, name, argumentCount, fields, controlFlowGraph);
    }

    @Deprecated
    public IRFunction(List<String> attributes, IRType returnType, String name, long argumentCount, IRField[] fields, IRControlFlowGraph controlFlowGraph) {
        this.attributes = attributes;
        this.returnType = returnType;
        this.name = name;
        this.argumentCount = argumentCount;
        this.fields = fields;
        this.controlFlowGraph = controlFlowGraph;
        controlFlowGraph.function = this;
        this.args = null;
        this.locals = null;
    }

    public IRFunction(IRType returnType, String name, List<IRLocalVariable> args, List<IRLocalVariable> locals, IRControlFlowGraph controlFlowGraph) {
        this(List.of(), returnType, name, args, locals, controlFlowGraph);
    }

    public IRFunction(List<String> attributes, IRType returnType, String name, List<IRLocalVariable> args, List<IRLocalVariable> locals, IRControlFlowGraph controlFlowGraph) {
        this.argumentCount = 0;
        this.fields = null;
        this.attributes = attributes;
        this.returnType = returnType;
        this.name = name;
        this.args = args;
        this.locals = locals;
        this.controlFlowGraph = controlFlowGraph;
        if (controlFlowGraph != null) {
            controlFlowGraph.function = this;
            isExtern = false;
        } else {
            isExtern = true;
        }
    }

    public IRFunction(List<String> attributes, IRType returnType, String name, List<IRLocalVariable> args, List<IRLocalVariable> locals) {
        this(attributes, returnType, name, args, locals, null);
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitFunction(this, additional);
    }

    @Override
    public String toString() {
        return "IRFunction{" +
                "attributes=" + attributes +
                ", returnType=" + returnType +
                ", name='" + name + '\'' +
                ", args=" + args +
                ", locals=" + locals +
                ", controlFlowGraph=" + controlFlowGraph +
                ", registerCount=" + registerCount +
                '}';
    }

    public void addBasicBlock(IRBasicBlock basicBlock) {
        controlFlowGraph.addBasicBlock(basicBlock);
    }
}
