package ldk.l.lg.ir.base;

import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.instruction.IRConditionalJump;
import ldk.l.lg.ir.instruction.IRGoto;
import ldk.l.lg.ir.instruction.IRInstruction;
import ldk.l.lg.ir.instruction.IRReturn;

import java.util.*;

public final class IRControlFlowGraph {
    public IRFunction function;
    public final Map<String, IRBasicBlock> basicBlocks = new LinkedHashMap<>();
    public final Map<IRBasicBlock, List<IRBasicBlock>> predecessors = new TreeMap<>();
    public final Map<IRBasicBlock, List<IRBasicBlock>> successors = new TreeMap<>();

    //    public final Map<String, Object>
//    public IRControlFlowGraph(IRFunction function) {
    public IRControlFlowGraph() {
        this.function = null;
    }

    public void addBasicBlock(IRBasicBlock basicBlock) {
        basicBlocks.put(basicBlock.name, basicBlock);
        successors.put(basicBlock, new ArrayList<>());
        predecessors.put(basicBlock, new ArrayList<>());
        basicBlock.cfg = this;
    }

    public void removeBasicBlock(IRBasicBlock basicBlock) {
        basicBlocks.remove(basicBlock.name);
        List<IRBasicBlock> outs = successors.remove(basicBlock);
        for (IRBasicBlock outBasicBlock : outs) predecessors.get(outBasicBlock).remove(basicBlock);
        List<IRBasicBlock> ins = predecessors.remove(basicBlock);
        for (IRBasicBlock inBasicBlock : ins) successors.get(inBasicBlock).remove(basicBlock);
    }

    public void buildEdges() {
        IRBasicBlock[] basicBlocks = this.basicBlocks.values().toArray(new IRBasicBlock[0]);
        for (int i = 0; i < basicBlocks.length - 1; i++) {
            IRBasicBlock basicBlock = basicBlocks[i];
            IRInstruction last = basicBlock.instructions.isEmpty() ? null : basicBlock.instructions.getLast();
            if (last instanceof IRGoto irGoto) {
                IRBasicBlock target = this.basicBlocks.get(irGoto.ttarget);
                if (target == null) continue;
                successors.get(basicBlock).add(target);
                predecessors.get(target).add(basicBlock);
            } else if (last instanceof IRConditionalJump irConditionalJump) {
                IRBasicBlock target = this.basicBlocks.get(irConditionalJump.ttarget);
                if (target == null) continue;
                IRBasicBlock next = basicBlocks[i + 1];
                List<IRBasicBlock> outs = successors.get(basicBlock);
                outs.add(target);
                outs.add(next);
                predecessors.get(target).add(basicBlock);
                predecessors.get(next).add(basicBlock);
            } else if (!(last instanceof IRReturn)) {
                IRBasicBlock next = basicBlocks[i + 1];
                successors.get(basicBlock).add(next);
                predecessors.get(next).add(basicBlock);
            }
        }
    }

    public void add(IRControlFlowGraph irControlFlowGraph) {
        for (IRBasicBlock basicBlock : irControlFlowGraph.basicBlocks.values()) {
            this.basicBlocks.put(basicBlock.name, basicBlock);
            if (this.successors.containsKey(basicBlock))
                this.successors.put(basicBlock, new ArrayList<>(irControlFlowGraph.successors.get(basicBlock)));
            if (this.predecessors.containsKey(basicBlock))
                this.predecessors.put(basicBlock, new ArrayList<>(irControlFlowGraph.predecessors.get(basicBlock)));
        }
    }

    @Override
    public String toString() {
        return "IRControlFlowGraph{" +
                "basicBlocks=" + basicBlocks +
                ", outEdges=" + successors +
                ", inEdges=" + predecessors +
                '}';
    }

}
