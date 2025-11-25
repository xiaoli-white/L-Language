package com.xiaoli.llvmir_generator;

import ldk.l.lg.Generator;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.*;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.operand.IRConstant;
import ldk.l.lg.ir.operand.IRMacro;
import ldk.l.lg.ir.operand.IRPhi;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;
import ldk.l.util.option.Options;

public final class LLVMIRGenerator extends Generator {
    @Override
    public void generate(IRModule module, Options options) {
        long llvmContext = createLLVMContext();
        long llvmModule = createLLVMModule(llvmContext);
        long llvmBuilder = createLLVMBuilder(llvmContext);
        LLVMModuleGenerator generator = new LLVMModuleGenerator(module, llvmContext, llvmModule, llvmBuilder, options);
        generator.generate();
        if (options.get("verbose", Boolean.class)) {
            dumpLLVMModule(llvmModule);
        }
        compile(llvmModule, options);
        destroyLLVMBuilder(llvmBuilder);
        destroyLLVMModule(llvmModule);
        destroyLLVMContext(llvmContext);
    }

    private static native long createLLVMContext();

    private static native long createLLVMModule(long llvmContext);

    private static native long createLLVMBuilder(long llvmContext);

    private static native void destroyLLVMContext(long llvmContext);

    private static native void destroyLLVMModule(long llvmModule);

    private static native void destroyLLVMBuilder(long llvmBuilder);

    private static native void dumpLLVMModule(long llvmModule);

    private static native void compile(long llvmModule, Options options);

    private static final class LLVMModuleGenerator extends IRVisitor {
        private final IRModule module;
        private final long llvmContext;
        private final long llvmModule;
        private final long llvmBuilder;
        private final Options options;
        private IRControlFlowGraph currentCFG;
        private IRBasicBlock currentBasicBlock;
        private long stack;
        private long currentFunction;
        private long basicBlockMap;
        private long field2LocalVar;
        private long virtualRegister2Value;
        private long queue;

        public LLVMModuleGenerator(IRModule module, long llvmContext, long llvmModule, long llvmBuilder, Options options) {
            this.module = module;
            this.llvmContext = llvmContext;
            this.llvmModule = llvmModule;
            this.llvmBuilder = llvmBuilder;
            this.options = options;
        }

        public void generate() {
            module.functions.values().forEach(this::createFunction);
            initializeQueue();
            this.visitGlobalDataSection(module.globalDataSection, null);
            initializeITableInitializer();
            for (IRFunction irFunction : module.functions.values()) this.visitFunction(irFunction, null);
        }

        private native void createFunction(IRFunction irFunction);

        private native void initializeQueue();

        private native void initializeITableInitializer();

//        @Override
//        public Object visit(IRNode irNode, Object additional) {
//            System.out.println("visiting node " + irNode);
//            return super.visit(irNode, additional);
//        }

        @Override
        public native Object visitGlobalData(IRGlobalDataSection.GlobalData globalData, Object additional);

        @Override
        public native Object visitFunction(IRFunction irFunction, Object additional);

        @Override
        public native Object visitReturn(IRReturn irReturn, Object additional);

        @Override
        public native Object visitGoto(IRGoto irGoto, Object additional);

        @Override
        public native Object visitConditionalJump(IRConditionalJump irConditionalJump, Object additional);

        @Override
        public native Object visitCalculate(IRCalculate irCalculate, Object additional);

        @Override
        public native Object visitNot(IRNot irNot, Object additional);

        @Override
        public native Object visitNegate(IRNegate irNegate, Object additional);

        @Override
        public native Object visitIncrease(IRIncrease irIncrease, Object additional);

        @Override
        public native Object visitDecrease(IRDecrease irDecrease, Object additional);

        @Override
        public native Object visitStore(IRStore irStore, Object additional);

        @Override
        public native Object visitLoad(IRLoad irLoad, Object additional);

        @Override
        public native Object visitInvoke(IRInvoke irInvoke, Object additional);

        @Override
        public native Object visitStackAllocate(IRStackAllocate irStackAllocate, Object additional);

        @Override
        public native Object visitSetRegister(IRSetRegister irSetRegister, Object additional);

        @Override
        public native Object visitAssembly(IRAssembly irAssembly, Object additional);

        @Override
        public native Object visitTypeCast(IRTypeCast irTypeCast, Object additional);

        @Override
        public native Object visitNop(IRNop irNop, Object additional);

        @Override
        public native Object visitMalloc(IRMalloc irMalloc, Object additional);

        @Override
        public native Object visitFree(IRFree irFree, Object additional);

        @Override
        public native Object visitRealloc(IRRealloc irRealloc, Object additional);

        @Override
        public native Object visitCompare(IRCompare irCompare, Object additional);

        @Override
        public native Object visitPhi(IRPhi irPhi, Object additional);

        @Override
        public native Object visitConstant(IRConstant irConstant, Object additional);

        @Override
        public native Object visitVirtualRegister(IRVirtualRegister irVirtualRegister, Object additional);

        @Override
        public native Object visitMacro(IRMacro irMacro, Object additional);

        @Override
        public native Object visitIntegerConstant(IRIntegerConstant irIntegerConstant, Object additional);
    }
}
