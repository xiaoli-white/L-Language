package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRFunction;
import ldk.l.lg.ir.base.IRGlobalDataSection;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.structure.IRStructure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IRModule extends IRNode {
    public final Map<String, IRStructure> structures = new LinkedHashMap<>();
    public final IRConstantPool constantPool = new IRConstantPool();
    public final IRGlobalDataSection globalDataSection = new IRGlobalDataSection();
    public final IRControlFlowGraph globalInitSection = new IRControlFlowGraph();
    public final Map<String, IRFunction> functions = new LinkedHashMap<>();
    public final Map<String, List<String>> name2VTableKeys = new LinkedHashMap<>();
    public final Map<String, List<String>> name2ITableKeys = new LinkedHashMap<>();
    public String entryPoint = null;

    public IRModule() {
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitModule(this, additional);
    }

    @Override
    public String toString() {
        return "IRModule{" +
                "structures=" + structures +
                ", constantPool=" + constantPool +
                ", globalDataSection=" + globalDataSection +
                ", globalInitSection=" + globalInitSection +
                ", functions=" + functions +
                ", entryPoint='" + entryPoint + '\'' +
                '}';
    }

    public void putStructure(IRStructure structure) {
        this.structures.put(structure.name, structure);
    }

    public void putFunction(IRFunction function) {
        this.functions.put(function.name, function);
    }
}
