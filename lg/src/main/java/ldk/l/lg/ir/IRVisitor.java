package ldk.l.lg.ir;

import ldk.l.lg.ir.attribute.IRAttributeGroupDeclaration;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRFunction;
import ldk.l.lg.ir.base.IRGlobalDataSection;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.operand.*;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.*;

public abstract class IRVisitor {
    public Object visit(IRNode irNode, Object additional) {
        return irNode.accept(this, additional);
    }

    public Object visitModule(IRModule irModule, Object additional) {
        for (IRStructure irStructure : irModule.structures.values()) this.visitStructure(irStructure, additional);
        this.visitConstantPool(irModule.constantPool, additional);
        this.visitGlobalDataSection(irModule.globalDataSection, additional);
        for (IRControlFlowGraph.BasicBlock basicBlock : irModule.globalInitSection.basicBlocks.values()) {
            for (IRInstruction instruction : basicBlock.instructions) {
                this.visit(instruction, additional);
            }
        }
        for (IRFunction irFunction : irModule.functions.values()) this.visitFunction(irFunction, additional);
        return null;
    }

    public Object visitConstantPool(IRConstantPool constantPool, Object additional) {
        for (IRConstantPool.Entry entry : constantPool.entries) this.visitConstantPoolEntry(entry, additional);
        return null;
    }

    public Object visitConstantPoolEntry(IRConstantPool.Entry entry, Object additional) {
        this.visit(entry.type, additional);
        return entry.value;
    }

    public Object visitFunction(IRFunction irFunction, Object additional) {
        this.visit(irFunction.returnType, additional);
        for (IRField field : irFunction.fields) this.visitField(field, additional);
        for (IRControlFlowGraph.BasicBlock block : irFunction.controlFlowGraph.basicBlocks.values()) {
            for (IRInstruction instruction : block.instructions) this.visit(instruction, additional);
        }
        return null;
    }

    public Object visitStructure(IRStructure irStructure, Object additional) {
        for (IRField field : irStructure.fields) this.visitField(field, additional);
        return null;
    }

    public Object visitField(IRField irField, Object additional) {
        this.visit(irField.type, additional);
        return null;
    }

    public Object visitGlobalDataSection(IRGlobalDataSection irGlobalDataSection, Object additional) {
        for (IRGlobalDataSection.GlobalData data : irGlobalDataSection.data) {
            this.visitGlobalData(data, additional);
        }
        return null;
    }

    public Object visitGlobalData(IRGlobalDataSection.GlobalData globalData, Object additional) {
        if (globalData.size != null) {
            this.visit(globalData.size, additional);
        }
        if (globalData.values != null) {
            for (IROperand operand : globalData.values) {
                this.visit(operand, additional);
            }
        }
        return null;
    }

    public Object visitIntegerType(IRIntegerType irIntegerType, Object additional) {
        return null;
    }

    public Object visitFloatType(IRFloatType irFloatType, Object additional) {
        return null;
    }

    public Object visitDoubleType(IRDoubleType irDoubleType, Object additional) {
        return null;
    }

    public Object visitPointerType(IRPointerType irPointerType, Object additional) {
        return this.visit(irPointerType.base, additional);
    }

    public Object visitVoidType(IRVoidType irVoidType, Object additional) {
        return null;
    }

    public Object visitAttributeGroupDeclaration(IRAttributeGroupDeclaration irAttributeGroupDeclaration, Object additional) {
        return null;
    }

    public Object visitGoto(IRGoto irGoto, Object additional) {
        return null;
    }

    public Object visitConditionalJump(IRConditionalJump irConditionalJump, Object additional) {
        this.visit(irConditionalJump.type, additional);
        this.visit(irConditionalJump.operand1, additional);
        if (irConditionalJump.operand2 != null) {
            this.visit(irConditionalJump.operand2, additional);
        }
        return null;
    }

    public Object visitReturn(IRReturn irReturn, Object additional) {
        if (irReturn.value == null) return null;
        return this.visit(irReturn.value, additional);
    }

    public Object visitCalculate(IRCalculate irCalculate, Object additional) {
        this.visit(irCalculate.type, additional);
        this.visit(irCalculate.operand1, additional);
        this.visit(irCalculate.operand2, additional);
        this.visitVirtualRegister(irCalculate.target, additional);
        return null;
    }

    public Object visitNot(IRNot irNot, Object additional) {
        this.visit(irNot.type, additional);
        this.visit(irNot.operand, additional);
        this.visitVirtualRegister(irNot.target, additional);
        return null;
    }

