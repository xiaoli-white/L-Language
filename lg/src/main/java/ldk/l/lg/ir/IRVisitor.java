package ldk.l.lg.ir;

import ldk.l.lg.ir.base.*;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.instruction.IRPhi;
import ldk.l.lg.ir.operand.*;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.*;
import ldk.l.lg.ir.value.constant.IRArrayConstant;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;
import ldk.l.lg.ir.value.IRValue;
import ldk.l.lg.ir.value.constant.IRStructureInitializer;

public abstract class IRVisitor {
    public Object visit(IRNode irNode, Object additional) {
        return irNode.accept(this, additional);
    }

    public Object visitModule(IRModule irModule, Object additional) {
//        this.visitConstantPool(irModule.constantPool, additional);
//        this.visitGlobalDataSection(irModule.globalDataSection, additional);
        for (IRGlobalVariable globalVariable : irModule.globals.values()) this.visit(globalVariable, additional);
        for (IRStructure irStructure : irModule.structures.values()) this.visitStructure(irStructure, additional);
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

    public Object visitGlobalVariable(IRGlobalVariable globalVariable, Object additional) {
        this.visit(globalVariable.type, additional);
        this.visit(globalVariable.initializer, additional);
        return null;
    }

    public Object visitFunction(IRFunction irFunction, Object additional) {
        this.visit(irFunction.returnType, additional);
        for (IRLocalVariable arg : irFunction.args) {
            this.visit(arg, additional);
        }
        if (irFunction.controlFlowGraph != null) {
            for (IRLocalVariable local : irFunction.locals) {
                this.visit(local, additional);
            }
            for (IRBasicBlock block : irFunction.controlFlowGraph.basicBlocks.values()) {
                for (IRInstruction instruction : block.instructions) this.visit(instruction, additional);
            }
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

    public Object visitGoto(IRGoto irGoto, Object additional) {
        return null;
    }

    public Object visitConditionalJump(IRConditionalJump irConditionalJump, Object additional) {
        this.visit(irConditionalJump.operand1, additional);
        if (irConditionalJump.operand2 != null) {
            this.visit(irConditionalJump.operand2, additional);
        }
        return null;
    }

    public Object visitBinaryOperates(IRBinaryOperates irBinaryOperates, Object additional) {
        this.visit(irBinaryOperates.operand1, additional);
        this.visit(irBinaryOperates.operand2, additional);
        this.visit(irBinaryOperates.target, additional);
        return null;
    }

    public Object visitUnaryOperates(IRUnaryOperates irUnaryOperates, Object additional) {
        this.visit(irUnaryOperates.operand, additional);
        this.visit(irUnaryOperates.target, additional);
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
        if (irNot.target != null) {
            this.visitVirtualRegister(irNot.target, additional);
        }
        return null;
    }

    public Object visitNegate(IRNegate irNegate, Object additional) {
        this.visit(irNegate.type, additional);
        this.visit(irNegate.operand, additional);
        if (irNegate.target != null) {
            this.visitVirtualRegister(irNegate.target, additional);
        }
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

    public Object visitLoad(IRLoad irLoad, Object additional) {
        this.visit(irLoad.ptr, additional);
        this.visit(irLoad.target, additional);
        return null;
    }

    public Object visitStore(IRStore irStore, Object additional) {
        this.visit(irStore.ptr, additional);
        this.visit(irStore.value, additional);
        return null;
    }

    public Object visitSetRegister(IRSetRegister irSetRegister, Object additional) {
        this.visit(irSetRegister.value, additional);
        this.visit(irSetRegister.target, additional);
        return null;
    }

    public Object visitInvoke(IRInvoke irInvoke, Object additional) {
        this.visit(irInvoke.returnType, additional);
        this.visit(irInvoke.func, additional);
        for (IRValue argument : irInvoke.arguments) {
            this.visit(argument, additional);
        }
        if (irInvoke.target != null) {
            this.visit(irInvoke.target, additional);
        }
        return null;
    }

    public Object visitNop(IRNop irNop, Object additional) {
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
        this.visit(irStackAllocate.type, additional);
        if (irStackAllocate.size != null) {
            this.visit(irStackAllocate.size, additional);
        }
        this.visit(irStackAllocate.target, additional);
        return null;
    }

    public Object visitTypeCast(IRTypeCast irTypeCast, Object additional) {
        this.visit(irTypeCast.source, additional);
        this.visit(irTypeCast.targetType, additional);
        this.visit(irTypeCast.target, additional);
        return null;
    }

    public Object visitAssembly(IRAssembly irAssembly, Object additional) {
        for (IRValue value : irAssembly.operands) {
            this.visit(value, additional);
        }
        return null;
    }

    public Object visitCompare(IRCompare irCompare, Object additional) {
        this.visit(irCompare.type, additional);
        this.visit(irCompare.operand1, additional);
        this.visit(irCompare.operand2, additional);
        this.visit(irCompare.target, additional);
        return null;
    }

    public Object visitConstant(IRConstant irConstant, Object additional) {
        return null;
    }

    public Object visitVirtualRegister(IRVirtualRegister irVirtualRegister, Object additional) {
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

    public Object visitIntegerConstant(IRIntegerConstant irIntegerConstant, Object additional) {
        return null;
    }

    public Object visitArrayType(IRArrayType irArrayType, Object additional) {
        this.visit(irArrayType.base, additional);
        return null;
    }

    public Object visitStructureType(IRStructureType irStructureType, Object additional) {
        return null;
    }

    public Object visitArrayConstant(IRArrayConstant irArrayConstant, Object additional) {
        this.visit(irArrayConstant.type, additional);
        for (ldk.l.lg.ir.value.constant.IRConstant value : irArrayConstant.values) {
            this.visit(value, additional);
        }
        return null;
    }

    public Object visitGetElementPointer(IRGetElementPointer irGetElementPointer, Object additional) {
        this.visit(irGetElementPointer.ptr, additional);
        for (IRIntegerConstant index : irGetElementPointer.indices) {
            this.visit(index, additional);
        }
        return null;
    }

    public Object visitPhi(IRPhi irPhi, Object additional) {
        for (IRValue value : irPhi.values.values()) {
            this.visit(value, additional);
        }
        this.visit(irPhi.target, additional);
        return null;
    }

    public Object visitSwitch(IRSwitch irSwitch, Object additional) {
        this.visit(irSwitch.value, additional);
        for (IRIntegerConstant constant : irSwitch.cases.keySet()) {
            this.visit(constant, additional);
        }
        return null;
    }

    public Object visitStructureInitializer(IRStructureInitializer irStructureInitializer, Object additional) {
        this.visit(irStructureInitializer.type, additional);
        for (IRValue value : irStructureInitializer.elements) {
            this.visit(value, additional);
        }
        return null;
    }
}
