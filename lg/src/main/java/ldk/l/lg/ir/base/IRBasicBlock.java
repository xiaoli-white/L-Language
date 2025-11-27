package ldk.l.lg.ir.base;

import ldk.l.lg.ir.instruction.IRInstruction;

import java.util.LinkedList;
import java.util.List;

public class IRBasicBlock implements Comparable<IRBasicBlock> {
    public IRControlFlowGraph cfg = null;
    public String name;
    public List<IRInstruction> instructions;

    public IRBasicBlock(String name) {
        this.name = name;
        this.instructions = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "BasicBlock{" +
                "name='" + name + '\'' +
                ", instructions=" + instructions +
                '}';
    }

    @Override
    public int compareTo(IRBasicBlock that) {
        return this.name.compareTo(that.name);
    }
}
