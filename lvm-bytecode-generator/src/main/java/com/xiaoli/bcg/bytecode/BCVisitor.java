package com.xiaoli.bcg.bytecode;

import com.xiaoli.bcg.bytecode.instruction.*;
import com.xiaoli.bcg.bytecode.operand.BCOperand;
import com.xiaoli.bcg.bytecode.operand.BCRegister;
import com.xiaoli.bcg.bytecode.operand.immediate.*;

public class BCVisitor {
    public Object visitModule(ByteCodeModule module, Object additional) {
        for (BCImmediate rodata : module.rodataSection) {
            this.visitOperand(rodata, additional);
        }
        for (BCImmediate data : module.dataSection) {
            this.visitOperand(data, additional);
        }
        for (BCControlFlowGraph cfg : module.functionName2CFG.values()) {
            for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                for (BCInstruction instruction : basicBlock.instructions) {
                    this.visitInstruction(instruction, additional);
                }
            }
        }
        return null;
    }

    public Object visitInstruction(BCInstruction instruction, Object additional) {
        if (instruction.operand1 != null)
            this.visitOperand(instruction.operand1, additional);
        if (instruction.operand2 != null)
            this.visitOperand(instruction.operand2, additional);
        if (instruction.operand3 != null)
            this.visitOperand(instruction.operand3, additional);
        if (instruction.operand4 != null)
            this.visitOperand(instruction.operand4, additional);
        return null;
    }

    public Object visitOperand(BCOperand operand, Object additional) {
        return operand.visit(this, additional);
    }

    public Object visitRegister(BCRegister bcRegister, Object additional) {
        return null;
    }

    public Object visitImmediate1(BCImmediate1 bcImmediate1, Object additional) {
        return null;
    }

    public Object visitImmediate2(BCImmediate2 bcImmediate2, Object additional) {
        return null;
    }

    public Object visitImmediate4(BCImmediate4 bcImmediate4, Object additional) {
        return null;
    }

    public Object visitImmediate8(BCImmediate8 bcImmediate8, Object additional) {
        return null;
    }
}
