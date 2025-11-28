package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRCondition;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.type.IRVoidType;
import ldk.l.lg.ir.value.constant.IRFunctionReference;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IRBuilder {
    private IRBasicBlock insertPoint;

    public void setInsertPoint(IRBasicBlock insertPoint) {
        this.insertPoint = insertPoint;
    }

    public void createReturn() {
        createReturn(null);
    }

    public void createReturn(IRValue value) {
        insertPoint.instructions.add(new IRReturn(value));
    }

    public void createGoto(IRBasicBlock target) {
        insertPoint.instructions.add(new IRGoto(target));
    }

    public void createNop() {
        insertPoint.instructions.add(new IRNop());
    }

    public IRRegister createLoad(IRValue ptr, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRLoad(ptr, register));
        return register;
    }

    public IRRegister createLoad(IRValue ptr) {
        return createLoad(ptr, allocateRegisterName());
    }

    public void createStore(IRValue ptr, IRValue value) {
        insertPoint.instructions.add(new IRStore(ptr, value));
    }

    public void createJumpIfTrue(IRValue operand, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.IfTrue, operand, target));
    }

    public void createJumpIfFalse(IRValue operand, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.IfFalse, operand, target));
    }

    public void createJumpIfEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.Equal, operand1, operand2, target));
    }

    public void createJumpIfNotEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.NotEqual, operand1, operand2, target));
    }

    public void createJumpIfLess(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.Less, operand1, operand2, target));
    }

    public void createJumpIfLessEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.LessEqual, operand1, operand2, target));
    }

    public void createJumpIfGreater(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.Greater, operand1, operand2, target));
    }

    public void createJumpIfGreaterEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.GreaterEqual, operand1, operand2, target));
    }

    public IRRegister createCmpEqual(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRCompare(IRCondition.Equal, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpEqual(IRValue operand1, IRValue operand2) {
        return createCmpEqual(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createCmpNotEqual(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRCompare(IRCondition.NotEqual, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpNotEqual(IRValue operand1, IRValue operand2) {
        return createCmpNotEqual(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createCmpLess(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRCompare(IRCondition.Less, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpLess(IRValue operand1, IRValue operand2) {
        return createCmpLess(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createCmpLessEqual(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRCompare(IRCondition.LessEqual, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpLessEqual(IRValue operand1, IRValue operand2) {
        return createCmpLessEqual(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createCmpGreater(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRCompare(IRCondition.Greater, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpGreater(IRValue operand1, IRValue operand2) {
        return createCmpGreater(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createCmpGreaterEqual(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRCompare(IRCondition.GreaterEqual, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpGreaterEqual(IRValue operand1, IRValue operand2) {
        return createCmpGreaterEqual(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createAdd(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.ADD, operand1, operand2, register));
        return register;
    }

    public IRRegister createAdd(IRValue operand1, IRValue operand2) {
        return createAdd(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createSub(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.SUB, operand1, operand2, register));
        return register;
    }

    public IRRegister createSub(IRValue operand1, IRValue operand2) {
        return createSub(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createMul(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.MUL, operand1, operand2, register));
        return register;
    }

    public IRRegister createMul(IRValue operand1, IRValue operand2) {
        return createMul(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createDiv(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.DIV, operand1, operand2, register));
        return register;
    }

    public IRRegister createDiv(IRValue operand1, IRValue operand2) {
        return createDiv(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createMod(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.MOD, operand1, operand2, register));
        return register;
    }

    public IRRegister createMod(IRValue operand1, IRValue operand2) {
        return createMod(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createAnd(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.AND, operand1, operand2, register));
        return register;
    }

    public IRRegister createAnd(IRValue operand1, IRValue operand2) {
        return createAnd(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createOr(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.OR, operand1, operand2, register));
        return register;
    }

    public IRRegister createOr(IRValue operand1, IRValue operand2) {
        return createOr(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createXor(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.XOR, operand1, operand2, register));
        return register;
    }

    public IRRegister createXor(IRValue operand1, IRValue operand2) {
        return createXor(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createShl(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.SHL, operand1, operand2, register));
        return register;
    }

    public IRRegister createShl(IRValue operand1, IRValue operand2) {
        return createShl(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createShr(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.SHR, operand1, operand2, register));
        return register;
    }

    public IRRegister createShr(IRValue operand1, IRValue operand2) {
        return createShr(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createUShr(IRValue operand1, IRValue operand2, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.USHR, operand1, operand2, register));
        return register;
    }

    public IRRegister createUShr(IRValue operand1, IRValue operand2) {
        return createUShr(operand1, operand2, allocateRegisterName());
    }

    public IRRegister createStackAlloc(IRType type, IRValue size, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRStackAllocate(type, size, register));
        return register;
    }

    public IRRegister createStackAlloc(IRType type, IRValue size) {
        return createStackAlloc(type, size, allocateRegisterName());
    }

    public IRRegister createStackAlloc(IRType type, String targetName) {
        return createStackAlloc(type, null, allocateRegisterName());
    }

    public IRRegister createStackAlloc(IRType type) {
        return createStackAlloc(type, allocateRegisterName());
    }

    public IRRegister createSetRegister(IRValue value, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRSetRegister(value, register));
        return register;
    }

    public IRRegister createSetRegister(IRValue value) {
        return createSetRegister(value, allocateRegisterName());
    }

    public IRRegister createInc(IRValue operand, String targetName) {
        if (!(operand.getType() instanceof IRPointerType)) {
            throw new RuntimeException("operand must be pointer type");
        }
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.INC, operand, register));
        return register;
    }

    public IRRegister createInc(IRValue operand) {
        return createInc(operand, allocateRegisterName());
    }

    public IRRegister createDec(IRValue operand, String targetName) {
        if (!(operand.getType() instanceof IRPointerType pointerType)) {
            throw new RuntimeException("operand must be pointer type");
        }
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.DEC, operand, register));
        return register;
    }

    public IRRegister createDec(IRValue operand) {
        return createDec(operand, allocateRegisterName());
    }

    public IRRegister createNot(IRValue operand, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.NOT, operand, register));
        return register;
    }

    public IRRegister createNot(IRValue operand) {
        return createNot(operand, allocateRegisterName());
    }

    public IRRegister createNeg(IRValue operand, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.NEGATE, operand, register));
        return register;
    }

    public IRRegister createNeg(IRValue operand) {
        return createNeg(operand, allocateRegisterName());
    }

    public IRRegister createZeroExtend(IRValue operand, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.ZeroExtend, operand, targetType, register));
        return register;
    }

    public IRRegister createZeroExtend(IRValue operand, IRType targetType) {
        return createZeroExtend(operand, targetType, allocateRegisterName());
    }

    public IRRegister createSignExtend(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.SignExtend, source, targetType, register));
        return register;
    }

    public IRRegister createSignExtend(IRValue source, IRType targetType) {
        return createSignExtend(source, targetType, allocateRegisterName());
    }

    public IRRegister createTruncate(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.Truncate, source, targetType, register));
        return register;
    }

    public IRRegister createTruncate(IRValue source, IRType targetType) {
        return createTruncate(source, targetType, allocateRegisterName());
    }

    public IRRegister createIntToFloat(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.IntToFloat, source, targetType, register));
        return register;
    }

    public IRRegister createIntToFloat(IRValue source, IRType targetType) {
        return createIntToFloat(source, targetType, allocateRegisterName());
    }

    public IRRegister createFloatToInt(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.FloatToInt, source, targetType, register));
        return register;
    }

    public IRRegister createFloatToInt(IRValue source, IRType targetType) {
        return createFloatToInt(source, targetType, allocateRegisterName());
    }

    public IRRegister createIntToPointer(IRValue source, IRPointerType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.IntToPtr, source, targetType, register));
        return register;
    }

    public IRRegister createIntToPointer(IRValue source, IRPointerType targetType) {
        return createIntToPointer(source, targetType, allocateRegisterName());
    }

    public IRRegister createPointerToInt(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.PtrToInt, source, targetType, register));
        return register;
    }

    public IRRegister createPointerToInt(IRValue source, IRType targetType) {
        return createPointerToInt(source, targetType, allocateRegisterName());
    }

    public IRRegister createPointerToPointer(IRValue source, IRPointerType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.PtrToPtr, source, targetType, register));
        return register;
    }

    public IRRegister createPointerToPointer(IRValue source, IRPointerType targetType) {
        return createPointerToPointer(source, targetType, allocateRegisterName());
    }

    public IRRegister createFloatExtend(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.FloatExtend, source, targetType, register));
        return register;
    }

    public IRRegister createFloatExtend(IRValue source, IRType targetType) {
        return createFloatExtend(source, targetType, allocateRegisterName());
    }

    public IRRegister createFloatTruncate(IRValue source, IRType targetType, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.FloatTruncate, source, targetType, register));
        return register;
    }

    public IRRegister createFloatTruncate(IRValue source, IRType targetType) {
        return createFloatTruncate(source, targetType, allocateRegisterName());
    }

    public IRRegister createInvoke(IRFunction func, List<IRValue> arguments, String targetName) {
        return createInvoke(func.returnType, new IRFunctionReference(func), arguments, targetName);
    }

    public IRRegister createInvoke(IRFunction func, List<IRValue> arguments) {
        return createInvoke(func, arguments, allocateRegisterName());
    }

    public IRRegister createInvoke(IRType returnType, IRValue func, List<IRValue> arguments, String targetName) {
        IRRegister register;
        if (returnType instanceof IRVoidType) {
            register = null;
        } else {
            register = new IRRegister(targetName);
        }
        insertPoint.instructions.add(new IRInvoke(returnType, func, arguments, register));
        return register;
    }

    public IRRegister createInvoke(IRType returnType, IRValue func, List<IRValue> arguments) {
        return createInvoke(returnType, func, arguments, allocateRegisterName());
    }

    public IRRegister createGetElementPointer(IRValue ptr, List<IRIntegerConstant> indices, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRGetElementPointer(ptr, indices, register));
        return register;
    }

    public IRRegister createGetElementPointer(IRValue ptr, List<IRIntegerConstant> indices) {
        return createGetElementPointer(ptr, indices, allocateRegisterName());
    }

    public void createAsm(String asm, String constraints, List<IRValue> operands) {
        insertPoint.instructions.add(new IRAssembly(asm, constraints, operands));
    }

    public IRRegister createPhi(Map<IRBasicBlock, IRValue> values) {
        return createPhi(values, allocateRegisterName());
    }

    public IRRegister createPhi(Map<IRBasicBlock, IRValue> values, String targetName) {
        IRRegister register = new IRRegister(targetName);
        insertPoint.instructions.add(new IRPhi(values, register));
        return register;
    }

    public void createSwitch(IRValue value, IRBasicBlock defaultCase, Map<IRIntegerConstant, IRBasicBlock> cases) {
        insertPoint.instructions.add(new IRSwitch(value, defaultCase, cases));
    }

    private String allocateRegisterName() {
        return String.valueOf(Objects.requireNonNull(Objects.requireNonNull(insertPoint.cfg).function).registerCount++);
    }
}
