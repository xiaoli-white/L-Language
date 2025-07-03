package com.xiaoli.bcg.bytecode;

import com.xiaoli.bcg.bytecode.instruction.BCInstruction;
import com.xiaoli.bcg.bytecode.operand.immediate.BCImmediate8;
import ldk.l.lvm.vm.ByteCode;

import java.util.*;

public final class BCControlFlowGraph {
    public final Map<String, BasicBlock> basicBlocks = new LinkedHashMap<>();
    public final Map<BasicBlock, List<BasicBlock>> outEdges = new TreeMap<>();
    public final Map<BasicBlock, List<BasicBlock>> inEdges = new TreeMap<>();
    public final Map<BCInstruction, List<Long>> instruction2LiveRegisters = new HashMap<>();
    public final Map<Long, List<Long>> interferenceGraph = new TreeMap<>();
    public final List<Byte> usedColors = new ArrayList<>();
    public final Map<Long, Byte> colorMap = new TreeMap<>();
    public final List<Long> spilledVirtualRegisters = new ArrayList<>();
    public final Map<String, Long> basicBlock2EntryPoint = new LinkedHashMap<>();

    public void addBasicBlock(BCControlFlowGraph.BasicBlock basicBlock) {
        basicBlocks.put(basicBlock.name, basicBlock);
        outEdges.put(basicBlock, new ArrayList<>());
        inEdges.put(basicBlock, new ArrayList<>());
    }

    public void buildEdges() {
        BCControlFlowGraph.BasicBlock[] basicBlocks = this.basicBlocks.values().toArray(new BCControlFlowGraph.BasicBlock[0]);
        for (int i = 0; i < basicBlocks.length - 1; i++) {
            BCControlFlowGraph.BasicBlock basicBlock = basicBlocks[i];
            int index = basicBlock.instructions.size() - 1;
            BCInstruction last = index < 0 ? null : basicBlock.instructions.get(index);
            if (last != null) {
                if (last.code == ByteCode.JUMP_IMMEDIATE) {
                    BCImmediate8 imm = (BCImmediate8) last.operand1;
                    if (imm.comment.startsWith("<basic_block>")) {
                        BasicBlock next = this.basicBlocks.get(imm.comment.substring("<basic_block>".length()));
                        outEdges.get(basicBlock).add(next);
                        inEdges.get(next).add(basicBlock);
                        continue;
                    }
                } else if (ByteCode.isJump(last.code)) {
                    if (index - 1 >= 0) {
                        BCInstruction prev = basicBlock.instructions.get(index - 1);
                        BCImmediate8 imm = (BCImmediate8) prev.operand1;
                        if (imm.comment.startsWith("<basic_block>")) {
                            BasicBlock target = this.basicBlocks.get(imm.comment.substring("<basic_block>".length()));
                            outEdges.get(basicBlock).add(target);
                            inEdges.get(target).add(basicBlock);
                            if (last.code == ByteCode.JUMP) continue;
                        }
                    }
                }
            }
            BCControlFlowGraph.BasicBlock next = basicBlocks[i + 1];
            outEdges.get(basicBlock).add(next);
            inEdges.get(next).add(basicBlock);
        }
    }

    public static class BasicBlock implements Comparable<BasicBlock> {
        public final String name;
        public List<BCInstruction> instructions;

        public BasicBlock(String name) {
            this.name = name;
            this.instructions = new ArrayList<>();
        }

        @Override
        public int compareTo(BasicBlock that) {
            return this.name.compareTo(that.name);
        }
    }
}