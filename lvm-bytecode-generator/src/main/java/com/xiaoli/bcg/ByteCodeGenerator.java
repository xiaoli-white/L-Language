package com.xiaoli.bcg;

import com.xiaoli.bcg.bytecode.BCControlFlowGraph;
import com.xiaoli.bcg.bytecode.BCVisitor;
import com.xiaoli.bcg.bytecode.instruction.*;
import com.xiaoli.bcg.bytecode.ByteCodeModule;
import com.xiaoli.bcg.bytecode.operand.BCOperand;
import com.xiaoli.bcg.bytecode.operand.BCRegister;
import com.xiaoli.bcg.bytecode.operand.immediate.*;
import ldk.l.lg.Generator;
import ldk.l.lg.ir.IRConstantPool;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRFunction;
import ldk.l.lg.ir.base.IRGlobalDataSection;
import ldk.l.lg.ir.operand.*;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.*;
import ldk.l.lvm.module.Module;
import ldk.l.lvm.vm.ByteCode;
import ldk.l.util.option.Options;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class ByteCodeGenerator extends Generator {
    @Override
    public void generate(IRModule module, Options options) {
        boolean verbose = options.get("verbose", Boolean.class);

        ByteCodeModule byteCodeModule = new ByteCodeModule();
        ByteCodeModuleGenerator generator = new ByteCodeModuleGenerator(module, byteCodeModule, options);
        generator.generate();

        if (verbose) {
            printTextSection(byteCodeModule);
        }

        Map<String, Map<Long, BCRegister.Interval>> intervals = new LinkedHashMap<>();
        Tagger tagger = new Tagger(byteCodeModule, intervals);
        tagger.tag();

        if (verbose) {
            byteCodeModule.functionName2CFG.forEach((name, cfg) -> {
                System.out.println("function " + name);
                cfg.basicBlocks.values().forEach(basicBlock -> basicBlock.instructions.forEach(instruction -> {
                    System.out.print("\t" + instruction + ": ");
                    System.out.println(cfg.instruction2LiveRegisters.get(instruction));
                }));
            });
            intervals.forEach((name, intervalMap) -> {
                System.out.println("function " + name);
                intervalMap.forEach((virtualRegister, interval) -> System.out.println("\t" + virtualRegister + ": " + interval));
            });
        }

        RegisterAllocator registerAllocator = new RegisterAllocator(byteCodeModule, intervals);
        registerAllocator.allocate();

        if (verbose) {
            byteCodeModule.functionName2CFG.forEach((name, cfg) -> {
                System.out.println("function " + name);
                for (Map.Entry<Long, Byte> e : cfg.colorMap.entrySet()) {
                    System.out.println("\tVirtual register " + e.getKey() + " -> Register " + e.getValue());
                }
                cfg.spilledVirtualRegisters.forEach(virtualRegister -> System.out.println("\tSpilled Virtual register: " + virtualRegister));
            });
            printTextSection(byteCodeModule);
        }

        Locator locator = new Locator(byteCodeModule);
        locator.locate();

        Redirector redirector = new Redirector(byteCodeModule);
        redirector.redirect();

        if (verbose) {
            printTextSection(byteCodeModule);
        }


        Module vmModule = toModule(byteCodeModule);
        byte[] raw = vmModule.raw();
        String output = options.get("output", String.class);
        if (output.isEmpty()) output = "a.lvme";
        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Module toModule(ByteCodeModule byteCodeModule) {
        List<Byte> arrayList = new ArrayList<>();
        for (BCControlFlowGraph cfg : byteCodeModule.functionName2CFG.values()) {
            for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                for (BCInstruction instruction : basicBlock.instructions) {
                    byte[] bytes = instruction.toByteCode();
                    for (byte b : bytes) arrayList.add(b);
                }
            }
        }
        byte[] text = new byte[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) text[i] = arrayList.get(i);
        List<Byte> rodataList = new ArrayList<>();
        for (BCImmediate immediate : byteCodeModule.rodataSection) {
            byte[] bytes = immediate.toByteCode();
            for (byte b : bytes) rodataList.add(b);
        }
        byte[] rodata = new byte[rodataList.size()];
        for (int i = 0; i < rodataList.size(); i++) rodata[i] = rodataList.get(i);
        List<Byte> dataList = new ArrayList<>();
        for (BCImmediate immediate : byteCodeModule.dataSection) {
            byte[] bytes = immediate.toByteCode();
            for (byte b : bytes) dataList.add(b);
        }
        byte[] data = new byte[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) data[i] = dataList.get(i);
        return new Module(text, rodata, data, byteCodeModule.bssSectionLength, byteCodeModule.functionName2EntryPoint.get(byteCodeModule.entryPoint));
    }

    private static void printTextSection(ByteCodeModule module) {
        System.out.println("Entry point: " + module.entryPoint);
        System.out.println("Rodata section:");
        long count = 0;
        for (BCImmediate immediate : module.rodataSection) {
            System.out.printf("\t%d->%s\n", count, immediate);
            count += immediate.getLength();
        }
        System.out.println("Data section:");
        count = 0;
        for (BCImmediate immediate : module.dataSection) {
            System.out.printf("\t%d->%s\n", count, immediate);
            count += immediate.getLength();
        }
        System.out.println("BSS section length: " + module.bssSectionLength);
        count = 0;
        for (Map.Entry<String, BCControlFlowGraph> entry : module.functionName2CFG.entrySet()) {
            System.out.printf("function %s:\n", entry.getKey());
            long i = 0;
            for (BCControlFlowGraph.BasicBlock basicBlock : entry.getValue().basicBlocks.values()) {
                System.out.printf("\t#%s:\n", basicBlock.name);
                for (BCInstruction instruction : basicBlock.instructions) {
                    System.out.printf("\t\t%d(%d)->%s\n", i, count, instruction);
                    count += instruction.getLength();
                    i++;
                }
            }
        }
    }

    private static final class ByteCodeModuleGenerator extends IRVisitor {
        private final IRModule irModule;
        private final ByteCodeModule module;
        private final Options options;
        private long rodataSectionLength = 0;
        private long dataSectionLength = 0;
        private long virtualRegisterCount = 0;
        private final Map<String, String> irBasicBlock2BCBasicBlock = new HashMap<>();
        private final Map<String, Long> virtualRegisterMap = new HashMap<>();
        private final Stack<BCRegister> registerStack = new Stack<>();
        private final Map<String, Long> argumentOffsets = new HashMap<>();
        private final Map<String, Long> localVarOffsets = new HashMap<>();
        private final List<Long> allocatedRegisters = new ArrayList<>();
        private IRControlFlowGraph currentIRCFG = null;
        private BCControlFlowGraph currentCFG = null;
        private BCControlFlowGraph.BasicBlock currentBasicBlock = null;

        public ByteCodeModuleGenerator(IRModule irModule, ByteCodeModule module, Options options) {
            this.irModule = irModule;
            this.module = module;
            this.options = options;
        }

        public BCControlFlowGraph.BasicBlock createBasicBlock() {
            return createBasicBlock("basicBlock_" + (currentCFG.basicBlocks.size()));
        }

        public BCControlFlowGraph.BasicBlock createBasicBlock(String name) {
            BCControlFlowGraph.BasicBlock basicBlock = new BCControlFlowGraph.BasicBlock(name);
            this.currentCFG.addBasicBlock(basicBlock);
            this.currentBasicBlock = basicBlock;
            return basicBlock;
        }

        public BCRegister allocateVirtualRegister() {
            long virtualRegisterCount = this.virtualRegisterCount++;
            this.allocatedRegisters.add(virtualRegisterCount);
            return new BCRegister(virtualRegisterCount);
        }

        private void addInstruction(BCInstruction instruction) {
            instruction.allocatedRegisters.addAll(this.allocatedRegisters);
            currentBasicBlock.instructions.add(instruction);
            this.allocatedRegisters.clear();
        }

        public BCInstruction setRegister(BCOperand source) {
            BCRegister register = allocateVirtualRegister();
            registerStack.push(new BCRegister(register.virtualRegister));
            return new BCInstruction(switch (source) {
                case BCRegister _ -> ByteCode.MOV;
                case BCImmediate1 _ -> ByteCode.MOV_IMMEDIATE1;
                case BCImmediate2 _ -> ByteCode.MOV_IMMEDIATE2;
                case BCImmediate4 _ -> ByteCode.MOV_IMMEDIATE4;
                case BCImmediate8 _ -> ByteCode.MOV_IMMEDIATE8;
                case null, default -> throw new RuntimeException("Unknown operand type");
            }, source, register);
        }

        public void generate() {
            this.visitModule(this.irModule, null);
            this.module.functionName2CFG.values().forEach(BCControlFlowGraph::buildEdges);
        }

        @Override
        public Object visitModule(IRModule irModule, Object additional) {
            for (IRStructure irStructure : irModule.structures.values()) this.visitStructure(irStructure, additional);
            this.visitConstantPool(irModule.constantPool, additional);
            this.visitGlobalDataSection(irModule.globalDataSection, additional);


            Map<String, String> irBasicBlock2BCBasicBlock = new HashMap<>();
            BCControlFlowGraph initCFG = new BCControlFlowGraph();
            module.functionName2CFG.put("<init>", initCFG);
            this.currentCFG = initCFG;
            createBasicBlock();
            createPrologue(0);
            this.currentIRCFG = irModule.globalInitSection;
            for (IRControlFlowGraph.BasicBlock block : irModule.globalInitSection.basicBlocks.values()) {
                BCControlFlowGraph.BasicBlock basicBlock = createBasicBlock();
                irBasicBlock2BCBasicBlock.put(block.name, basicBlock.name);
                for (IRInstruction instruction : block.instructions) {
                    this.visit(instruction, additional);
                }
            }
            BCControlFlowGraph.BasicBlock end = createBasicBlock();
            createEpilogue(0);
            addInstruction(new BCInstruction(ByteCode.RETURN));
            for (BCControlFlowGraph.BasicBlock block : initCFG.basicBlocks.values()) {
                for (BCInstruction instruction : block.instructions) {
                    if (instruction.code == ByteCode.MOV_IMMEDIATE8 || instruction.code == ByteCode.JUMP_IMMEDIATE) {
                        BCImmediate8 imm = (BCImmediate8) instruction.operand1;
                        if (imm.comment != null) {
                            if (imm.comment.startsWith("<ir_basic_block>")) {
                                imm.comment = "<basic_block>" + irBasicBlock2BCBasicBlock.get(imm.comment.substring("<ir_basic_block>".length()));
                            } else if (imm.comment.equals("<end>")) {
                                imm.comment = "<basic_block>" + end.name;
                            }
                        }
                    }
                }
            }


            for (IRFunction irFunction : irModule.functions.values()) this.visitFunction(irFunction, additional);


            BCControlFlowGraph mainCFG = new BCControlFlowGraph();
            this.currentCFG = mainCFG;
            createBasicBlock();
            createPrologue(0);
            createBasicBlock();
            addInstruction(new BCInstruction(ByteCode.INVOKE_IMMEDIATE, new BCImmediate8(0, "<function_address><init>")));
            addInstruction(new BCInstruction(ByteCode.INVOKE_IMMEDIATE, new BCImmediate8(0, "<function_address>" + irModule.entryPoint)));
            createBasicBlock();
            createEpilogue(0);
            addInstruction(new BCInstruction(ByteCode.EXIT_IMMEDIATE, new BCImmediate8(0)));
            module.functionName2CFG.put("<main>", mainCFG);
            module.entryPoint = "<main>";
            return null;
        }

        @Override
        public Object visitConstantPool(IRConstantPool constantPool, Object additional) {
            for (int i = 0; i < constantPool.entries.size(); i++) {
                IRConstantPool.Entry entry = constantPool.entries.get(i);
                if (entry.type instanceof IRPointerType) {
                    if (entry.value instanceof String string) {
                        module.constantIndex2RodataSectionOffset.put(i, rodataSectionLength);
                        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
                        byte[] cString = new byte[bytes.length + 1];
                        System.arraycopy(bytes, 0, cString, 0, bytes.length);
                        cString[cString.length - 1] = 0;
                        for (byte b : cString) module.rodataSection.add(new BCImmediate1(b));
                        rodataSectionLength += cString.length;
                    } else if (!(entry.value instanceof Long || entry.value instanceof Integer || entry.value instanceof Short || entry.value instanceof Byte || entry.value == null)) {
                        throw new RuntimeException("Unknown constant pool entry value type: " + entry.value.getClass());
                    }
                }
            }
            return null;
        }

        @Override
        public Object visitGlobalDataSection(IRGlobalDataSection irGlobalDataSection, Object additional) {
            super.visitGlobalDataSection(irGlobalDataSection, additional);
            for (BCImmediate data : module.dataSection) {
                if (data instanceof BCImmediate8 bcImmediate8) {
                    if (bcImmediate8.comment != null && bcImmediate8.comment.startsWith("<global_data_address>")) {
                        String name = bcImmediate8.comment.substring("<global_data_address>".length());
                        if (module.name2DataSectionOffset.containsKey(name)) {
                            bcImmediate8.value = module.name2DataSectionOffset.get(name);
                            bcImmediate8.comment = "<add_data_section_entry_point>";
                        } else {
                            bcImmediate8.value = module.name2BssSectionOffset.get(name);
                            bcImmediate8.comment = "<add_bss_section_entry_point>";
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Object visitGlobalData(IRGlobalDataSection.GlobalData globalData, Object additional) {
            if (globalData.size != null) {
                module.name2BssSectionOffset.put(globalData.name, module.bssSectionLength);
                long size = switch (globalData.size) {
                    case IRMacro irMacro -> {
                        if ("structure_length".equals(irMacro.name)) {
                            irMacro.setType(IRType.getUnsignedLongType());
                            yield this.irModule.structures.get(irMacro.args[0]).getLength();
                        } else {
                            throw new RuntimeException("Not expected macro: " + irMacro.name);
                        }
                    }
                    case IRConstant irConstant -> {
                        IRConstantPool.Entry entry = this.irModule.constantPool.get(irConstant.index);
                        if (entry == null) throw new RuntimeException("Unknown constant pool entry");
                        yield switch (entry.value) {
                            case Byte byteValue -> byteValue;
                            case Short shortValue -> shortValue;
                            case Integer integerValue -> integerValue;
                            case Long longValue -> longValue;
                            default -> throw new RuntimeException("Unknown integer size");
                        };
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + globalData.size);
                };
                module.bssSectionLength += size;
            } else {
                module.name2DataSectionOffset.put(globalData.name, dataSectionLength);
                for (IROperand value : globalData.values) {
                    switch (value) {
                        case IRConstant irConstant -> {
                            IRConstantPool.Entry entry = this.irModule.constantPool.get(irConstant.index);
                            switch (entry.value) {
                                case Byte byteValue -> {
                                    module.dataSection.add(new BCImmediate1(byteValue));
                                    dataSectionLength++;
                                }
                                case Short shortValue -> {
                                    module.dataSection.add(new BCImmediate2(shortValue));
                                    dataSectionLength += 2;
                                }
                                case Integer integerValue -> {
                                    module.dataSection.add(new BCImmediate4(integerValue));
                                    dataSectionLength += 4;
                                }
                                case Long longValue -> {
                                    module.dataSection.add(new BCImmediate8(longValue));
                                    dataSectionLength += 8;
                                }
                                default -> throw new RuntimeException("Unknown integer size");
                            }
                        }
                        case IRMacro irMacro -> {
                            switch (irMacro.name) {
                                case "structure_length" -> {
                                    irMacro.setType(IRType.getUnsignedLongType());
                                    module.dataSection.add(new BCImmediate8(this.irModule.structures.get(irMacro.args[0]).getLength()));
                                    dataSectionLength += 8;
                                }
                                default -> throw new RuntimeException("Not expected macro: " + irMacro.name);
                            }
                        }
                        case IRVirtualTable irVirtualTable -> {
                            for (String function : irVirtualTable.functions) {
                                if (function.isEmpty())
                                    module.dataSection.add(new BCImmediate8(0));
                                else
                                    module.dataSection.add(new BCImmediate8(0, "<function_address>" + function));
                            }
                            dataSectionLength += 8L * irVirtualTable.functions.length;
                        }
                        case IRInterfaceTable irInterfaceTable -> {
                            dataSectionLength += 8L * irInterfaceTable.entries.length;
                            List<BCImmediate8> list = new ArrayList<>();
                            for (IRInterfaceTable.Entry entry : irInterfaceTable.entries) {
                                module.dataSection.add(new BCImmediate8(dataSectionLength, "<add_data_section_entry_point>"));
                                list.add(new BCImmediate8(0, "<global_data_address><class_instance " + entry.name() + ">"));
                                for (String function : entry.functions()) {
                                    if (function.isEmpty())
                                        list.add(new BCImmediate8(0));
                                    else
                                        list.add(new BCImmediate8(0, "<function_address>" + function));
                                }
                                dataSectionLength += 8L * entry.functions().length + 8;
                            }
                            module.dataSection.addAll(list);
                        }
                        default -> throw new RuntimeException("Unknown global data value");
                    }
                }
            }
            return null;
        }

        @Override
        public Object visitFunction(IRFunction irFunction, Object additional) {
            this.virtualRegisterCount = 0;

            long currentFunctionArgumentsSize = 0;
            for (int i = (int) (irFunction.argumentCount - 1); i >= 0; i--) {
                this.argumentOffsets.put(irFunction.fields[i].name, currentFunctionArgumentsSize);
                currentFunctionArgumentsSize += IRType.getLength(irFunction.fields[i].type);
            }
            long currentFunctionLocalVarSize = 0;
            for (int i = (int) irFunction.argumentCount; i < irFunction.fields.length; i++) {
                currentFunctionLocalVarSize += IRType.getLength(irFunction.fields[i].type);
                this.localVarOffsets.put(irFunction.fields[i].name, currentFunctionLocalVarSize);
            }

            BCControlFlowGraph cfg = new BCControlFlowGraph();
            this.currentCFG = cfg;
            createBasicBlock();
            createPrologue(currentFunctionLocalVarSize);
            this.currentIRCFG = irFunction.controlFlowGraph;
            for (IRControlFlowGraph.BasicBlock block : irFunction.controlFlowGraph.basicBlocks.values()) {
                BCControlFlowGraph.BasicBlock basicBlock = createBasicBlock();
                irBasicBlock2BCBasicBlock.put(block.name, basicBlock.name);
                for (IRInstruction instruction : block.instructions) this.visit(instruction, additional);
            }
            BCControlFlowGraph.BasicBlock end = createBasicBlock();
            createEpilogue(currentFunctionLocalVarSize);
            addInstruction(new BCInstruction(ByteCode.RETURN));

            for (BCControlFlowGraph.BasicBlock block : cfg.basicBlocks.values()) {
                for (BCInstruction instruction : block.instructions) {
                    if (instruction.code == ByteCode.MOV_IMMEDIATE8 || instruction.code == ByteCode.JUMP_IMMEDIATE) {
                        BCImmediate8 imm = (BCImmediate8) instruction.operand1;
                        if (imm.comment != null) {
                            if (imm.comment.startsWith("<ir_basic_block>")) {
                                imm.comment = "<basic_block>" + irBasicBlock2BCBasicBlock.get(imm.comment.substring("<ir_basic_block>".length()));
                            } else if (imm.comment.equals("<end>")) {
                                imm.comment = "<basic_block>" + end.name;
                            }
                        }
                    }
                }
            }
            module.functionName2CFG.put(irFunction.name, cfg);

            this.irBasicBlock2BCBasicBlock.clear();
            this.virtualRegisterMap.clear();
            this.argumentOffsets.clear();
            this.localVarOffsets.clear();
            return null;
        }

        @Override
        public Object visitNoOperate(IRNoOperate irNoOperate, Object additional) {
            addInstruction(new BCInstruction(ByteCode.NOP));
            return null;
        }

        @Override
        public Object visitCalculate(IRCalculate irCalculate, Object additional) {
            this.visit(irCalculate.operand1, additional);
            BCRegister operand1 = registerStack.pop();
            this.visit(irCalculate.operand2, additional);
            BCRegister operand2 = registerStack.pop();
            this.visitVirtualRegister(irCalculate.target, additional);
            BCRegister result = registerStack.pop();

            if (irCalculate.type.equals(IRType.getFloatType())) {
                addInstruction(new BCInstruction(switch (irCalculate.operator) {
                    case ADD -> irCalculate.isAtomic ? ByteCode.ATOMIC_ADD_FLOAT : ByteCode.ADD_FLOAT;
                    case SUB -> irCalculate.isAtomic ? ByteCode.ATOMIC_SUB_FLOAT : ByteCode.SUB_FLOAT;
                    case MUL -> irCalculate.isAtomic ? ByteCode.ATOMIC_MUL_FLOAT : ByteCode.MUL_FLOAT;
                    case DIV -> irCalculate.isAtomic ? ByteCode.ATOMIC_DIV_FLOAT : ByteCode.DIV_FLOAT;
                    case MOD -> irCalculate.isAtomic ? ByteCode.ATOMIC_MOD_FLOAT : ByteCode.MOD_FLOAT;
                    default -> throw new RuntimeException("Unsupported operator: " + irCalculate.operator);
                }, operand1, operand2, result));
            } else if (irCalculate.type.equals(IRType.getDoubleType())) {
                addInstruction(new BCInstruction(switch (irCalculate.operator) {
                    case ADD -> irCalculate.isAtomic ? ByteCode.ATOMIC_ADD_DOUBLE : ByteCode.ADD_DOUBLE;
                    case SUB -> irCalculate.isAtomic ? ByteCode.ATOMIC_SUB_DOUBLE : ByteCode.SUB_DOUBLE;
                    case MUL -> irCalculate.isAtomic ? ByteCode.ATOMIC_MUL_DOUBLE : ByteCode.MUL_DOUBLE;
                    case DIV -> irCalculate.isAtomic ? ByteCode.ATOMIC_DIV_DOUBLE : ByteCode.DIV_DOUBLE;
                    case MOD -> irCalculate.isAtomic ? ByteCode.ATOMIC_MOD_DOUBLE : ByteCode.MOD_DOUBLE;
                    default -> throw new RuntimeException("Unsupported operator: " + irCalculate.operator);
                }, operand1, operand2, result));
            } else {
                addInstruction(new BCInstruction(switch (irCalculate.operator) {
                    case ADD -> irCalculate.isAtomic ? ByteCode.ATOMIC_ADD : ByteCode.ADD;
                    case SUB -> irCalculate.isAtomic ? ByteCode.ATOMIC_SUB : ByteCode.SUB;
                    case MUL -> irCalculate.isAtomic ? ByteCode.ATOMIC_MUL : ByteCode.MUL;
                    case DIV -> irCalculate.isAtomic ? ByteCode.ATOMIC_DIV : ByteCode.DIV;
                    case MOD -> irCalculate.isAtomic ? ByteCode.ATOMIC_MOD : ByteCode.MOD;
                    case AND -> irCalculate.isAtomic ? ByteCode.ATOMIC_AND : ByteCode.AND;
                    case OR -> irCalculate.isAtomic ? ByteCode.ATOMIC_OR : ByteCode.OR;
                    case XOR -> irCalculate.isAtomic ? ByteCode.ATOMIC_XOR : ByteCode.XOR;
                    case SHL -> irCalculate.isAtomic ? ByteCode.ATOMIC_SHL : ByteCode.SHL;
                    case SHR -> irCalculate.isAtomic ? ByteCode.ATOMIC_SHR : ByteCode.SHR;
                    case USHR -> irCalculate.isAtomic ? ByteCode.ATOMIC_USHR : ByteCode.USHR;
                }, operand1, operand2, result));
            }

            registerStack.push(new BCRegister(result.register));
            return null;
        }

        @Override
        public Object visitNot(IRNot irNot, Object additional) {
            this.visit(irNot.operand, additional);
            BCRegister operand = registerStack.pop();
            this.visitVirtualRegister(irNot.target, additional);
            BCRegister result = registerStack.pop();

            if (irNot.type instanceof IRIntegerType irIntegerType) {
                addInstruction(new BCInstruction(irNot.isAtomic ? ByteCode.ATOMIC_NOT : ByteCode.NOT, operand, result));
                registerStack.push(new BCRegister(result.register));
            }
            return null;
        }

        @Override
        public Object visitNegate(IRNegate irNegate, Object additional) {
            this.visit(irNegate.operand, additional);
            BCRegister operand = registerStack.pop();
            this.visitVirtualRegister(irNegate.target, additional);
            BCRegister result = registerStack.pop();

            if (irNegate.type instanceof IRIntegerType irIntegerType) {
                addInstruction(new BCInstruction(irNegate.isAtomic ? ByteCode.ATOMIC_NEG : ByteCode.NEG, operand, result));
                registerStack.push(new BCRegister(result.register));
            }
            return null;
        }

        @Override
        public Object visitGoto(IRGoto irGoto, Object additional) {
            addInstruction(new BCInstruction(ByteCode.JUMP_IMMEDIATE, new BCImmediate8(0, "<ir_basic_block>" + irGoto.target)));
            return null;
        }

        @Override
        public Object visitConditionalJump(IRConditionalJump irConditionalJump, Object additional) {
            this.visit(irConditionalJump.operand1, additional);
            BCRegister operand1 = registerStack.pop();
            this.visit(irConditionalJump.operand2, additional);
            BCRegister operand2 = registerStack.pop();

            if (irConditionalJump.type instanceof IRIntegerType irIntegerType) {
                addInstruction(new BCInstruction(ByteCode.CMP, new BCImmediate1(switch (irIntegerType.size) {
                    case OneBit, OneByte -> ByteCode.BYTE_TYPE;
                    case TwoBytes -> ByteCode.SHORT_TYPE;
                    case FourBytes -> ByteCode.INT_TYPE;
                    case EightBytes -> ByteCode.LONG_TYPE;
                }), operand1, operand2));
            } else if (irConditionalJump.type instanceof IRPointerType) {
                addInstruction(new BCInstruction(ByteCode.CMP, new BCImmediate1(ByteCode.LONG_TYPE), operand1, operand2));
            } else {
                throw new RuntimeException("Unsupported type: " + irConditionalJump.type);
            }

            BCRegister address = allocateVirtualRegister();
            addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(0, "<ir_basic_block>" + irConditionalJump.target), address));
            addInstruction(new BCInstruction(switch (irConditionalJump.condition) {
                case Equal -> ByteCode.JE;
                case NotEqual -> ByteCode.JNE;
                case Less -> ByteCode.JL;
                case LessEqual -> ByteCode.JLE;
                case Greater -> ByteCode.JG;
                case GreaterEqual -> ByteCode.JGE;
            }, address));
            return null;
        }

        @Override
        public Object visitGet(IRGet irGet, Object additional) {
            this.visitVirtualRegister(irGet.target, additional);
            BCRegister result = registerStack.pop();
            if (irGet.address instanceof IRMacro irMacro && "field_address".equals(irMacro.name)) {
                BCImmediate1 typeLength = new BCImmediate1((byte) IRType.getLength(irGet.type));
                if (irMacro.args.length == 1) {
                    if (this.localVarOffsets.containsKey(irMacro.args[0]))
                        addInstruction(new BCInstruction(ByteCode.LOAD_LOCAL, typeLength, new BCImmediate8(this.localVarOffsets.get(irMacro.args[0])), result));
                    else
                        addInstruction(new BCInstruction(ByteCode.LOAD_PARAMETER, typeLength, new BCImmediate8(this.argumentOffsets.get(irMacro.args[0]) + 16), result));
                } else {
                    IRStructure structure = this.irModule.structures.get(irMacro.args[0]);
                    long length = 0;
                    long offset = -1;
                    for (IRField field : structure.fields) {
                        if (field.name.equals(irMacro.args[1])) {
                            offset = length;
                        }
                        length += IRType.getLength(field.type);
                    }
                    if (offset == -1) throw new RuntimeException("Unknown field: " + irMacro.args[1]);
                    this.visit(irMacro.additionalOperands[0], additional);
                    BCRegister object = registerStack.pop();
                    addInstruction(new BCInstruction(ByteCode.LOAD_FIELD, typeLength, object, new BCImmediate8(offset), result));
                }
            } else {
                this.visit(irGet.address, additional);
                BCRegister address = registerStack.pop();
                addInstruction(new BCInstruction(switch ((int) IRType.getLength(irGet.type)) {
                    case 1 -> ByteCode.LOAD_1;
                    case 2 -> ByteCode.LOAD_2;
                    case 4 -> ByteCode.LOAD_4;
                    case 8 -> ByteCode.LOAD_8;
                    default -> throw new RuntimeException("Unknown type size");
                }, address, result));
            }
            return null;
        }

        @Override
        public Object visitSet(IRSet irSet, Object additional) {
            this.visit(irSet.value, additional);
            BCRegister value = registerStack.pop();
            if (irSet.address instanceof IRMacro irMacro && "field_address".equals(irMacro.name)) {
                BCImmediate1 typeLength = new BCImmediate1((byte) IRType.getLength(irSet.type));
                if (irMacro.args.length == 1) {
                    if (this.localVarOffsets.containsKey(irMacro.args[0]))
                        addInstruction(new BCInstruction(ByteCode.STORE_LOCAL, typeLength, new BCImmediate8(this.localVarOffsets.get(irMacro.args[0])), value));
                    else
                        addInstruction(new BCInstruction(ByteCode.STORE_PARAMETER, typeLength, new BCImmediate8(this.argumentOffsets.get(irMacro.args[0]) + 16), value));
                } else {
                    IRStructure structure = this.irModule.structures.get(irMacro.args[0]);
                    long length = 0;
                    long offset = -1;
                    for (IRField field : structure.fields) {
                        if (field.name.equals(irMacro.args[1])) {
                            offset = length;
                        }
                        length += IRType.getLength(field.type);
                    }
                    if (offset == -1) throw new RuntimeException("Unknown field");
                    this.visit(irMacro.additionalOperands[0], additional);
                    BCRegister object = registerStack.pop();
                    addInstruction(new BCInstruction(ByteCode.STORE_FIELD, typeLength, object, new BCImmediate8(offset), value));
                }
            } else {
                this.visit(irSet.address, additional);
                BCRegister address = registerStack.pop();
                addInstruction(new BCInstruction(switch ((int) IRType.getLength(irSet.type)) {
                    case 1 -> ByteCode.STORE_1;
                    case 2 -> ByteCode.STORE_2;
                    case 4 -> ByteCode.STORE_4;
                    case 8 -> ByteCode.STORE_8;
                    default -> throw new RuntimeException("Unknown type size");
                }, address, value));
            }
            return null;
        }

        @Override
        public Object visitSetVirtualRegister(IRSetVirtualRegister irSetVirtualRegister, Object additional) {
            this.visit(irSetVirtualRegister.source, additional);
            BCRegister source = registerStack.pop();
            this.visit(irSetVirtualRegister.target, additional);
            BCRegister target = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.MOV, source, target));
            return null;
        }

        @Override
        public Object visitMalloc(IRMalloc irMalloc, Object additional) {
            this.visit(irMalloc.size, additional);
            BCRegister size = registerStack.pop();
            this.visitVirtualRegister(irMalloc.target, additional);
            BCRegister result = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.MALLOC, size, result));
            return null;
        }

        @Override
        public Object visitFree(IRFree irFree, Object additional) {
            this.visit(irFree.ptr, additional);
            BCRegister ptr = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.FREE, ptr));
            return null;
        }

        @Override
        public Object visitRealloc(IRRealloc irRealloc, Object additional) {
            this.visit(irRealloc.ptr, additional);
            BCRegister ptr = registerStack.pop();
            this.visit(irRealloc.size, additional);
            BCRegister size = registerStack.pop();
            this.visitVirtualRegister(irRealloc.target, additional);
            BCRegister result = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.REALLOC, ptr, size, result));
            return null;
        }

        @Override
        public Object visitReturn(IRReturn irReturn, Object additional) {
            if (irReturn.value != null) {
                this.visit(irReturn.value, additional);
                BCRegister value = registerStack.pop();
                addInstruction(new BCInstruction(ByteCode.MOV, value, new BCRegister(ByteCode.RETURN_VALUE_REGISTER)));
            }
            addInstruction(new BCInstruction(ByteCode.JUMP_IMMEDIATE, new BCImmediate8(0, "<end>")));
            return null;
        }

        @Override
        public Object visitInvoke(IRInvoke irInvoke, Object additional) {
            long argumentsSize = 0;
            for (int i = 0; i < irInvoke.arguments.length; i++) {
                this.visit(irInvoke.arguments[i], additional);
                BCRegister argumentRegister = registerStack.pop();
                long argumentSize = IRType.getLength(irInvoke.argumentTypes[i]);
                argumentsSize += argumentSize;
                addInstruction(new BCInstruction(switch ((int) argumentSize) {
                    case 1 -> ByteCode.PUSH_1;
                    case 2 -> ByteCode.PUSH_2;
                    case 4 -> ByteCode.PUSH_4;
                    case 8 -> ByteCode.PUSH_8;
                    default -> throw new RuntimeException("Unknown type size");
                }, argumentRegister));
            }
            this.visit(irInvoke.address, additional);
            BCRegister address = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.INVOKE, address));
            if (irInvoke.target != null) {
                this.visitVirtualRegister(irInvoke.target, additional);
                BCRegister result = registerStack.pop();
                addInstruction(new BCInstruction(ByteCode.MOV, result, new BCRegister(ByteCode.RETURN_VALUE_REGISTER)));
            }
            BCRegister register = allocateVirtualRegister();
            addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(argumentsSize), register));
            addInstruction(new BCInstruction(ByteCode.ADD, new BCRegister(ByteCode.SP_REGISTER), new BCRegister(register.virtualRegister), new BCRegister(ByteCode.SP_REGISTER)));
            return null;
        }

        @Override
        public Object visitIncrease(IRIncrease irIncrease, Object additional) {
            this.visit(irIncrease.operand, additional);
            BCRegister operand = registerStack.pop();
            if (irIncrease.target != null) {
                this.visitVirtualRegister(irIncrease.target, additional);
                BCRegister target = registerStack.pop();

                addInstruction(new BCInstruction(ByteCode.MOV, operand, target));
                addInstruction(new BCInstruction(ByteCode.INC, new BCRegister(target)));
            } else {
                addInstruction(new BCInstruction(ByteCode.ATOMIC_INC, operand));
            }
            return null;
        }

        @Override
        public Object visitDecrease(IRDecrease irDecrease, Object additional) {
            this.visit(irDecrease.operand, additional);
            BCRegister operand = registerStack.pop();
            if (irDecrease.target != null) {
                this.visitVirtualRegister(irDecrease.target, additional);
                BCRegister target = registerStack.pop();

                addInstruction(new BCInstruction(ByteCode.MOV, operand, target));
                addInstruction(new BCInstruction(ByteCode.DEC, new BCRegister(target)));
            } else {
                addInstruction(new BCInstruction(ByteCode.ATOMIC_DEC, operand));
            }
            return null;
        }

        @Override
        public Object visitTypeCast(IRTypeCast irTypeCast, Object additional) {
            this.visit(irTypeCast.source, additional);
            BCRegister source = registerStack.pop();
            this.visit(irTypeCast.target, additional);
            BCRegister target = registerStack.pop();
            switch (irTypeCast.kind) {
                case ZeroExtend, Truncate -> addInstruction(new BCInstruction(ByteCode.MOV, source, target));
                case SignExtend -> {
                    byte code1 = getBCType(irTypeCast.originalType);
                    byte code2 = getBCType(irTypeCast.targetType);
                    addInstruction(new BCInstruction(ByteCode.TYPE_CAST, new BCImmediate1((byte) (((code1 << 4) | code2) & 0xff)), source, target));
                }
                case IntToFloat -> {
                    byte code1 = getBCType(irTypeCast.originalType);
                    var tempRegister = allocateVirtualRegister();
                    addInstruction(new BCInstruction(ByteCode.TYPE_CAST, new BCImmediate1((byte) (((code1 << 4) | ByteCode.LONG_TYPE) & 0xff)), source, tempRegister));
                    var tempRegister2 = allocateVirtualRegister();
                    addInstruction(new BCInstruction(ByteCode.LONG_TO_DOUBLE, new BCRegister(tempRegister.virtualRegister), tempRegister2));
                    byte code2;
                    if (irTypeCast.targetType.equals(IRType.getFloatType())) code2 = ByteCode.DOUBLE_TO_FLOAT;
                    else if (irTypeCast.targetType.equals(IRType.getDoubleType())) code2 = ByteCode.MOV;
                    else throw new RuntimeException("Unknown type");
                    addInstruction(new BCInstruction(code2, new BCRegister(tempRegister2.virtualRegister), target));
                }
                case FloatToInt -> {
                    byte code1;
                    if (irTypeCast.targetType.equals(IRType.getFloatType())) code1 = ByteCode.DOUBLE_TO_FLOAT;
                    else if (irTypeCast.targetType.equals(IRType.getDoubleType())) code1 = ByteCode.MOV;
                    else throw new RuntimeException("Unknown type");
                    var tempRegister = allocateVirtualRegister();
                    addInstruction(new BCInstruction(code1, source, tempRegister));
                    var tempRegister2 = allocateVirtualRegister();
                    addInstruction(new BCInstruction(ByteCode.DOUBLE_TO_LONG, new BCRegister(tempRegister.virtualRegister), tempRegister2));
                    byte code2 = getBCType(irTypeCast.targetType);
                    addInstruction(new BCInstruction(ByteCode.TYPE_CAST, new BCImmediate1((byte) (((ByteCode.LONG_TYPE << 4) | code2) & 0xff)), new BCRegister(tempRegister2.virtualRegister), target));
                }
                case FloatExtend, FloatTruncate -> {
                    byte code;
                    if (irTypeCast.originalType.equals(IRType.getFloatType())) {
                        if (irTypeCast.targetType.equals(IRType.getDoubleType())) code = ByteCode.FLOAT_TO_DOUBLE;
                        else if (irTypeCast.targetType.equals(IRType.getFloatType())) code = ByteCode.MOV;
                        else throw new RuntimeException("Unknown type");
                    } else if (irTypeCast.originalType.equals(IRType.getDoubleType())) {
                        if (irTypeCast.targetType.equals(IRType.getFloatType())) code = ByteCode.DOUBLE_TO_FLOAT;
                        else if (irTypeCast.targetType.equals(IRType.getDoubleType())) code = ByteCode.MOV;
                        else throw new RuntimeException("Unknown type");
                    } else {
                        throw new RuntimeException("Unknown type");
                    }
                    addInstruction(new BCInstruction(code, source, target));
                }
            }
            return null;
        }

        @Override
        public Object visitAsm(IRAsm irAsm, Object additional) {
            Map<String, BCRegister> name2Register = new HashMap<>();
            for (int i = 0; i < irAsm.resources.length; i++) {
                this.visit(irAsm.resources[i], additional);
                BCRegister register = registerStack.pop();
                name2Register.put(irAsm.names[i], register);
            }
            Map<String, BCRegister> localVirtualRegister2Register = new HashMap<>();
            String[] lines = irAsm.code.split("\n");
            for (String line : lines) {
                parseCode(line.strip(), name2Register, localVirtualRegister2Register);
            }
            return null;
        }

        @Override
        public Object visitStackAllocate(IRStackAllocate irStackAllocate, Object additional) {
            this.visit(irStackAllocate.size, additional);
            BCRegister size = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.SUB, new BCRegister(ByteCode.SP_REGISTER), size, new BCRegister(ByteCode.SP_REGISTER)));

            this.visit(irStackAllocate.target, additional);
            BCRegister target = registerStack.pop();
            addInstruction(new BCInstruction(ByteCode.MOV, new BCRegister(ByteCode.SP_REGISTER), target));
            return null;
        }

        @Override
        public Object visitConstant(IRConstant irConstant, Object additional) {
            IRConstantPool.Entry entry = this.irModule.constantPool.get(irConstant.index);

            if (entry == null) {
                System.err.println("Invalid constant index: " + irConstant.index);
                registerStack.push(allocateVirtualRegister());
                return null;
            }

            if (entry.type instanceof IRIntegerType irIntegerType) {
                if (irIntegerType.size == IRIntegerType.Size.OneBit) {
                    BCInstruction instruction = setRegister(new BCImmediate8(((boolean) entry.value) ? 1L : 0L));
                    addInstruction(instruction);
                } else if (entry.value instanceof Character charValue) {
                    byte[] bytes = charValue.toString().getBytes(StandardCharsets.UTF_8);
                    int value = 0;
                    for (byte b : bytes) {
                        value <<= 8;
                        value |= b & 0xFF;
                    }
                    BCInstruction instruction = setRegister(new BCImmediate4(value));
                    addInstruction(instruction);
                } else {
                    long value;
                    if (entry.value instanceof Number number) {
                        value = number.longValue();
                    } else {
                        throw new IllegalArgumentException("Unknown type");
                    }
                    BCImmediate imm = switch (irIntegerType.size) {
                        case OneByte -> new BCImmediate1((byte) value);
                        case TwoBytes -> new BCImmediate2((short) value);
                        case FourBytes -> new BCImmediate4((int) value);
                        case EightBytes -> new BCImmediate8(value);
                        default -> throw new IllegalArgumentException("Unknown size");
                    };
                    BCInstruction instruction = setRegister(imm);
                    addInstruction(instruction);
                }
            } else if (entry.type instanceof IRFloatType) {
                if (entry.value instanceof Number number) {
                    addInstruction(setRegister(new BCImmediate4(Float.floatToIntBits(number.floatValue()))));
                } else {
                    throw new RuntimeException("Unknown type");
                }
            } else if (entry.type instanceof IRDoubleType) {
                if (entry.value instanceof Number number) {
                    addInstruction(setRegister(new BCImmediate8(Double.doubleToLongBits(number.doubleValue()))));
                } else {
                    throw new RuntimeException("Unknown type");
                }
            } else if (entry.type instanceof IRPointerType irPointerType) {
                BCRegister register = allocateVirtualRegister();
                if (entry.value instanceof String) {
                    addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(module.constantIndex2RodataSectionOffset.get(irConstant.index), "<add_rodata_section_entry_point>"), register));
                } else {
                    long value;
                    if (entry.value == null) {
                        value = 0;
                    } else if (entry.value instanceof Number number) {
                        value = number.longValue();
                    } else {
                        throw new IllegalArgumentException("Unknown type");
                    }
                    addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(value), register));
                }
                registerStack.push(new BCRegister(register.virtualRegister));
            }
            return null;
        }

        @Override
        public Object visitVirtualRegister(IRVirtualRegister irVirtualRegister, Object additional) {
            BCRegister register;
            if (virtualRegisterMap.containsKey(irVirtualRegister.name)) {
                register = new BCRegister(virtualRegisterMap.get(irVirtualRegister.name));
            } else {
                register = allocateVirtualRegister();
                virtualRegisterMap.put(irVirtualRegister.name, register.virtualRegister);
            }
            registerStack.push(register);
            return null;
        }

        @Override
        public Object visitMacro(IRMacro irMacro, Object additional) {
            if ("function_address".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                BCRegister register = allocateVirtualRegister();
                addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(0, "<function_address>" + irMacro.args[0]), register));
                registerStack.push(new BCRegister(register.virtualRegister));
            } else if ("field_address".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                BCRegister result = allocateVirtualRegister();
                if (irMacro.args.length == 1) {
                    long offset;
                    if (this.localVarOffsets.containsKey(irMacro.args[0])) {
                        offset = this.localVarOffsets.get(irMacro.args[0]);
                        addInstruction(new BCInstruction(ByteCode.GET_LOCAL_ADDRESS, new BCImmediate8(offset), result));
                    } else {
                        offset = this.argumentOffsets.get(irMacro.args[0]);
                        addInstruction(new BCInstruction(ByteCode.GET_PARAMETER_ADDRESS, new BCImmediate8(offset + 16), result));
                    }
                } else {
                    IRStructure structure = this.irModule.structures.get(irMacro.args[0]);
                    long length = 0;
                    long offset = -1;
                    for (IRField field : structure.fields) {
                        if (field.name.equals(irMacro.args[1])) {
                            offset = length;
                        }
                        length += IRType.getLength(field.type);
                    }
                    if (offset == -1) throw new RuntimeException("Unknown field");
                    this.visit(irMacro.additionalOperands[0], additional);
                    BCRegister object = registerStack.pop();
                    addInstruction(new BCInstruction(ByteCode.GET_FIELD_ADDRESS, object, new BCImmediate8(offset), result));
                }
                registerStack.push(new BCRegister(result.virtualRegister));
            } else if ("structure_length".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                BCRegister register = allocateVirtualRegister();
                addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.irModule.structures.get(irMacro.args[0]).getLength()), register));
                registerStack.push(new BCRegister(register.virtualRegister));
            } else if ("structure_field_offset".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                IRStructure structure = this.irModule.structures.get(irMacro.args[0]);
                long length = 0;
                long offset = -1;
                for (IRField field : structure.fields) {
                    if (field.name.equals(irMacro.args[1])) {
                        offset = length;
                    }
                    length += IRType.getLength(field.type);
                }
                BCRegister register = allocateVirtualRegister();
                addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(offset), register));
                registerStack.push(new BCRegister(register.virtualRegister));
            } else if ("global_data_address".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                BCRegister temp = allocateVirtualRegister();
                BCRegister temp2 = allocateVirtualRegister();
                if (module.name2DataSectionOffset.containsKey(irMacro.args[0])) {
                    addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(0, "<data_section_entry_point>"), temp));
                    addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.module.name2DataSectionOffset.get(irMacro.args[0])), temp2));
                } else {
                    addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(0, "<bss_section_entry_point>"), temp));
                    addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.module.name2BssSectionOffset.get(irMacro.args[0])), temp2));
                }
                BCRegister result = allocateVirtualRegister();
                addInstruction(new BCInstruction(ByteCode.ADD, new BCRegister(temp.virtualRegister), new BCRegister(temp2.virtualRegister), result));
                registerStack.push(new BCRegister(result.virtualRegister));
            } else if ("vtable_entry_offset".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                BCRegister result = allocateVirtualRegister();
                addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.irModule.name2VTableKeys.get(irMacro.args[0]).indexOf(irMacro.args[1]) * 8L), result));
                registerStack.push(new BCRegister(result.virtualRegister));
            } else if ("itable_entry_offset".equals(irMacro.name)) {
                irMacro.setType(IRType.getUnsignedLongType());
                BCRegister result = allocateVirtualRegister();
                addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.irModule.name2ITableKeys.get(irMacro.args[0]).indexOf(irMacro.args[1]) * 8L + 8), result));
                registerStack.push(new BCRegister(result.virtualRegister));
            }
            return null;
        }

        @Override
        public Object visitPhi(IRPhi irPhi, Object additional) {
            BCRegister temp = new BCRegister(this.virtualRegisterCount++);
            for (int i = 0; i < irPhi.labels.length; i++) {
                BCControlFlowGraph.BasicBlock bb = this.currentCFG.basicBlocks.get(this.irBasicBlock2BCBasicBlock.get(this.currentIRCFG.basicBlocks.get(irPhi.labels[i]).name));
                this.visit(irPhi.operands[i], null);
                BCRegister operand = registerStack.pop();
                BCInstruction instruction = new BCInstruction(ByteCode.MOV, new BCRegister(operand.virtualRegister), new BCRegister(temp.virtualRegister));
                instruction.allocatedRegisters.add(temp.virtualRegister);
                if (ByteCode.isJump(bb.instructions.getLast().code))
                    bb.instructions.add(bb.instructions.size() - 1, instruction);
                else
                    bb.instructions.add(instruction);
            }
            registerStack.push(temp);
            return null;
        }

        private void createPrologue(long currentFunctionLocalVarSize) {
            addInstruction(new BCInstruction(ByteCode.CREATE_FRAME, new BCImmediate8(currentFunctionLocalVarSize)));
        }

        private void createEpilogue(long currentFunctionLocalVarSize) {
            BCRegister temp = allocateVirtualRegister();
            addInstruction(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(currentFunctionLocalVarSize), temp));
            addInstruction(new BCInstruction(ByteCode.SUB, new BCRegister(ByteCode.BP_REGISTER), new BCRegister(temp.virtualRegister), new BCRegister(ByteCode.SP_REGISTER)));
            addInstruction(new BCInstruction(ByteCode.DESTROY_FRAME, new BCImmediate8(currentFunctionLocalVarSize)));
        }

        private void parseCode(String text, Map<String, BCRegister> name2Register, Map<String, BCRegister> localVirtualRegister2Register) {
            if (text.isEmpty()) return;

            StringBuilder codeText = new StringBuilder();
            int i = 0;
            for (; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.isWhitespace(c) || c == ',') {
                    break;
                } else if (c == ':') {
                    createBasicBlock(codeText.toString());
                    return;
                } else {
                    codeText.append(c);
                }
            }
            byte code = ByteCode.parseInstruction(codeText.toString());
            BCOperand operand1 = null;
            BCOperand operand2 = null;
            BCOperand operand3 = null;
            BCOperand operand4 = null;
            while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
            if (i < text.length()) {
                StringBuilder operandCode = new StringBuilder();
                for (; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (Character.isWhitespace(c) || c == ',')
                        break;
                    else
                        operandCode.append(c);
                }
                operand1 = parseOperand(operandCode.toString(), name2Register, localVirtualRegister2Register);
            }
            while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
            if (i < text.length() && text.charAt(i) == ',') {
                do {
                    i++;
                } while (i < text.length() && Character.isWhitespace(text.charAt(i)));
                StringBuilder operandCode = new StringBuilder();
                for (; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (Character.isWhitespace(c) || c == ',')
                        break;
                    else
                        operandCode.append(c);
                }
                operand2 = parseOperand(operandCode.toString(), name2Register, localVirtualRegister2Register);
            }
            while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
            if (i < text.length() && text.charAt(i) == ',') {
                do {
                    i++;
                } while (i < text.length() && Character.isWhitespace(text.charAt(i)));
                StringBuilder operandCode = new StringBuilder();
                for (; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (Character.isWhitespace(c) || c == ',')
                        break;
                    else
                        operandCode.append(c);
                }
                operand3 = parseOperand(operandCode.toString(), name2Register, localVirtualRegister2Register);
            }
            while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
            if (i < text.length() && text.charAt(i) == ',') {
                do {
                    i++;
                } while (i < text.length() && Character.isWhitespace(text.charAt(i)));
                StringBuilder operandCode = new StringBuilder();
                for (; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (Character.isWhitespace(c) || c == ',')
                        break;
                    else
                        operandCode.append(c);
                }
                operand4 = parseOperand(operandCode.toString(), name2Register, localVirtualRegister2Register);
            }
            addInstruction(new BCInstruction(code, operand1, operand2, operand3, operand4));
        }

        private BCOperand parseOperand(String text, Map<String, BCRegister> name2Register, Map<String, BCRegister> localVirtualRegister2Register) {
            if (text.startsWith("%")) {
                String registerName = text.substring(1);
                byte register = switch (registerName.toUpperCase()) {
                    case "BP" -> ByteCode.BP_REGISTER;
                    case "SP" -> ByteCode.SP_REGISTER;
                    case "PC" -> ByteCode.PC_REGISTER;
                    default -> Byte.parseByte(registerName);
                };
                return new BCRegister(register);
            } else if (text.startsWith("@")) {
                return name2Register.get(text.substring(1));
            } else if (text.startsWith("$")) {
                String t = text.substring(1).toLowerCase();
                if (t.startsWith("0x")) {
                    return new BCImmediate8(Long.parseLong(t.substring(2), 16));
                } else if (t.startsWith("0b")) {
                    return new BCImmediate8(Long.parseLong(t.substring(2), 2));
                } else if (t.startsWith("0") && t.length() != 1) {
                    return new BCImmediate8(Long.parseLong(t.substring(1), 8));
                } else {
                    return new BCImmediate8(Long.parseLong(t));
                }
            } else if (text.startsWith("`")) {
                String name = text.substring(1);
                BCRegister register = localVirtualRegister2Register.get(name);
                if (register != null) {
                    return new BCRegister(register.virtualRegister);
                } else {
                    register = allocateVirtualRegister();
                    localVirtualRegister2Register.put(name, register);
                    return register;
                }
            }
            return null;
        }

        private byte getBCType(IRType type) {
            if (type.equals(IRType.getByteType())) return ByteCode.BYTE_TYPE;
            else if (type.equals(IRType.getShortType())) return ByteCode.SHORT_TYPE;
            else if (type.equals(IRType.getIntType())) return ByteCode.INT_TYPE;
            else if (type.equals(IRType.getLongType())) return ByteCode.LONG_TYPE;
            else throw new RuntimeException("Unsupported type: " + type);
        }
    }

    private static final class Tagger extends BCVisitor {
        private final ByteCodeModule module;
        private final Map<String, Map<Long, BCRegister.Interval>> resultMap;
        private final Map<BCControlFlowGraph.BasicBlock, List<Long>> initialRegisters;
        private Map<BCInstruction, List<Long>> instruction2LiveRegisters = null;
        private List<Long> liveRegisters = null;

        public Tagger(ByteCodeModule module, Map<String, Map<Long, BCRegister.Interval>> resultMap) {
            this.module = module;
            this.resultMap = resultMap;
            this.initialRegisters = new LinkedHashMap<>();
        }

        public void tag() {
            this.visitModule(this.module, null);
        }

        @Override
        public Void visitModule(ByteCodeModule module, Object additional) {
            for (BCControlFlowGraph cfg : module.functionName2CFG.values()) {
                this.instruction2LiveRegisters = cfg.instruction2LiveRegisters;
                Stack<BCControlFlowGraph.BasicBlock> toDo = new Stack<>();
                for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                    toDo.push(basicBlock);
                    initialRegisters.put(basicBlock, new ArrayList<>());
                }
                while (!toDo.isEmpty()) {
                    BCControlFlowGraph.BasicBlock basicBlock = toDo.pop();
                    this.tagBasicBlock(basicBlock);
                    List<Long> registers = this.liveRegisters;
                    for (BCControlFlowGraph.BasicBlock from : cfg.inEdges.get(basicBlock)) {
                        List<Long> fromRegisters = this.initialRegisters.get(from);
                        if (!new HashSet<>(fromRegisters).containsAll(registers)) {
                            if (!toDo.contains(from)) toDo.push(from);
                            List<Long> union = this.unionOf(fromRegisters, registers);
                            this.initialRegisters.put(from, union);
                        }
                    }
                }
                initialRegisters.clear();
            }
            module.functionName2CFG.forEach((name, cfg) -> {
                Map<Long, BCRegister.Interval> intervalMap = createIntervalMap(cfg);
                this.resultMap.put(name, intervalMap);
            });
            return null;
        }

        @Override
        public Void visitRegister(BCRegister bcRegister, Object additional) {
            if (bcRegister.virtualRegister != -1 && !this.liveRegisters.contains(bcRegister.virtualRegister))
                this.liveRegisters.add(bcRegister.virtualRegister);
            return null;
        }

        private void tagBasicBlock(BCControlFlowGraph.BasicBlock basicBlock) {
            liveRegisters = initialRegisters.get(basicBlock);
            for (int i = basicBlock.instructions.size() - 1; i >= 0; i--) {
                BCInstruction instruction = basicBlock.instructions.get(i);
                this.instruction2LiveRegisters.put(instruction, liveRegisters);

                this.visitInstruction(instruction, null);

                liveRegisters = new ArrayList<>(liveRegisters);
                liveRegisters.removeAll(instruction.allocatedRegisters);
            }
        }

        private List<Long> unionOf(List<Long> list1, List<Long> list2) {
            Set<Long> union = new HashSet<>(list1);
            union.addAll(list2);
            return new ArrayList<>(union);
        }

        private Map<Long, BCRegister.Interval> createIntervalMap(BCControlFlowGraph cfg) {
            Map<Long, BCRegister.Interval> intervalMap = new TreeMap<>();
            int index = 0;
            for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                for (BCInstruction instruction : basicBlock.instructions) {
                    List<Long> liveRegisters = cfg.instruction2LiveRegisters.get(instruction);
                    for (Long liveRegister : liveRegisters) {
                        if (intervalMap.containsKey(liveRegister)) {
                            intervalMap.get(liveRegister).end = index;
                        } else {
                            intervalMap.put(liveRegister, new BCRegister.Interval(index, index));
                        }
                    }
                    index++;
                }
            }
            return intervalMap;
        }
    }

    private static final class RegisterAllocator extends BCVisitor {
        private final ByteCodeModule module;
        private final Map<String, Map<Long, BCRegister.Interval>> intervalMap;
        private final byte numRegisters;
        private final byte TEMP_REGISTER;
        private List<Byte> usedColors = null;
        private Map<Long, Byte> colorMap = null;
        private List<Long> spilledVirtualRegisters = null;
        private BCControlFlowGraph.BasicBlock currentBasicBlock = null;
        private Map<Long, Long> spilledVirtualRegisterOffsets = null;
        private int index = 0;
        private final List<Byte> usedRegisters = new ArrayList<>();
        private final Queue<BCRegister> registersToBeLoaded = new LinkedList<>();
        private long offset = 0;
        private Queue<Long> registerSpillSlots = null;

        public RegisterAllocator(ByteCodeModule module, Map<String, Map<Long, BCRegister.Interval>> intervalMap) {
            this.module = module;
            this.intervalMap = intervalMap;
            this.numRegisters = 35; // 36-1
            this.TEMP_REGISTER = 35;
        }

        public void allocate() {
            this.visitModule(this.module, null);
        }

        @Override
        public Void visitModule(ByteCodeModule module, Object additional) {
            for (Map.Entry<String, BCControlFlowGraph> entry : module.functionName2CFG.entrySet()) {
                BCControlFlowGraph cfg = entry.getValue();

                Map<Long, BCRegister.Interval> currentIntervalMap = this.intervalMap.get(entry.getKey());

                List<Long> virtualRegisters = new ArrayList<>(currentIntervalMap.keySet());
                Map<Long, List<Long>> interferenceGraph = cfg.interferenceGraph;
                for (Long virtualRegister : virtualRegisters)
                    interferenceGraph.put(virtualRegister, new ArrayList<>());
                for (int i = 0; i < virtualRegisters.size(); i++) {
                    for (int j = i + 1; j < virtualRegisters.size(); j++) {
                        Long var1 = virtualRegisters.get(i);
                        Long var2 = virtualRegisters.get(j);
                        if (currentIntervalMap.get(var1).overlaps(currentIntervalMap.get(var2))) {
                            interferenceGraph.get(var1).add(var2);
                            interferenceGraph.get(var2).add(var1);
                        }
                    }
                }

                this.usedColors = cfg.usedColors;
                this.colorMap = cfg.colorMap;
                this.spilledVirtualRegisters = cfg.spilledVirtualRegisters;

                allocateRegisters(interferenceGraph);

                long spilledVirtualRegisterSize = cfg.spilledVirtualRegisters.size() * 8L;
                BCControlFlowGraph.BasicBlock[] basicBlocks = cfg.basicBlocks.values().toArray(new BCControlFlowGraph.BasicBlock[0]);
                BCControlFlowGraph.BasicBlock begin = basicBlocks[0];
                BCImmediate8 imm = (BCImmediate8) begin.instructions.getFirst().operand1;
                long oldOffset = imm.value;
                this.offset = oldOffset + spilledVirtualRegisterSize;
                for (int i = 0; i < this.usedColors.size(); i++) {
                    begin.instructions.add(1 + i, new BCInstruction(ByteCode.PUSH_8, new BCRegister(this.usedColors.get(i)), null));
                }

                this.spilledVirtualRegisterOffsets = new HashMap<>();
                long regOffset = oldOffset;
                for (Long reg : cfg.spilledVirtualRegisters) {
                    regOffset += 8L;
                    spilledVirtualRegisterOffsets.put(reg, regOffset);
                }

                this.registerSpillSlots = new LinkedList<>();
                for (BCControlFlowGraph.BasicBlock basicBlock : basicBlocks) {
                    this.currentBasicBlock = basicBlock;
                    for (index = 0; index < basicBlock.instructions.size(); index++) {
                        this.visitInstruction(basicBlock.instructions.get(index), additional);
                    }
                }

                imm.value = this.offset;
                BCControlFlowGraph.BasicBlock end = basicBlocks[basicBlocks.length - 1];
                ((BCImmediate8) end.instructions.get(0).operand1).value = this.offset + this.usedColors.size() * 8L;
                ((BCImmediate8) end.instructions.get(2).operand1).value = this.offset;
                for (Byte color : this.usedColors) {
                    end.instructions.add(2, new BCInstruction(ByteCode.POP_8, new BCRegister(color)));
                }
            }
            return null;
        }

        @Override
        public Object visitInstruction(BCInstruction instruction, Object additional) {
            super.visitInstruction(instruction, additional);
            if (!registersToBeLoaded.isEmpty()) {
                Queue<Long> register2RegisterSpillSlot = new LinkedList<>();
                Queue<Byte> virtualRegister2TempRegister = new LinkedList<>();
                List<BCInstruction> instructionsOfLoad = new ArrayList<>();
                for (BCRegister reg : registersToBeLoaded) {
                    byte tempRegister = getFreeRegister();
                    virtualRegister2TempRegister.add(tempRegister);
                    long regSpillSlot = getRegisterSpillSlot();
                    register2RegisterSpillSlot.add(regSpillSlot);
                    instructionsOfLoad.add(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(regSpillSlot), new BCRegister(TEMP_REGISTER)));
                    instructionsOfLoad.add(new BCInstruction(ByteCode.SUB, new BCRegister(ByteCode.BP_REGISTER), new BCRegister(TEMP_REGISTER), new BCRegister(TEMP_REGISTER)));
                    instructionsOfLoad.add(new BCInstruction(ByteCode.STORE_8, new BCRegister(TEMP_REGISTER), new BCRegister(tempRegister)));
                    instructionsOfLoad.add(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.spilledVirtualRegisterOffsets.get(reg.virtualRegister)), new BCRegister(TEMP_REGISTER)));
                    instructionsOfLoad.add(new BCInstruction(ByteCode.SUB, new BCRegister(ByteCode.BP_REGISTER), new BCRegister(TEMP_REGISTER), new BCRegister(TEMP_REGISTER)));
                    instructionsOfLoad.add(new BCInstruction(ByteCode.LOAD_8, new BCRegister(TEMP_REGISTER), new BCRegister(tempRegister)));
                    reg.register = tempRegister;
                }
                this.currentBasicBlock.instructions.addAll(index, instructionsOfLoad);
                index += instructionsOfLoad.size();
                List<BCInstruction> instructionsOfStore = new ArrayList<>();
                while (!registersToBeLoaded.isEmpty()) {
                    BCRegister reg = registersToBeLoaded.poll();
                    byte tempRegister = Objects.requireNonNull(virtualRegister2TempRegister.poll());
                    long regSpillSlot = Objects.requireNonNull(register2RegisterSpillSlot.poll());
                    instructionsOfStore.add(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(this.spilledVirtualRegisterOffsets.get(reg.virtualRegister)), new BCRegister(TEMP_REGISTER)));
                    instructionsOfStore.add(new BCInstruction(ByteCode.SUB, new BCRegister(ByteCode.BP_REGISTER), new BCRegister(TEMP_REGISTER), new BCRegister(TEMP_REGISTER)));
                    instructionsOfStore.add(new BCInstruction(ByteCode.STORE_8, new BCRegister(TEMP_REGISTER), new BCRegister(tempRegister)));
                    instructionsOfStore.add(new BCInstruction(ByteCode.MOV_IMMEDIATE8, new BCImmediate8(regSpillSlot), new BCRegister(TEMP_REGISTER)));
                    instructionsOfStore.add(new BCInstruction(ByteCode.SUB, new BCRegister(ByteCode.BP_REGISTER), new BCRegister(TEMP_REGISTER), new BCRegister(TEMP_REGISTER)));
                    instructionsOfStore.add(new BCInstruction(ByteCode.LOAD_8, new BCRegister(TEMP_REGISTER), new BCRegister(tempRegister)));
                    returnRegisterSpillSlot(regSpillSlot);
                }
                this.currentBasicBlock.instructions.addAll(index + 1, instructionsOfStore);
                index += instructionsOfStore.size();

                registersToBeLoaded.clear();
            }
            usedRegisters.clear();
            return null;
        }

        @Override
        public Void visitRegister(BCRegister bcRegister, Object additional) {
            if (bcRegister.register == -1) {
                if (this.colorMap.containsKey(bcRegister.virtualRegister)) {
                    bcRegister.register = this.colorMap.get(bcRegister.virtualRegister);
                    usedRegisters.add(bcRegister.register);
                } else {
                    registersToBeLoaded.add(bcRegister);
                }
            }
            return null;
        }

        private void allocateRegisters(Map<Long, List<Long>> interferenceGraph) {
            Stack<Long> stack = new Stack<>();
            Map<Long, List<Long>> graphCopy = copyGraph(interferenceGraph);

            boolean simplified;
            do {
                simplified = false;
                List<Long> toRemove = graphCopy.keySet().stream()
                        .filter(v -> graphCopy.get(v).size() < numRegisters)
                        .toList();

                if (!toRemove.isEmpty()) {
                    simplified = true;
                    for (Long v : toRemove) {
                        stack.push(v);
                        removeVertex(graphCopy, v);
                    }
                }
            } while (simplified);

            if (!graphCopy.isEmpty()) {
                Long spillVar = selectSpillVirtualRegister(graphCopy);
                this.spilledVirtualRegisters.add(spillVar);
                removeVertex(graphCopy, spillVar);
                allocateRegisters(graphCopy);
            }
            while (!stack.isEmpty()) {
                Long var = stack.pop();
                Set<Byte> forbiddenColors = new HashSet<>();
                for (Long neighbor : interferenceGraph.get(var)) {
                    if (this.colorMap.containsKey(neighbor)) {
                        forbiddenColors.add(this.colorMap.get(neighbor));
                    }
                }
                for (byte color = 0; color < numRegisters; color++) {
                    if (!forbiddenColors.contains(color)) {
                        this.colorMap.put(var, color);
                        if (!this.usedColors.contains(color)) this.usedColors.add(color);
                        break;
                    }
                }
            }
        }

        private Long selectSpillVirtualRegister(Map<Long, List<Long>> graph) {
            return graph.entrySet().stream()
                    .min(Comparator.comparingLong(e -> e.getValue().size()))
                    .map(Map.Entry::getKey)
                    .orElseThrow(() -> new IllegalStateException("No virtual register to spill"));
        }

        private void removeVertex(Map<Long, List<Long>> graph, Long v) {
            graph.remove(v);
            for (List<Long> neighbors : graph.values()) {
                neighbors.remove(v);
            }
        }

        private Map<Long, List<Long>> copyGraph(Map<Long, List<Long>> original) {
            Map<Long, List<Long>> copy = new HashMap<>();
            for (Map.Entry<Long, List<Long>> entry : original.entrySet()) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            return copy;
        }

        private byte getFreeRegister() {
            for (byte i = 0; i < numRegisters; i++) {
                if (!usedRegisters.contains(i)) {
                    usedRegisters.add(i);
                    return i;
                }
            }
            return -1;
        }

        private long getRegisterSpillSlot() {
            Long result = registerSpillSlots.poll();
            if (result != null) return result;

            offset += 8L;
            return offset;
        }

        private void returnRegisterSpillSlot(long registerSpillSlot) {
            registerSpillSlots.add(registerSpillSlot);
        }
    }

    private static final class Locator {
        private final ByteCodeModule module;
        private long textSectionLength = 0;

        public Locator(ByteCodeModule module) {
            this.module = module;
        }

        public void locate() {
            for (Map.Entry<String, BCControlFlowGraph> entry : module.functionName2CFG.entrySet()) {
                module.functionName2EntryPoint.put(entry.getKey(), textSectionLength);
                BCControlFlowGraph cfg = entry.getValue();
                for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                    cfg.basicBlock2EntryPoint.put(basicBlock.name, textSectionLength);
                    for (BCInstruction instruction : basicBlock.instructions)
                        textSectionLength += instruction.getLength();
                }
            }
        }
    }

    private static final class Redirector extends BCVisitor {
        private final ByteCodeModule module;
        private BCControlFlowGraph currentCFG = null;

        public Redirector(ByteCodeModule module) {
            this.module = module;
        }

        public void redirect() {
            this.visitModule(module, null);
        }

        @Override
        public Object visitModule(ByteCodeModule module, Object additional) {
            for (BCImmediate rodata : module.rodataSection) {
                this.visitOperand(rodata, additional);
            }
            for (BCImmediate data : module.dataSection) {
                this.visitOperand(data, additional);
            }
            for (BCControlFlowGraph cfg : module.functionName2CFG.values()) {
                this.currentCFG = cfg;
                for (BCControlFlowGraph.BasicBlock basicBlock : cfg.basicBlocks.values()) {
                    for (BCInstruction instruction : basicBlock.instructions) {
                        this.visitInstruction(instruction, additional);
                    }
                }
            }
            return null;
        }

        @Override
        public Object visitImmediate8(BCImmediate8 bcImmediate8, Object additional) {
            if (bcImmediate8.comment != null) {
                if (bcImmediate8.comment.equals("<rodata_section_entry_point>")) {
                    bcImmediate8.value = module.getTextSectionLength();
                } else if (bcImmediate8.comment.equals("<add_rodata_section_entry_point>")) {
                    bcImmediate8.value += module.getTextSectionLength();
                } else if (bcImmediate8.comment.equals("<data_section_entry_point>")) {
                    bcImmediate8.value = module.getTextSectionLength() + module.getRodataSectionLength();
                } else if (bcImmediate8.comment.equals("<add_data_section_entry_point>")) {
                    bcImmediate8.value += module.getTextSectionLength() + module.getRodataSectionLength();
                } else if (bcImmediate8.comment.equals("<bss_section_entry_point>")) {
                    bcImmediate8.value = module.getTextSectionLength() + module.getRodataSectionLength() + module.getDataSectionLength();
                } else if (bcImmediate8.comment.equals("<add_bss_section_entry_point>")) {
                    bcImmediate8.value += module.getTextSectionLength() + module.getRodataSectionLength() + module.getDataSectionLength();
                } else if (bcImmediate8.comment.startsWith("<basic_block>")) {
                    bcImmediate8.value = currentCFG.basicBlock2EntryPoint.get(bcImmediate8.comment.substring("<basic_block>".length()));
                } else if (bcImmediate8.comment.startsWith("<function_address>")) {
                    bcImmediate8.value = module.functionName2EntryPoint.get(bcImmediate8.comment.substring("<function_address>".length()));
                }
            }
            return null;
        }
    }
}
