package ldk.l.lvm.vm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.math.BigInteger;

public final class ExecutionUnit implements Runnable {
    public final VirtualMachine virtualMachine;
    public ThreadHandle threadHandle;
    private Arena arena;
    private MemorySegment registers;

    public ExecutionUnit(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void init(long stackBase, long entryPoint) {
        this.arena = Arena.ofAuto();
        this.registers = this.arena.allocate(MemoryLayout.sequenceLayout(ByteCode.REGISTER_COUNT, Memory.LAYOUT_LONG));
        setRegister(ByteCode.BP_REGISTER, stackBase);
        setRegister(ByteCode.SP_REGISTER, stackBase);
        setRegister(ByteCode.PC_REGISTER, entryPoint);
    }

    public void setThreadHandle(ThreadHandle threadHandle) {
        this.threadHandle = threadHandle;
    }

    public void execute() {
        ThreadHandle threadHandle = this.threadHandle;
        Memory memory = virtualMachine.memory;
//        long start = System.nanoTime();
        loop:
        for (; ; ) {
            long pc = getRegister(ByteCode.PC_REGISTER);
            byte code = memory.getByte(threadHandle, pc++);
//            System.out.printf("%d: %s\n", pc - 1, ByteCode.getInstructionName(code));
            switch (code) {
                case ByteCode.NOP -> setRegister(ByteCode.PC_REGISTER, pc);
                case ByteCode.PUSH_1 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    --sp;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setByte(threadHandle, sp, (byte) (getRegister(register) & 0xFF));
                }
                case ByteCode.PUSH_2 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 2;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setShort(threadHandle, sp, (short) (getRegister(register) & 0xFFFF));
                }
                case ByteCode.PUSH_4 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 4;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setInt(threadHandle, sp, (int) (getRegister(register) & 0xFFFFFFFFL));
                }
                case ByteCode.PUSH_8 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 8;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setLong(threadHandle, sp, getRegister(register));
                }
                case ByteCode.POP_1 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getByte(threadHandle, sp) & 0xFF);
                    setRegister(ByteCode.SP_REGISTER, sp + 1);
                }
                case ByteCode.POP_2 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getShort(threadHandle, sp) & 0xFFFF);
                    setRegister(ByteCode.SP_REGISTER, sp + 2);
                }
                case ByteCode.POP_4 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getInt(threadHandle, sp) & 0xFFFFFFFFL);
                    setRegister(ByteCode.SP_REGISTER, sp + 4);
                }
                case ByteCode.POP_8 -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getLong(threadHandle, sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.LOAD_1 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getByte(threadHandle, getRegister(address)) & 0xFF);
                }
                case ByteCode.LOAD_2 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getShort(threadHandle, getRegister(address)) & 0xFFFF);
                }
                case ByteCode.LOAD_4 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getInt(threadHandle, getRegister(address)) & 0xFFFFFFFFL);
                }
                case ByteCode.LOAD_8 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getLong(threadHandle, getRegister(address)));
                }
                case ByteCode.STORE_1 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte source = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setByte(threadHandle, getRegister(address), (byte) (getRegister(source) & 0xFF));
                }
                case ByteCode.STORE_2 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte source = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setShort(threadHandle, getRegister(address), (short) (getRegister(source) & 0xFFFF));
                }
                case ByteCode.STORE_4 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte source = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setInt(threadHandle, getRegister(address), (int) (getRegister(source) & 0xFFFFFFFFL));
                }
                case ByteCode.STORE_8 -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    byte source = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setLong(threadHandle, getRegister(address), getRegister(source));
                }
                case ByteCode.CMP -> {
                    byte type = memory.getByte(threadHandle, pc++);
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = getRegister(operand1);
                    long value2 = getRegister(operand2);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if (type == ByteCode.FLOAT_TYPE) {
                        float float1 = Float.intBitsToFloat((int) (value1 & 0xFFFFFFFFL));
                        float float2 = Float.intBitsToFloat((int) (value2 & 0xFFFFFFFFL));
                        boolean result = float1 < float2;
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((result ? 0b11 : 0b00) << 1);
                    } else if (type == ByteCode.DOUBLE_TYPE) {
                        double double1 = Double.longBitsToDouble(value1);
                        double double2 = Double.longBitsToDouble(value2);
                        boolean result = double1 < double2;
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((result ? 0b11 : 0b00) << 1);
                    } else {
                        if (type == ByteCode.BYTE_TYPE) {
                            value1 = (byte) (value1 & 0xFF);
                            value2 = (byte) (value2 & 0xFF);
                        } else if (type == ByteCode.SHORT_TYPE) {
                            value1 = (short) (value1 & 0xFFFF);
                            value2 = (short) (value2 & 0xFFFF);
                        } else if (type == ByteCode.INT_TYPE) {
                            value1 = (int) (value1 & 0xFFFFFFFFL);
                            value2 = (int) (value2 & 0xFFFFFFFFL);
                        } else if (type != ByteCode.LONG_TYPE) {
                            throw new RuntimeException("Unsupported type: " + type);
                        }
                        if (value1 == value2) {
                            flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | 1;
                        } else {
                            boolean signedResult = value1 < value2;
                            int unsignedResult = Long.compareUnsigned(value1, value2);
                            flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                        }
                    }
                    setRegister(ByteCode.FLAGS_REGISTER, flags);
                }
                case ByteCode.ATOMIC_CMP -> {
                    memory.lock();
                    byte type = memory.getByte(threadHandle, pc++);
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = memory.getLong(threadHandle, getRegister(operand1));
                    long value2 = getRegister(operand2);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if (type == ByteCode.FLOAT_TYPE) {
                        float float1 = Float.intBitsToFloat((int) (value1 & 0xFFFFFFFFL));
                        float float2 = Float.intBitsToFloat((int) (value2 & 0xFFFFFFFFL));
                        boolean result = float1 < float2;
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((result ? 0b11 : 0b00) << 1);
                    } else if (type == ByteCode.DOUBLE_TYPE) {
                        double double1 = Double.longBitsToDouble(value1);
                        double double2 = Double.longBitsToDouble(value2);
                        boolean result = double1 < double2;
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((result ? 0b11 : 0b00) << 1);
                    } else {
                        if (type == ByteCode.BYTE_TYPE) {
                            value1 = (byte) (value1 & 0xFF);
                            value2 = (byte) (value2 & 0xFF);
                        } else if (type == ByteCode.SHORT_TYPE) {
                            value1 = (short) (value1 & 0xFFFF);
                            value2 = (short) (value2 & 0xFFFF);
                        } else if (type == ByteCode.INT_TYPE) {
                            value1 = (int) (value1 & 0xFFFFFFFFL);
                            value2 = (int) (value2 & 0xFFFFFFFFL);
                        } else if (type != ByteCode.LONG_TYPE) {
                            throw new RuntimeException("Unsupported type: " + type);
                        }
                        if (value1 == value2) {
                            flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | 1;
                        } else {
                            boolean signedResult = value1 < value2;
                            long unsignedResult = Long.compareUnsigned(value1, value2);
                            flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                        }
                    }
                    setRegister(ByteCode.FLAGS_REGISTER, flags);
                    memory.unlock();
                }
                case ByteCode.MOV_E -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_NE -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_L -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_LE -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_G -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_GE -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UL -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_ULE -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UG -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UGE -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV -> {
                    byte source = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, getRegister(source));
                }
                case ByteCode.MOV_IMMEDIATE1 -> {
                    byte value = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE2 -> {
                    short value = memory.getShort(threadHandle, pc);
                    pc += 2;
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE4 -> {
                    int value = memory.getInt(threadHandle, pc);
                    pc += 4;
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE8 -> {
                    long value = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.JUMP -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUMP_IMMEDIATE -> {
                    long address = memory.getLong(threadHandle, pc);
                    setRegister(ByteCode.PC_REGISTER, address);
                }
                case ByteCode.JE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 1)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JNE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JL -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.CARRY_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JLE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.CARRY_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JG -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.CARRY_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JGE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.CARRY_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUL -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.UNSIGNED_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JULE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.UNSIGNED_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUG -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.UNSIGNED_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUGE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.UNSIGNED_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.MALLOC -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, memory.allocateMemory(threadHandle, getRegister(size)));
                }
                case ByteCode.FREE -> {
                    byte ptr = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.freeMemory(threadHandle, getRegister(ptr));
                }
                case ByteCode.REALLOC -> {
                    byte ptr = memory.getByte(threadHandle, pc++);
                    byte size = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, memory.reallocateMemory(threadHandle, getRegister(ptr), getRegister(size)));
                }
                case ByteCode.ADD -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) + getRegister(operand2));
                }
                case ByteCode.SUB -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) - getRegister(operand2));
                }
                case ByteCode.MUL -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) * getRegister(operand2));
                }
                case ByteCode.DIV -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) / getRegister(operand2));
                }
                case ByteCode.MOD -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) % getRegister(operand2));
                }
                case ByteCode.AND -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) & getRegister(operand2));
                }
                case ByteCode.OR -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) | getRegister(operand2));
                }
                case ByteCode.XOR -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) ^ getRegister(operand2));
                }
                case ByteCode.NOT -> {
                    byte operand = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, ~getRegister(operand));
                }
                case ByteCode.NEG -> {
                    byte operand = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, -getRegister(operand));
                }
                case ByteCode.SHL -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) << getRegister(operand2));
                }
                case ByteCode.SHR -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) >> getRegister(operand2));
                }
                case ByteCode.USHR -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) >>> getRegister(operand2));
                }
                case ByteCode.INC -> {
                    byte operand = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(operand, getRegister(operand) + 1);
                }
                case ByteCode.DEC -> {
                    byte operand = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(operand, getRegister(operand) - 1);
                }
                case ByteCode.ADD_DOUBLE -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) + Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.SUB_DOUBLE -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) - Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.MUL_DOUBLE -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) * Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.DIV_DOUBLE -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) / Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.MOD_DOUBLE -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) % Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.ADD_FLOAT -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) + Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.SUB_FLOAT -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) - Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.MUL_FLOAT -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) * Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.DIV_FLOAT -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) / Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.MOD_FLOAT -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) % Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.ATOMIC_ADD -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp + getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SUB -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp - getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MUL -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp * getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DIV -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp / getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MOD -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp % getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_AND -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp & getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_OR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp | getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_XOR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp = temp ^ getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_NOT -> {
                    memory.lock();
                    byte operand = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = ~memory.getLong(threadHandle, address);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_NEG -> {
                    memory.lock();
                    byte operand = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = -memory.getLong(threadHandle, address);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SHL -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp <<= getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SHR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp >>= getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_USHR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(threadHandle, address);
                    temp >>>= getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_INC -> {
                    memory.lock();
                    byte operand = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = memory.getLong(threadHandle, address) + 1;
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DEC -> {
                    memory.lock();
                    byte operand = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = memory.getLong(threadHandle, address) - 1;
                    memory.setLong(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(threadHandle, address);
                    temp += Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(threadHandle, address);
                    temp -= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_DOUBLE -> {
                    memory.unlock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(threadHandle, address);
                    temp *= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(threadHandle, address);
                    temp /= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(threadHandle, address);
                    temp %= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(threadHandle, address);
                    temp += Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(threadHandle, address);
                    temp -= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(threadHandle, address);
                    temp *= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(threadHandle, address);
                    temp /= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte result = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(threadHandle, address);
                    temp %= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(threadHandle, address, temp);
                    memory.unlock();
                }
                case ByteCode.CAS -> {
                    byte operand1 = memory.getByte(threadHandle, pc++);
                    byte operand2 = memory.getByte(threadHandle, pc++);
                    byte operand3 = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = getRegister(operand1);
                    long value2 = getRegister(operand2);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK) | 1;
                        setRegister(operand1, getRegister(operand3));
                    } else {
                        long signedResult = getRegister(operand1) - getRegister(operand2);
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                        setRegister(operand2, getRegister(operand1));
                    }
                    setRegister(ByteCode.FLAGS_REGISTER, flags);
                }
                case ByteCode.INVOKE -> {
                    byte address = memory.getByte(threadHandle, pc++);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    memory.setLong(threadHandle, sp, pc);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.INVOKE_IMMEDIATE -> {
                    long address = memory.getLong(threadHandle, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    memory.setLong(threadHandle, sp, pc + 8);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    setRegister(ByteCode.PC_REGISTER, address);
                }
                case ByteCode.RETURN -> {
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(ByteCode.PC_REGISTER, memory.getLong(threadHandle, sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.INTERRUPT -> {
                    byte interruptNumber = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    interrupt(interruptNumber);
                }
                case ByteCode.INTERRUPT_RETURN -> {
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(ByteCode.FLAGS_REGISTER, memory.getLong(threadHandle, sp + 8));
                    setRegister(ByteCode.PC_REGISTER, memory.getLong(threadHandle, sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 16);
                }
                case ByteCode.TYPE_CAST -> {
                    byte types = memory.getByte(threadHandle, pc++);
                    byte source = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    byte type1 = (byte) (types >> 4);
                    byte type2 = (byte) (types & 0xF);
                    long src = getRegister(source);
                    if (type1 == type2) {
                        setRegister(target, src);
                    } else {
                        long srcBits = (8L << type1) - 1;
                        long sign = (src & (1L << srcBits)) >>> srcBits;
                        long targetBits = (8L << type2) - 1;
                        setRegister(target, sign << targetBits | (src & ((1L << targetBits) - 1)));
                    }
                }
                case ByteCode.LONG_TO_DOUBLE -> {
                    byte source = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Double.doubleToLongBits(getRegister(source)));
                }
                case ByteCode.DOUBLE_TO_LONG -> {
                    byte source = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, (long) Double.longBitsToDouble(getRegister(source)));
                }
                case ByteCode.DOUBLE_TO_FLOAT -> {
                    byte source = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Float.floatToIntBits((float) Double.longBitsToDouble(getRegister(source))));
                }
                case ByteCode.FLOAT_TO_DOUBLE -> {
                    byte source = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Double.doubleToLongBits(Float.intBitsToFloat((int) (getRegister(source) & 0xFFFFFFFFL))));
                }
                case ByteCode.OPEN -> {
                    byte pathRegister = memory.getByte(threadHandle, pc++);
                    byte flagsRegister = memory.getByte(threadHandle, pc++);
                    byte modeRegister = memory.getByte(threadHandle, pc++);
                    byte resultRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(pathRegister);
                    StringBuilder path = new StringBuilder();
                    char c;
                    while ((c = (char) memory.getByte(threadHandle, address++)) != '\0') path.append(c);
                    try {
                        setRegister(resultRegister, virtualMachine.open(path.toString(), (int) (getRegister(flagsRegister) & 0xFFFFFFFFL), (int) (getRegister(modeRegister) & 0xFFFFFFFFL)));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CLOSE -> {
                    byte fdRegister = memory.getByte(threadHandle, pc++);
                    byte resultRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(resultRegister, virtualMachine.close(getRegister(fdRegister)));
                }
                case ByteCode.READ -> {
                    byte fdRegister = memory.getByte(threadHandle, pc++);
                    byte buFFerRegister = memory.getByte(threadHandle, pc++);
                    byte countRegister = memory.getByte(threadHandle, pc++);
                    byte resultRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long bufferAddress = getRegister(buFFerRegister);
                    long count = getRegister(countRegister);
                    byte[] buFFer = new byte[(int) count];
                    int readCount;
                    try {
                        readCount = virtualMachine.read(getRegister(fdRegister), buFFer, (int) count);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setRegister(resultRegister, readCount);
                    for (int i = 0; i < readCount; i++) memory.setByte(threadHandle, bufferAddress + i, buFFer[i]);
                }
                case ByteCode.WRITE -> {
                    byte fdRegister = memory.getByte(threadHandle, pc++);
                    byte bufferRegister = memory.getByte(threadHandle, pc++);
                    byte countRegister = memory.getByte(threadHandle, pc++);
                    byte resultRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(bufferRegister);
                    long count = getRegister(countRegister);
                    byte[] buFFer = new byte[(int) count];
                    for (int i = 0; i < count; i++) buFFer[i] = memory.getByte(threadHandle, address + i);
                    try {
                        setRegister(resultRegister, virtualMachine.write(getRegister(fdRegister), buFFer));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CREATE_FRAME -> {
                    long size = memory.getLong(threadHandle, pc);
                    setRegister(ByteCode.PC_REGISTER, pc + 8);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    memory.setLong(threadHandle, sp, getRegister(ByteCode.BP_REGISTER));
                    setRegister(ByteCode.BP_REGISTER, sp);
                    setRegister(ByteCode.SP_REGISTER, sp - size);
                }
                case ByteCode.DESTROY_FRAME -> {
                    long size = memory.getLong(threadHandle, pc);
                    setRegister(ByteCode.PC_REGISTER, pc + 8);
                    long sp = getRegister(ByteCode.SP_REGISTER) + size;
                    setRegister(ByteCode.BP_REGISTER, memory.getLong(threadHandle, sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.EXIT -> {
                    byte exitCodeRegister = memory.getByte(threadHandle, pc);
                    virtualMachine.exit(getRegister(exitCodeRegister));
                    break loop;
                }
                case ByteCode.EXIT_IMMEDIATE -> {
                    long exitCode = memory.getLong(threadHandle, pc);
                    virtualMachine.exit(exitCode);
                    break loop;
                }
                case ByteCode.GET_FIELD_ADDRESS -> {
                    byte objectRegister = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(targetRegister, getRegister(objectRegister) + offset);
                }
                case ByteCode.GET_LOCAL_ADDRESS -> {
                    long offset = memory.getLong(threadHandle, pc);
                    byte targetRegister = memory.getByte(threadHandle, pc + 8);
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                    setRegister(targetRegister, getRegister(ByteCode.BP_REGISTER) - offset);
                }
                case ByteCode.GET_PARAMETER_ADDRESS -> {
                    long offset = memory.getLong(threadHandle, pc);
                    byte targetRegister = memory.getByte(threadHandle, pc + 8);
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                    setRegister(targetRegister, getRegister(ByteCode.BP_REGISTER) + offset);
                }
                case ByteCode.CREATE_THREAD -> {
                    long entryPoint = memory.getLong(threadHandle, pc);
                    byte resultRegister = memory.getByte(threadHandle, pc + 8);
                    setRegister(resultRegister, virtualMachine.createThread(entryPoint));
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                }
                case ByteCode.THREAD_CONTROL -> {
                    byte threadIDRegister = memory.getByte(threadHandle, pc++);
                    byte command = memory.getByte(threadHandle, pc++);
                    ThreadHandle handle = virtualMachine.threadID2Handle.get(getRegister(threadIDRegister));
                    switch (command) {
                        case ByteCode.TC_STOP -> handle.thread.interrupt();
                        case ByteCode.TC_WAIT -> {
                            try {
                                handle.thread.join();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case ByteCode.TC_GET_REGISTER -> {
                            byte register = memory.getByte(threadHandle, pc++);
                            byte target = memory.getByte(threadHandle, pc++);
                            setRegister(ByteCode.PC_REGISTER, pc);
                            setRegister(target, handle.executionUnit.getRegister(register));
                        }
                        case ByteCode.TC_SET_REGISTER -> {
                            byte register = memory.getByte(threadHandle, pc++);
                            byte value = memory.getByte(threadHandle, pc++);
                            setRegister(ByteCode.PC_REGISTER, pc);
                            handle.executionUnit.setRegister(register, getRegister(value));
                        }
                        default -> throw new RuntimeException("Unknown command: " + command);
                    }
                }
                case ByteCode.LOAD_FIELD -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    byte objectRegister = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(objectRegister) + offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> memory.getByte(threadHandle, address) & 0xFF;
                        case 2 -> memory.getShort(threadHandle, address) & 0xFFFF;
                        case 4 -> memory.getInt(threadHandle, address) & 0xFFFFFFFFL;
                        case 8 -> memory.getLong(threadHandle, address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_FIELD -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    byte objectRegister = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte valueRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(objectRegister) + offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> memory.setByte(threadHandle, address, (byte) (value & 0xFF));
                        case 2 -> memory.setShort(threadHandle, address, (short) (value & 0xFFFF));
                        case 4 -> memory.setInt(threadHandle, address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> memory.setLong(threadHandle, address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.LOAD_LOCAL -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) - offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> memory.getByte(threadHandle, address) & 0xFF;
                        case 2 -> memory.getShort(threadHandle, address) & 0xFFFF;
                        case 4 -> memory.getInt(threadHandle, address) & 0xFFFFFFFFL;
                        case 8 -> memory.getLong(threadHandle, address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_LOCAL -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte valueRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) - offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> memory.setByte(threadHandle, address, (byte) (value & 0xFF));
                        case 2 -> memory.setShort(threadHandle, address, (short) (value & 0xFFFF));
                        case 4 -> memory.setInt(threadHandle, address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> memory.setLong(threadHandle, address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.LOAD_PARAMETER -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) + offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> memory.getByte(threadHandle, address) & 0xFF;
                        case 2 -> memory.getShort(threadHandle, address) & 0xFFFF;
                        case 4 -> memory.getInt(threadHandle, address) & 0xFFFFFFFFL;
                        case 8 -> memory.getLong(threadHandle, address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_PARAMETER -> {
                    byte size = memory.getByte(threadHandle, pc++);
                    long offset = memory.getLong(threadHandle, pc);
                    pc += 8;
                    byte valueRegister = memory.getByte(threadHandle, pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) + offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> memory.setByte(threadHandle, address, (byte) (value & 0xFF));
                        case 2 -> memory.setShort(threadHandle, address, (short) (value & 0xFFFF));
                        case 4 -> memory.setInt(threadHandle, address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> memory.setLong(threadHandle, address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.JUMP_IF_TRUE -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    if (this.getRegister(register) != 0) {
                        setRegister(ByteCode.PC_REGISTER, getRegister(target));
                    } else {
                        setRegister(ByteCode.PC_REGISTER, pc);
                    }
                }
                case ByteCode.JUMP_IF_FALSE -> {
                    byte register = memory.getByte(threadHandle, pc++);
                    byte target = memory.getByte(threadHandle, pc++);
                    if (getRegister(register) == 0) {
                        setRegister(ByteCode.PC_REGISTER, getRegister(target));
                    } else {
                        setRegister(ByteCode.PC_REGISTER, pc);
                    }
                }
            }
        }
//        long end = System.nanoTime();
//        System.out.println("Execution time: " + (end - start) / 1000000 + " ms");
    }

    @Override
    public void run() {
        this.execute();
    }

    public void interrupt(byte interruptNumber) {
        ThreadHandle threadHandle = this.threadHandle;
        Memory memory = this.virtualMachine.memory;
        long sp = getRegister(ByteCode.SP_REGISTER) - 16;
        memory.setLong(threadHandle, sp + 8, getRegister(ByteCode.FLAGS_REGISTER));
        memory.setLong(threadHandle, sp, getRegister(ByteCode.PC_REGISTER));
        setRegister(ByteCode.SP_REGISTER, sp);
        long idtEntry = getRegister(ByteCode.IDTR_REGISTER) + 8 * interruptNumber;
        setRegister(ByteCode.PC_REGISTER, memory.getLong(threadHandle, idtEntry));
    }

    public void destroy() {
        this.arena = null;
    }

    public long getRegister(byte register) {
        return registers.get(Memory.LAYOUT_LONG, register << 3);
    }

    public void setRegister(byte register, long value) {
        registers.set(Memory.LAYOUT_LONG, register << 3, value);
    }
}