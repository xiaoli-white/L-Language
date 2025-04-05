package com.xiaoli.bcg.bytecode;

import com.xiaoli.bcg.bytecode.instruction.BCInstruction;
import com.xiaoli.bcg.bytecode.operand.immediate.BCImmediate;

import java.util.*;

public final class ByteCodeModule {
    public final Map<String, Long> functionName2EntryPoint = new LinkedHashMap<>();
    public final Map<String, BCControlFlowGraph> functionName2CFG = new LinkedHashMap<>();
    public final List<BCImmediate> rodataSection = new ArrayList<>();
    public final List<BCImmediate> dataSection = new ArrayList<>();
    public long bssSectionLength = 0;
    public final Map<Integer, Long> constantIndex2RodataSectionOffset = new HashMap<>();
    public final Map<String, Long> name2DataSectionOffset = new LinkedHashMap<>();
    public final Map<String, Long> name2BssSectionOffset = new LinkedHashMap<>();
    public String entryPoint = null;

    public long getTextSectionLength() {
        long length = 0;
        for (BCControlFlowGraph cfg : functionName2CFG.values()) {
            for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                for (BCInstruction instruction : basicBlock.instructions)
                    length += instruction.getLength();
            }
        }
        return length;
    }

    public long getRodataSectionLength() {
        long length = 0;
        for (BCImmediate immediate : rodataSection)
            length += immediate.getLength();
        return length;
    }

    public long getDataSectionLength() {
        long length = 0;
        for (BCImmediate immediate : dataSection)
            length += immediate.getLength();
        return length;
    }
}
