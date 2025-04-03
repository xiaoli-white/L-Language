package com.xiaoli.llvmir_generator;

import ldk.l.lg.Generator;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.IRVisitor;
import ldk.l.util.option.Options;

public class LLVMIRGenerator extends Generator {
    @Override
    public void generate(IRModule module, Options options) {
        long llvmContext = createLLVMContext();
        long llvmModule = createLLVMModule(llvmContext);
        LLVMModuleGenerator generator = new LLVMModuleGenerator(module, llvmModule, options);
        generator.generate();
        destroyLLVMModule(llvmModule);
        destroyLLVMContext(llvmContext);
    }

    static {
        System.loadLibrary("llvm_ir_generator");
    }

    private static native long createLLVMContext();

    private static native void destroyLLVMContext(long llvmContext);

    private static native long createLLVMModule(long llvmContext);

    private static native void destroyLLVMModule(long llvmModule);

    private static class LLVMModuleGenerator extends IRVisitor {
        private final IRModule module;
        private final long llvmModule;
        private final Options options;

        public LLVMModuleGenerator(IRModule module, long llvmModule, Options options) {
            this.module = module;
            this.llvmModule = llvmModule;
            this.options = options;
        }

        public void generate() {
            this.visitModule(this.module, null);
        }
    }
}
