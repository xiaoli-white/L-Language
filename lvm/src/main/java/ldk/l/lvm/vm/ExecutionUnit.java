package ldk.l.lvm.vm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.math.BigInteger;

public final class ExecutionUnit implements Runnable {
    public final VirtualMachine virtualMachine;
    public long threadID;
    public Arena arena;
    public MemorySegment registers;
    public long flags = 0;

    public ExecutionUnit(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void init(long threadID, long stackStart, long entryPoint) {
        this.threadID = threadID;

        this.arena = Arena.ofShared();
        this.registers = this.arena.allocate(MemoryLayout.sequenceLayout(ByteCode.REGISTER_COUNT, Memory.LAYOUT_LONG));
        setRegister(ByteCode.BP_REGISTER, stackStart);
        setRegister(ByteCode.SP_REGISTER, stackStart);
        setRegister(ByteCode.PC_REGISTER, entryPoint);
    }

    public void execute() {
        Memory memory = virtualMachine.memory;
        loop:
        for (; ; ) {
            long pc = getRegister(ByteCode.PC_REGISTER);
            byte code = memory.getByte(pc++);
//            System.out.printf("%d: %s\n", pc - 1, ByteCode.getInstructionName(code));
            switch (code) {
                case ByteCode.NOP -> setRegister(ByteCode.PC_REGISTER, pc);
                case ByteCode.PUSH_1 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    --sp;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setByte(sp, (byte) (getRegister(register) & 0xFF));
                }
                case ByteCode.PUSH_2 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 2;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setShort(sp, (short) (getRegister(register) & 0xFFFF));
                }
                case ByteCode.PUSH_4 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 4;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setInt(sp, (int) (getRegister(register) & 0xFFFFFFFFL));
                }
                case ByteCode.PUSH_8 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 8;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    memory.setLong(sp, getRegister(register));
                }
                case ByteCode.POP_1 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getByte(sp) & 0xFF);
                    setRegister(ByteCode.SP_REGISTER, sp + 1);
                }
                case ByteCode.POP_2 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getShort(sp) & 0xFFFF);
                    setRegister(ByteCode.SP_REGISTER, sp + 2);
                }
                case ByteCode.POP_4 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getInt(sp) & 0xFFFFFFFFL);
                    setRegister(ByteCode.SP_REGISTER, sp + 4);
                }
                case ByteCode.POP_8 -> {
                    byte register = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.LOAD_1 -> {
                    byte address = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getByte(getRegister(address)) & 0xFF);
                }
                case ByteCode.LOAD_2 -> {
                    byte address = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getShort(getRegister(address)) & 0xFFFF);
                }
                case ByteCode.LOAD_4 -> {
                    byte address = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getInt(getRegister(address)) & 0xFFFFFFFFL);
                }
                case ByteCode.LOAD_8 -> {
                    byte address = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, memory.getLong(getRegister(address)));
                }
                case ByteCode.STORE_1 -> {
                    byte address = memory.getByte(pc++);
                    byte source = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setByte(getRegister(address), (byte) (getRegister(source) & 0xFF));
                }
                case ByteCode.STORE_2 -> {
                    byte address = memory.getByte(pc++);
                    byte source = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setShort(getRegister(address), (short) (getRegister(source) & 0xFFFF));
                }
                case ByteCode.STORE_4 -> {
                    byte address = memory.getByte(pc++);
                    byte source = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setInt(getRegister(address), (int) (getRegister(source) & 0xFFFFFFFFL));
                }
                case ByteCode.STORE_8 -> {
                    byte address = memory.getByte(pc++);
                    byte source = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.setLong(getRegister(address), getRegister(source));
                }
                case ByteCode.CMP -> {
                    byte type = memory.getByte(pc++);
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = getRegister(operand1);
                    long value2 = getRegister(operand2);
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
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | 1;
                    } else {
                        long signedResult = value1 - value2;
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                    }
                    setRegister(ByteCode.FLAGS_REGISTER, flags);
                }
                case ByteCode.ATOMIC_CMP -> {
                    memory.lock();
                    byte type = memory.getByte(pc++);
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = memory.getLong(getRegister(operand1));
                    long value2 = getRegister(operand2);
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
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | 1;
                    } else {
                        long signedResult = value1 - value2;
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                    }
                    setRegister(ByteCode.FLAGS_REGISTER, flags);
                    memory.unlock();
                }
                case ByteCode.MOV_E -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_NE -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_L -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_LE -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_G -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_GE -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UL -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_ULE -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UG -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UGE -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV -> {
                    byte source = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, getRegister(source));
                }
                case ByteCode.MOV_IMMEDIATE1 -> {
                    byte value = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE2 -> {
                    short value = memory.getShort(pc);
                    pc += 2;
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE4 -> {
                    int value = memory.getInt(pc);
                    pc += 4;
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE8 -> {
                    long value = memory.getLong(pc);
                    pc += 8;
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.JUMP -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUMP_IMMEDIATE -> {
                    long address = memory.getLong(pc);
                    setRegister(ByteCode.PC_REGISTER, address);
                }
                case ByteCode.JE -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 1)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JNE -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((getRegister(ByteCode.FLAGS_REGISTER) & ByteCode.ZERO_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JL -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.CARRY_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JLE -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.CARRY_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JG -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.CARRY_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JGE -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.CARRY_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUL -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.UNSIGNED_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JULE -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.UNSIGNED_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUG -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.UNSIGNED_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUGE -> {
                    byte address = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long flags = getRegister(ByteCode.FLAGS_REGISTER);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.UNSIGNED_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.MALLOC -> {
                    byte size = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, memory.allocateMemory(getRegister(size)));
                }
                case ByteCode.FREE -> {
                    byte ptr = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    memory.freeMemory(getRegister(ptr));
                }
                case ByteCode.REALLOC -> {
                    byte ptr = memory.getByte(pc++);
                    byte size = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, memory.reallocateMemory(getRegister(ptr), getRegister(size)));
                }
                case ByteCode.ADD -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) + getRegister(operand2));
                }
                case ByteCode.SUB -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) - getRegister(operand2));
                }
                case ByteCode.MUL -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) * getRegister(operand2));
                }
                case ByteCode.DIV -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) / getRegister(operand2));
                }
                case ByteCode.MOD -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) % getRegister(operand2));
                }
                case ByteCode.AND -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) & getRegister(operand2));
                }
                case ByteCode.OR -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) | getRegister(operand2));
                }
                case ByteCode.XOR -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) ^ getRegister(operand2));
                }
                case ByteCode.NOT -> {
                    byte operand = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, ~getRegister(operand));
                }
                case ByteCode.NEG -> {
                    byte operand = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, -getRegister(operand));
                }
                case ByteCode.SHL -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) << getRegister(operand2));
                }
                case ByteCode.SHR -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) >> getRegister(operand2));
                }
                case ByteCode.USHR -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) >>> getRegister(operand2));
                }
                case ByteCode.INC -> {
                    byte operand = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(operand, getRegister(operand) + 1);
                }
                case ByteCode.DEC -> {
                    byte operand = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(operand, getRegister(operand) - 1);
                }
                case ByteCode.ADD_DOUBLE -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) + Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.SUB_DOUBLE -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) - Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.MUL_DOUBLE -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) * Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.DIV_DOUBLE -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) / Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.MOD_DOUBLE -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) % Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.ADD_FLOAT -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) + Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.SUB_FLOAT -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) - Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.MUL_FLOAT -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) * Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.DIV_FLOAT -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) / Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.MOD_FLOAT -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) % Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.ATOMIC_ADD -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp + getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SUB -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp - getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MUL -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp * getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DIV -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp / getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MOD -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp % getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_AND -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp & getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_OR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp | getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_XOR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp = temp ^ getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_NOT -> {
                    memory.lock();
                    byte operand = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = ~memory.getLong(address);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_NEG -> {
                    memory.lock();
                    byte operand = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = -memory.getLong(address);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SHL -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp <<= getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SHR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp >>= getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_USHR -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = memory.getLong(address);
                    temp >>>= getRegister(operand2);
                    setRegister(result, temp);
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_INC -> {
                    memory.lock();
                    byte operand = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = memory.getLong(address) + 1;
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DEC -> {
                    memory.lock();
                    byte operand = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = memory.getLong(address) - 1;
                    memory.setLong(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(address);
                    temp += Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(address);
                    temp -= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_DOUBLE -> {
                    memory.unlock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(address);
                    temp *= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(address);
                    temp /= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_DOUBLE -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = memory.getDouble(address);
                    temp %= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    memory.setDouble(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(address);
                    temp += Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(address);
                    temp -= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(address);
                    temp *= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(address);
                    temp /= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(address, temp);
                    memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_FLOAT -> {
                    memory.lock();
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte result = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = memory.getFloat(address);
                    temp %= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    memory.setFloat(address, temp);
                    memory.unlock();
                }
                case ByteCode.CAS -> {
                    byte operand1 = memory.getByte(pc++);
                    byte operand2 = memory.getByte(pc++);
                    byte operand3 = memory.getByte(pc++);
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
                    byte address = memory.getByte(pc++);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    memory.setLong(sp, pc);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.INVOKE_IMMEDIATE -> {
                    long address = memory.getLong(pc);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    memory.setLong(sp, pc + 8);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    setRegister(ByteCode.PC_REGISTER, address);
                }
                case ByteCode.RETURN -> {
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(ByteCode.PC_REGISTER, memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.INTERRUPT -> {
                    byte interruptNumber = memory.getByte(pc++);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 16;
                    memory.setLong(sp + 8, getRegister(ByteCode.FLAGS_REGISTER));
                    memory.setLong(sp, pc);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    long idtEntry = getRegister(ByteCode.IDTR_REGISTER) + 8 * interruptNumber;
                    setRegister(ByteCode.PC_REGISTER, memory.getLong(idtEntry));
                }
                case ByteCode.INTERRUPT_RETURN -> {
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(ByteCode.FLAGS_REGISTER, memory.getLong(sp + 8));
                    setRegister(ByteCode.PC_REGISTER, memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 16);
                }
                case ByteCode.TYPE_CAST -> {
                    byte types = memory.getByte(pc++);
                    byte source = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
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
                    byte source = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Double.doubleToLongBits(getRegister(source)));
                }
                case ByteCode.DOUBLE_TO_LONG -> {
                    byte source = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, (long) Double.longBitsToDouble(getRegister(source)));
                }
                case ByteCode.DOUBLE_TO_FLOAT -> {
                    byte source = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Float.floatToIntBits((float) Double.longBitsToDouble(getRegister(source))));
                }
                case ByteCode.FLOAT_TO_DOUBLE -> {
                    byte source = memory.getByte(pc++);
                    byte target = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Double.doubleToLongBits(Float.intBitsToFloat((int) (getRegister(source) & 0xFFFFFFFFL))));
                }
                case ByteCode.OPEN -> {
                    byte pathRegister = memory.getByte(pc++);
                    byte flagsRegister = memory.getByte(pc++);
                    byte modeRegister = memory.getByte(pc++);
                    byte resultRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(pathRegister);
                    StringBuilder path = new StringBuilder();
                    char c;
                    while ((c = (char) memory.getByte(address++)) != '\0') path.append(c);
                    try {
                        setRegister(resultRegister, virtualMachine.open(path.toString(), (int) (getRegister(flagsRegister) & 0xFFFFFFFFL), (int) (getRegister(modeRegister) & 0xFFFFFFFFL)));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CLOSE -> {
                    byte fdRegister = memory.getByte(pc++);
                    byte resultRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(resultRegister, virtualMachine.close(getRegister(fdRegister)));
                }
                case ByteCode.READ -> {
                    byte fdRegister = memory.getByte(pc++);
                    byte buFFerRegister = memory.getByte(pc++);
                    byte countRegister = memory.getByte(pc++);
                    byte resultRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long buFFerAddress = getRegister(buFFerRegister);
                    long count = getRegister(countRegister);
                    byte[] buFFer = new byte[(int) count];
                    int readCount;
                    try {
                        readCount = virtualMachine.read(getRegister(fdRegister), buFFer, (int) count);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setRegister(resultRegister, readCount);
                    for (int i = 0; i < readCount; i++) memory.setByte(buFFerAddress + i, buFFer[i]);
                }
                case ByteCode.WRITE -> {
                    byte fdRegister = memory.getByte(pc++);
                    byte bufferRegister = memory.getByte(pc++);
                    byte countRegister = memory.getByte(pc++);
                    byte resultRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(bufferRegister);
                    long count = getRegister(countRegister);
                    byte[] buFFer = new byte[(int) count];
                    for (int i = 0; i < count; i++) buFFer[i] = memory.getByte(address + i);
                    try {
                        setRegister(resultRegister, virtualMachine.write(getRegister(fdRegister), buFFer));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CREATE_FRAME -> {
                    long size = memory.getLong(pc);
                    setRegister(ByteCode.PC_REGISTER, pc + 8);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    memory.setLong(sp, getRegister(ByteCode.BP_REGISTER));
                    setRegister(ByteCode.BP_REGISTER, sp);
                    setRegister(ByteCode.SP_REGISTER, sp - size);
                }
                case ByteCode.DESTROY_FRAME -> {
                    long size = memory.getLong(pc);
                    setRegister(ByteCode.PC_REGISTER, pc + 8);
                    long sp = getRegister(ByteCode.SP_REGISTER) + size;
                    setRegister(ByteCode.BP_REGISTER, memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.EXIT -> {
                    byte exitCodeRegister = memory.getByte(pc);
                    virtualMachine.exit(getRegister(exitCodeRegister));
                    break loop;
                }
                case ByteCode.EXIT_IMMEDIATE -> {
                    long exitCode = memory.getLong(pc);
                    virtualMachine.exit(exitCode);
                    break loop;
                }
                case ByteCode.GET_FIELD_ADDRESS -> {
                    byte objectRegister = memory.getByte(pc++);
                    long offset = memory.getLong(pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(targetRegister, getRegister(objectRegister) + offset);
                }
                case ByteCode.GET_LOCAL_ADDRESS -> {
                    long offset = memory.getLong(pc);
                    byte targetRegister = memory.getByte(pc + 8);
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                    setRegister(targetRegister, getRegister(ByteCode.BP_REGISTER) - offset);
                }
                case ByteCode.GET_PARAMETER_ADDRESS -> {
                    long offset = memory.getLong(pc);
                    byte targetRegister = memory.getByte(pc + 8);
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                    setRegister(targetRegister, getRegister(ByteCode.BP_REGISTER) + offset);
                }
                case ByteCode.CREATE_THREAD -> {
                    long entryPoint = memory.getLong(pc);
                    byte resultRegister = memory.getByte(pc + 8);
                    setRegister(resultRegister, virtualMachine.createThread(entryPoint));
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                }
                case ByteCode.THREAD_CONTROL -> {
                    byte threadIDRegister = memory.getByte(pc++);
                    byte command = memory.getByte(pc++);
                    ThreadHandle threadHandle = virtualMachine.threadID2Handle.get(getRegister(threadIDRegister));
                    switch (command) {
                        case ByteCode.TC_STOP -> threadHandle.thread.interrupt();
                        case ByteCode.TC_WAIT -> {
                            try {
                                threadHandle.thread.join();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case ByteCode.TC_GET_REGISTER -> {
                            byte register = memory.getByte(pc++);
                            byte target = memory.getByte(pc++);
                            setRegister(ByteCode.PC_REGISTER, pc);
                            setRegister(target, threadHandle.executionUnit.getRegister(register));
                        }
                        case ByteCode.TC_SET_REGISTER -> {
                            byte register = memory.getByte(pc++);
                            byte value = memory.getByte(pc++);
                            setRegister(ByteCode.PC_REGISTER, pc);
                            threadHandle.executionUnit.setRegister(register, getRegister(value));
                        }
                        default -> throw new RuntimeException("Unknown command: " + command);
                    }
                }
                case ByteCode.LOAD_FIELD -> {
                    byte size = memory.getByte(pc++);
                    byte objectRegister = memory.getByte(pc++);
                    long offset = memory.getLong(pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(objectRegister) + offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> memory.getByte(address) & 0xFF;
                        case 2 -> memory.getShort(address) & 0xFFFF;
                        case 4 -> memory.getInt(address) & 0xFFFFFFFFL;
                        case 8 -> memory.getLong(address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_FIELD -> {
                    byte size = memory.getByte(pc++);
                    byte objectRegister = memory.getByte(pc++);
                    long offset = memory.getLong(pc);
                    pc += 8;
                    byte valueRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(objectRegister) + offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> memory.setByte(address, (byte) (value & 0xFF));
                        case 2 -> memory.setShort(address, (short) (value & 0xFFFF));
                        case 4 -> memory.setInt(address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> memory.setLong(address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.LOAD_LOCAL -> {
                    byte size = memory.getByte(pc++);
                    long offset = memory.getByte(pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) - offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> memory.getByte(address) & 0xFF;
                        case 2 -> memory.getShort(address) & 0xFFFF;
                        case 4 -> memory.getInt(address) & 0xFFFFFFFFL;
                        case 8 -> memory.getLong(address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_LOCAL -> {
                    byte size = memory.getByte(pc++);
                    long offset = memory.getByte(pc);
                    pc += 8;
                    byte valueRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) - offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> memory.setByte(address, (byte) (value & 0xFF));
                        case 2 -> memory.setShort(address, (short) (value & 0xFFFF));
                        case 4 -> memory.setInt(address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> memory.setLong(address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.LOAD_PARAMETER -> {
                    byte size = memory.getByte(pc++);
                    long offset = memory.getByte(pc);
                    pc += 8;
                    byte targetRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) + offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> memory.getByte(address) & 0xFF;
                        case 2 -> memory.getShort(address) & 0xFFFF;
                        case 4 -> memory.getInt(address) & 0xFFFFFFFFL;
                        case 8 -> memory.getLong(address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_PARAMETER -> {
                    byte size = memory.getByte(pc++);
                    long offset = memory.getByte(pc);
                    pc += 8;
                    byte valueRegister = memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) + offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> memory.setByte(address, (byte) (value & 0xFF));
                        case 2 -> memory.setShort(address, (short) (value & 0xFFFF));
                        case 4 -> memory.setInt(address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> memory.setLong(address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        this.execute();
    }

    public void destroy() {
        arena.close();
    }

    public long getRegister(byte register) {
        return registers.get(Memory.LAYOUT_LONG, register << 3);
    }

    public void setRegister(byte register, long value) {
        registers.set(Memory.LAYOUT_LONG, register << 3, value);
    }
}