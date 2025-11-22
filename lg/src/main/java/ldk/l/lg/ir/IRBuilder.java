package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRCondition;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.type.IRVoidType;
import ldk.l.lg.ir.value.IRFunctionReference;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;

import java.util.List;
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

    public IRRegister createLoad(IRValue ptr) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRLoad(ptr, register));
        return register;
    }

    public void createStore(IRValue ptr, IRValue value) {
        insertPoint.instructions.add(new IRStore(ptr, value));
    }

    public void createIfTrue(IRValue value, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.IfTrue, value, target));
    }

    public void createIfFalse(IRValue value, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.IfFalse, value, target));
    }

    public void createIfEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.Equal, operand1, operand2, target));
    }

    public void createIfNotEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.NotEqual, operand1, operand2, target));
    }

    public void createIfLess(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.Less, operand1, operand2, target));
    }

    public void createIfLessEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.LessEqual, operand1, operand2, target));
    }

    public void createIfGreater(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.Greater, operand1, operand2, target));
    }

    public void createIfGreaterEqual(IRValue operand1, IRValue operand2, IRBasicBlock target) {
        insertPoint.instructions.add(new IRConditionalJump(IRCondition.GreaterEqual, operand1, operand2, target));
    }

    public IRRegister createCmpEqual(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRCompare(IRCondition.Equal, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpNotEqual(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRCompare(IRCondition.NotEqual, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpLess(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRCompare(IRCondition.Less, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpLessEqual(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRCompare(IRCondition.LessEqual, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpGreater(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRCompare(IRCondition.Greater, operand1, operand2, register));
        return register;
    }

    public IRRegister createCmpGreaterEqual(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRCompare(IRCondition.GreaterEqual, operand1, operand2, register));
        return register;
    }

    public IRRegister createAdd(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.ADD, operand1, operand2, register));
        return register;
    }

    public IRRegister createSub(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.SUB, operand1, operand2, register));
        return register;
    }

    public IRRegister createMul(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.MUL, operand1, operand2, register));
        return register;
    }

    public IRRegister createDiv(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.DIV, operand1, operand2, register));
        return register;
    }

    public IRRegister createMod(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.MOD, operand1, operand2, register));
        return register;
    }

    public IRRegister createAnd(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.AND, operand1, operand2, register));
        return register;
    }

    public IRRegister createOr(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.OR, operand1, operand2, register));
        return register;
    }

    public IRRegister createXor(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.XOR, operand1, operand2, register));
        return register;
    }

    public IRRegister createShl(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.SHL, operand1, operand2, register));
        return register;
    }

    public IRRegister createShr(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.SHR, operand1, operand2, register));
        return register;
    }

    public IRRegister createUShr(IRValue operand1, IRValue operand2) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRBinaryOperates(IRBinaryOperates.Operator.USHR, operand1, operand2, register));
        return register;
    }

    public IRRegister createStackAlloc(IRValue size) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRStackAllocate(size, register));
        return register;
    }

    public IRRegister createSetRegister(IRValue value) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRSetRegister(value, register));
        return register;
    }

    public IRRegister createInc(IRValue operand) {
        if (!(operand.getType() instanceof IRPointerType)) {
            throw new RuntimeException("operand must be pointer type");
        }
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.INC, operand, register));
        return register;
    }

    public IRRegister createDec(IRValue operand) {
        if (!(operand.getType() instanceof IRPointerType pointerType)) {
            throw new RuntimeException("operand must be pointer type");
        }
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.DEC, operand, register));
        return register;
    }

    public IRRegister createNot(IRValue operand) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.NOT, operand, register));
        return register;
    }

    public IRRegister createNeg(IRValue operand) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRUnaryOperates(IRUnaryOperates.Operator.NEGATE, operand, register));
        return register;
    }

    public IRRegister createZeroExtend(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.ZeroExtend, operand, targetType, register));
        return register;
    }

    public IRRegister createSignExtend(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.SignExtend, operand, targetType, register));
        return register;
    }

    public IRRegister createTruncate(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.Truncate, operand, targetType, register));
        return register;
    }

    public IRRegister createIntToFloat(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.IntToFloat, operand, targetType, register));
        return register;
    }

    public IRRegister createFloatToInt(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.FloatToInt, operand, targetType, register));
        return register;
    }

    public IRRegister createFloatExtend(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.FloatExtend, operand, targetType, register));
        return register;
    }

    public IRRegister createFloatTruncate(IRValue operand, IRType targetType) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRTypeCast(IRTypeCast.Kind.FloatTruncate, operand, targetType, register));
        return register;
    }

    public IRRegister createInvoke(IRFunction func, List<IRValue> arguments) {
        return createInvoke(func.returnType, new IRFunctionReference(func), arguments);
    }
    public IRRegister createInvoke(IRType returnType, IRValue func, List<IRValue> arguments) {
        IRRegister register;
        if (returnType instanceof IRVoidType) {
            register = null;
        } else {
            register = new IRRegister(allocateName());
        }
        insertPoint.instructions.add(new IRInvoke(returnType, func, arguments, register));
        return register;
    }
    public IRRegister createGetElementPointer(IRValue ptr, List<IRIntegerConstant> indices) {
        IRRegister register = new IRRegister(allocateName());
        insertPoint.instructions.add(new IRGetElementPointer(ptr, indices, register));
        return register;
    }
    private String allocateName() {
        return String.valueOf(Objects.requireNonNull(Objects.requireNonNull(insertPoint.cfg).function).registerCount++);
    }
}