    public Object visitNegate(IRNegate irNegate, Object additional) {
        this.visit(irNegate.type, additional);
        this.visit(irNegate.operand, additional);
        this.visitVirtualRegister(irNegate.target, additional);
        return null;
    }

    public Object visitMalloc(IRMalloc irMalloc, Object additional) {
        this.visit(irMalloc.size, additional);
        this.visitVirtualRegister(irMalloc.target, additional);
        return null;
    }

    public Object visitFree(IRFree irFree, Object additional) {
        this.visit(irFree.ptr, additional);
        return null;
    }

    public Object visitRealloc(IRRealloc irRealloc, Object additional) {
        this.visit(irRealloc.ptr, additional);
        this.visit(irRealloc.size, additional);
        this.visitVirtualRegister(irRealloc.target, additional);
        return null;
    }

    public Object visitGet(IRGet irGet, Object additional) {
        this.visit(irGet.type, additional);
        this.visit(irGet.address, additional);
        this.visitVirtualRegister(irGet.target, additional);
        return null;
    }

    public Object visitSet(IRSet irSet, Object additional) {
        this.visit(irSet.type, additional);
        this.visit(irSet.value, additional);
        this.visit(irSet.address, additional);
        return null;
    }

    public Object visitSetVirtualRegister(IRSetVirtualRegister irSetVirtualRegister, Object additional) {
        this.visit(irSetVirtualRegister.source, additional);
        this.visitVirtualRegister(irSetVirtualRegister.target, additional);
        return null;
    }

    public Object visitInvoke(IRInvoke irInvoke, Object additional) {
        this.visit(irInvoke.address, additional);
        for (int i = 0; i < irInvoke.arguments.length; i++) {
            this.visit(irInvoke.argumentTypes[i], additional);
            this.visit(irInvoke.arguments[i], additional);
        }
        this.visit(irInvoke.returnType, additional);
        if (irInvoke.target != null) {
            this.visitVirtualRegister(irInvoke.target, additional);
        }
        return null;
    }

    public Object visitNoOperate(IRNoOperate irNoOperate, Object additional) {
        return null;
    }

    public Object visitIncrease(IRIncrease irIncrease, Object additional) {
        this.visit(irIncrease.type, additional);
        this.visit(irIncrease.operand, additional);
        if (irIncrease.target != null) {
            this.visitVirtualRegister(irIncrease.target, additional);
        }
        return null;
    }

    public Object visitDecrease(IRDecrease irDecrease, Object additional) {
        this.visit(irDecrease.type, additional);
        this.visit(irDecrease.operand, additional);
        if (irDecrease.target != null) {
            this.visitVirtualRegister(irDecrease.target, additional);
        }
        return null;
    }

    public Object visitStackAllocate(IRStackAllocate irStackAllocate, Object additional) {
        this.visit(irStackAllocate.size, additional);
        this.visitVirtualRegister(irStackAllocate.target, additional);
        return null;
    }

    public Object visitTypeCast(IRTypeCast irTypeCast, Object additional) {
        this.visit(irTypeCast.originalType, additional);
        this.visit(irTypeCast.source, additional);
        this.visit(irTypeCast.targetType, additional);
        this.visitVirtualRegister(irTypeCast.target, additional);
        return null;
    }

    public Object visitAsm(IRAsm irAsm, Object additional) {
        for (int i = 0; i < irAsm.resources.length; i++) {
            this.visit(irAsm.types[i], additional);
            this.visit(irAsm.resources[i], additional);
        }
        return null;
    }

    public Object visitCompare(IRCompare irCompare, Object additional) {
        this.visit(irCompare.type, additional);
        this.visit(irCompare.operand1, additional);
        this.visit(irCompare.operand2, additional);
        this.visitVirtualRegister(irCompare.target, additional);
        return null;
    }

    public Object visitConstant(IRConstant irConstant, Object additional) {
        return null;
    }

    public Object visitVirtualRegister(IRVirtualRegister irVirtualRegister, Object additional) {
        return null;
    }

    public Object visitPhi(IRPhi irPhi, Object additional) {
        this.visit(irPhi.type, additional);
        for (IROperand operand : irPhi.operands) {
            this.visit(operand, additional);
        }
        return null;
    }

    public Object visitMacro(IRMacro irMacro, Object additional) {
        if (irMacro.typeIsInitial() && irMacro.type() != null) {
            this.visit(irMacro.type(), additional);
        }
        return null;
    }

    public Object visitVirtualTable(IRVirtualTable irVirtualTable, Object additional) {
        return null;
    }

    public Object visitInterfaceTable(IRInterfaceTable irInterfaceTable, Object additional) {
        return null;
    }
}
