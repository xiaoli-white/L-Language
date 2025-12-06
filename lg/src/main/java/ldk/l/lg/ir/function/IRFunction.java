package ldk.l.lg.ir.function;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.type.IRType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IRFunction extends IRNode {
    private final Map<String, IRLocalVariable> name2LocalVariable = new HashMap<>();
    public List<String> attributes;
    public boolean isExtern;
    public IRType returnType;
    public String name;
    public List<IRLocalVariable> args;
    public boolean isVarArg;
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

    public IRFunction(IRType returnType, String name, List<IRLocalVariable> args, boolean isVarArg, List<IRLocalVariable> locals, IRControlFlowGraph controlFlowGraph) {
        this(List.of(), returnType, name, args, isVarArg, locals, controlFlowGraph);
    }

    public IRFunction(List<String> attributes, IRType returnType, String name, List<IRLocalVariable> args, boolean isVarArg, List<IRLocalVariable> locals, IRControlFlowGraph controlFlowGraph) {
        this.argumentCount = 0;
        this.fields = null;
        this.attributes = attributes;
        this.returnType = returnType;
        this.name = name;
        this.args = args;
        this.isVarArg = isVarArg;
        this.locals = locals;
        this.controlFlowGraph = controlFlowGraph;
        for (IRLocalVariable arg : args) name2LocalVariable.put(arg.name, arg);
        if (controlFlowGraph != null) {
            controlFlowGraph.function = this;
            isExtern = false;
            for (IRLocalVariable local : locals) name2LocalVariable.put(local.name, local);
        } else {
            isExtern = true;
        }
    }

    public IRFunction(List<String> attributes, IRType returnType, String name, List<IRLocalVariable> args, boolean isVarArg) {
        this(attributes, returnType, name, args, isVarArg, null, null);
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

    public IRLocalVariable getLocalVariable(String name) {
        return name2LocalVariable.get(name);
    }

    public void addArg(IRLocalVariable arg) {
        name2LocalVariable.put(arg.name, arg);
        args.add(arg);
    }

    public void addLocal(IRLocalVariable local) {
        name2LocalVariable.put(local.name, local);
        locals.add(local);
    }
}
