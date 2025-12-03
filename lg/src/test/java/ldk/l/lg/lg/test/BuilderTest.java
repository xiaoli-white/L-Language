package ldk.l.lg.lg.test;

import ldk.l.lg.ir.IRBuilder;
import ldk.l.lg.ir.IRDumper;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRGlobalVariable;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.IRArrayType;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;

import java.util.List;

public class BuilderTest {
    public static void main(String[] args) {
        IRBuilder builder = new IRBuilder();
        IRModule module = new IRModule();
        IRGlobalVariable globalVariable = new IRGlobalVariable(false, "glo", new IRIntegerConstant(IRType.getIntType(), 0));
        module.putGlobalVariable(globalVariable);

        IRStructure structure = new IRStructure(List.of(), "struct", List.of(new IRField(IRType.getIntType(), "field"), new IRField(IRType.getLongType(), "field2")));
        module.putStructure(structure);

        IRFunction function = new IRFunction(IRType.getIntType(), "main", List.of(), List.of(), new IRControlFlowGraph());
        IRFunction function2 = new IRFunction(IRType.getIntType(), "main2", List.of(new IRLocalVariable(IRType.getIntType(), "arg1"), new IRLocalVariable(IRType.getIntType(), "arg2")), List.of(new IRLocalVariable(IRType.getIntType(), "var1"), new IRLocalVariable(IRType.getFloatType(), "var2")), new IRControlFlowGraph());
        module.putFunction(function);
        module.putFunction(function2);
        IRBasicBlock block = new IRBasicBlock("entry");
        IRBasicBlock block2 = new IRBasicBlock("exit");
        function.addBasicBlock(block);
        function.addBasicBlock(block2);

        builder.setInsertPoint(block);
        builder.createNop();
        builder.createJumpIfTrue(new IRIntegerConstant(IRType.getBooleanType(), 1), block2);
        builder.createJumpIfFalse(new IRIntegerConstant(IRType.getBooleanType(), 0), block2);
        builder.createJumpIfEqual(new IRIntegerConstant(IRType.getIntType(), 1), new IRIntegerConstant(IRType.getIntType(), 1), block2);
        builder.createGoto(block2);
        builder.setInsertPoint(block2);
        var ptr = builder.createStackAlloc(IRType.getIntType());
        var val = builder.createLoad(ptr);
        var val2 = builder.createCmpEqual(val, new IRIntegerConstant(IRType.getIntType(), 0));
        builder.createStore(new IRIntegerConstant(IRType.getIntType(), 0), val);
        builder.createStore(new IRIntegerConstant(IRType.getIntType(), 0), val2);
        var val3 = builder.createAdd(new IRIntegerConstant(IRType.getIntType(), 1), new IRIntegerConstant(IRType.getIntType(), 2));
        builder.createMul(val3, new IRIntegerConstant(IRType.getIntType(), 3));
        builder.createStore(ptr, new IRIntegerConstant(IRType.getIntType(), 0));
        var val4 = builder.createSetRegister(val3);
        var var5 = builder.createSetRegister(ptr);
        var var6 = builder.createNeg(val4);
        var var7 = builder.createNot(var6);
        var var8 = builder.createInc(var5);
        var val9 = builder.createDec(var5);
        var val10 = builder.createZeroExtend(val9, IRType.getByteType());
        var val11 = builder.createInvoke(function2, List.of());
        var val12 = builder.createZeroExtend(new IRIntegerConstant(IRType.getIntType(), 0), new IRPointerType(new IRArrayType(IRType.getIntType(), 10)));
        var val13 = builder.createGetElementPointer(val12, List.of(new IRIntegerConstant(IRType.getIntType(), 0), new IRIntegerConstant(IRType.getIntType(), 1)));
        builder.createStore(val13, new IRIntegerConstant(IRType.getIntType(), 0));
        builder.createReturn(new IRIntegerConstant(IRType.getIntType(), 0));
        IRBasicBlock b3 = new IRBasicBlock("entry2");
        function2.addBasicBlock(b3);
        builder.setInsertPoint(b3);
        builder.createAsm("mov eax, 0x1234", "", List.of());
        builder.createReturn(new IRIntegerConstant(IRType.getIntType(), 1));

        IRDumper dumper = new IRDumper();
        dumper.visitModule(module, "");
    }
}
