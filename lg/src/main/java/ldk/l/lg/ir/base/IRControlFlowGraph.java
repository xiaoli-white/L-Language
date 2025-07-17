package ldk.l.lg.ir.base;

import ldk.l.lg.ir.instruction.IRConditionalJump;
import ldk.l.lg.ir.instruction.IRGoto;
import ldk.l.lg.ir.instruction.IRInstruction;

import java.util.*;

public final class IRControlFlowGraph {
    public final Map<String, BasicBlock> basicBlocks = new LinkedHashMap<>();
    public final Map<BasicBlock, List<BasicBlock>> outEdges = new TreeMap<>();
    public final Map<BasicBlock, List<BasicBlock>> inEdges = new TreeMap<>();

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.put(basicBlock.name, basicBlock);
        outEdges.put(basicBlock, new ArrayList<>());
        inEdges.put(basicBlock, new ArrayList<>());
    }

    public void buildEdges() {
        BasicBlock[] basicBlocks = this.basicBlocks.values().toArray(new BasicBlock[0]);
        for (int i = 0; i < basicBlocks.length - 1; i++) {
            BasicBlock basicBlock = basicBlocks[i];
            IRInstruction last = basicBlock.instructions.isEmpty() ? null : basicBlock.instructions.getLast();
            if (last instanceof IRGoto irGoto) {
                BasicBlock target = this.basicBlocks.get(irGoto.target);
                outEdges.get(basicBlock).add(target);
                inEdges.get(target).add(basicBlock);
            } else if (last instanceof IRConditionalJump irConditionalJump) {
                BasicBlock target = this.basicBlocks.get(irConditionalJump.target);
                BasicBlock next = basicBlocks[i + 1];
                List<BasicBlock> outs = outEdges.get(basicBlock);
                outs.add(target);
                outs.add(next);
                inEdges.get(target).add(basicBlock);
                inEdges.get(next).add(basicBlock);
            } else {
                BasicBlock next = basicBlocks[i + 1];
                outEdges.get(basicBlock).add(next);
                inEdges.get(next).add(basicBlock);
            }
        }
    }

    public void add(IRControlFlowGraph irControlFlowGraph) {
        for (BasicBlock basicBlock : irControlFlowGraph.basicBlocks.values()) {
            this.basicBlocks.put(basicBlock.name, basicBlock);
            if (this.outEdges.containsKey(basicBlock))
                this.outEdges.put(basicBlock, new ArrayList<>(irControlFlowGraph.outEdges.get(basicBlock)));
            if (this.inEdges.containsKey(basicBlock))
                this.inEdges.put(basicBlock, new ArrayList<>(irControlFlowGraph.inEdges.get(basicBlock)));
        }
    }

    @Override
    public String toString() {
        return "IRControlFlowGraph{" +
                "basicBlocks=" + basicBlocks +
                ", outEdges=" + outEdges +
                ", inEdges=" + inEdges +
                '}';
    }

    public static class BasicBlock implements Comparable<BasicBlock> {
        public final String name;
        public final List<IRInstruction> instructions;

        public BasicBlock(String name) {
            this.name = name;
            this.instructions = new LinkedList<>();
        }

        @Override
        public int compareTo(BasicBlock that) {
            return this.name.compareTo(that.name);
        }
    }
}
