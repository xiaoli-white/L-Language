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
    public final Map<IRBasicBlock, List<IRBasicBlock>> outEdges = new TreeMap<>();
    public final Map<IRBasicBlock, List<IRBasicBlock>> inEdges = new TreeMap<>();

    //    public final Map<String, Object>
//    public IRControlFlowGraph(IRFunction function) {
    public IRControlFlowGraph() {
        this.function = null;
    }

    public void addBasicBlock(IRBasicBlock basicBlock) {
        basicBlocks.put(basicBlock.name, basicBlock);
        outEdges.put(basicBlock, new ArrayList<>());
        inEdges.put(basicBlock, new ArrayList<>());
        basicBlock.cfg = this;
    }

    public void removeBasicBlock(IRBasicBlock basicBlock) {
        basicBlocks.remove(basicBlock.name);
        List<IRBasicBlock> outs = outEdges.remove(basicBlock);
        for (IRBasicBlock outBasicBlock : outs) inEdges.get(outBasicBlock).remove(basicBlock);
        List<IRBasicBlock> ins = inEdges.remove(basicBlock);
        for (IRBasicBlock inBasicBlock : ins) outEdges.get(inBasicBlock).remove(basicBlock);
    }

    public void buildEdges() {
        IRBasicBlock[] basicBlocks = this.basicBlocks.values().toArray(new IRBasicBlock[0]);
        for (int i = 0; i < basicBlocks.length - 1; i++) {
            IRBasicBlock basicBlock = basicBlocks[i];
            IRInstruction last = basicBlock.instructions.isEmpty() ? null : basicBlock.instructions.getLast();
            if (last instanceof IRGoto irGoto) {
                IRBasicBlock target = this.basicBlocks.get(irGoto.ttarget);
                if (target == null) continue;
                outEdges.get(basicBlock).add(target);
                inEdges.get(target).add(basicBlock);
            } else if (last instanceof IRConditionalJump irConditionalJump) {
                IRBasicBlock target = this.basicBlocks.get(irConditionalJump.ttarget);
                if (target == null) continue;
                IRBasicBlock next = basicBlocks[i + 1];
                List<IRBasicBlock> outs = outEdges.get(basicBlock);
                outs.add(target);
                outs.add(next);
                inEdges.get(target).add(basicBlock);
                inEdges.get(next).add(basicBlock);
            } else if (!(last instanceof IRReturn)) {
                IRBasicBlock next = basicBlocks[i + 1];
                outEdges.get(basicBlock).add(next);
                inEdges.get(next).add(basicBlock);
            }
        }
    }

    public void add(IRControlFlowGraph irControlFlowGraph) {
        for (IRBasicBlock basicBlock : irControlFlowGraph.basicBlocks.values()) {
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

}
