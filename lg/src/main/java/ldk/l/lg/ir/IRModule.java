package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRGlobalVariable;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.base.IRGlobalDataSection;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.structure.IRStructure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class IRModule extends IRNode {
    public final Map<String, IRGlobalVariable> globals = new LinkedHashMap<>();
    public final Map<String, IRStructure> structures = new LinkedHashMap<>();
    public final Map<String, IRFunction> functions = new LinkedHashMap<>();
    @Deprecated
    public final IRConstantPool constantPool = new IRConstantPool();
    @Deprecated
    public final IRGlobalDataSection globalDataSection = new IRGlobalDataSection();
   @Deprecated
    public final Map<String, List<String>> name2VTableKeys = new LinkedHashMap<>();
   @Deprecated
    public final Map<String, List<String>> name2ITableKeys = new LinkedHashMap<>();

    public IRModule() {
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitModule(this, additional);
    }

    @Override
    public String toString() {
        return "IRModule{" +
                "globals=" + globals +
                ", structures=" + structures +
                ", functions=" + functions +
                '}';
    }

    public void putGlobalVariable(IRGlobalVariable globalVariable) {
        if (this.globals.containsKey(globalVariable.name)) {
            throw new RuntimeException("Global variable " + globalVariable.name + " already exists");
        }
        this.globals.put(globalVariable.name, globalVariable);
    }
    public void putStructure(IRStructure structure) {
        if (this.structures.containsKey(structure.name)) {
            throw new RuntimeException("Structure " + structure.name + " already exists");
        }
        this.structures.put(structure.name, structure);
    }

    public void putFunction(IRFunction function) {
        if (this.functions.containsKey(function.name)) {
            throw new RuntimeException("Function " + function.name + " already exists");
        }
        this.functions.put(function.name, function);
    }
}
