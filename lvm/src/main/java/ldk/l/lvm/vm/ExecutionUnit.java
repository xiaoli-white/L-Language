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
    public static long flags = 0;
    public static long result = 0;
    public boolean running = false;

    public ExecutionUnit(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void init(long threadID, long stackStart, long entryPoint) {
        this.threadID = threadID;

        this.arena = Arena.ofShared();
        this.registers = this.arena.allocate(MemoryLayout.sequenceLayout(40, Memory.LAYOUT_LONG));
        setRegister(ByteCode.BP_REGISTER, stackStart);
        setRegister(ByteCode.SP_REGISTER, stackStart);
        setRegister(ByteCode.PC_REGISTER, entryPoint);
    }

    public void execute() {
        running = true;
        while (running) {
            long pc = getRegister(ByteCode.PC_REGISTER);
            byte code = virtualMachine.memory.getByte(pc++);
//            System.out.printf("%d: %s\n", pc - 1, ByteCode.getInstructionName(code));
            switch (code) {
                case ByteCode.NOP -> setRegister(ByteCode.PC_REGISTER, pc);
                case ByteCode.PUSH_1 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    --sp;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    virtualMachine.memory.setByte(sp, (byte) (getRegister(register) & 0xFF));
                }
                case ByteCode.PUSH_2 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 2;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    virtualMachine.memory.setShort(sp, (short) (getRegister(register) & 0xFFFF));
                }
                case ByteCode.PUSH_4 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 4;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    virtualMachine.memory.setInt(sp, (int) (getRegister(register) & 0xFFFFFFFFL));
                }
                case ByteCode.PUSH_8 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    sp -= 8;
                    setRegister(ByteCode.SP_REGISTER, sp);
                    virtualMachine.memory.setLong(sp, getRegister(register));
                }
                case ByteCode.POP_1 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, virtualMachine.memory.getByte(sp) & 0xFF);
                    setRegister(ByteCode.SP_REGISTER, sp + 1);
                }
                case ByteCode.POP_2 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, virtualMachine.memory.getShort(sp) & 0xFFFF);
                    setRegister(ByteCode.SP_REGISTER, sp + 2);
                }
                case ByteCode.POP_4 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, virtualMachine.memory.getInt(sp) & 0xFFFFFFFFL);
                    setRegister(ByteCode.SP_REGISTER, sp + 4);
                }
                case ByteCode.POP_8 -> {
                    byte register = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(register, virtualMachine.memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.LOAD_1 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, virtualMachine.memory.getByte(getRegister(address)) & 0xFF);
                }
                case ByteCode.LOAD_2 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, virtualMachine.memory.getShort(getRegister(address)) & 0xFFFF);
                }
                case ByteCode.LOAD_4 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, virtualMachine.memory.getInt(getRegister(address)) & 0xFFFFFFFFL);
                }
                case ByteCode.LOAD_8 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, virtualMachine.memory.getLong(getRegister(address)));
                }
                case ByteCode.STORE_1 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte source = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    virtualMachine.memory.setByte(getRegister(address), (byte) (getRegister(source) & 0xFF));
                }
                case ByteCode.STORE_2 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte source = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    virtualMachine.memory.setShort(getRegister(address), (short) (getRegister(source) & 0xFFFF));
                }
                case ByteCode.STORE_4 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte source = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    virtualMachine.memory.setInt(getRegister(address), (int) (getRegister(source) & 0xFFFFFFFFL));
                }
                case ByteCode.STORE_8 -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    byte source = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    virtualMachine.memory.setLong(getRegister(address), getRegister(source));
                }
                case ByteCode.CMP -> {
                    byte type = virtualMachine.memory.getByte(pc++);
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
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
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | 1;
                    } else {
                        long signedResult = value1 - value2;
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                    }
                }
                case ByteCode.ATOMIC_CMP -> {
                    virtualMachine.memory.lock();
                    byte type = virtualMachine.memory.getByte(pc++);
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = virtualMachine.memory.getLong(getRegister(operand1));
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
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | 1;
                    } else {
                        long signedResult = value1 - value2;
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                    }
                    virtualMachine.memory.unlock();
                }
                case ByteCode.MOV_E -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_NE -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_L -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_LE -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_G -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_GE -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UL -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_ULE -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UG -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV_UGE -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        setRegister(target, getRegister(value));
                }
                case ByteCode.MOV -> {
                    byte source = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, getRegister(source));
                }
                case ByteCode.MOV_IMMEDIATE1 -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE2 -> {
                    short value = virtualMachine.memory.getShort(pc);
                    pc += 2;
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE4 -> {
                    int value = virtualMachine.memory.getInt(pc);
                    pc += 4;
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.MOV_IMMEDIATE8 -> {
                    long value = virtualMachine.memory.getLong(pc);
                    pc += 8;
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, value);
                }
                case ByteCode.JUMP -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUMP_IMMEDIATE -> {
                    long address = virtualMachine.memory.getLong(pc);
                    setRegister(ByteCode.PC_REGISTER, address);
                }
                case ByteCode.JE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JNE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JL -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.CARRY_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JLE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.CARRY_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JG -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.CARRY_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JGE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.CARRY_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUL -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.UNSIGNED_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JULE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.UNSIGNED_MARK) != 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUG -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && (flags & ByteCode.UNSIGNED_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.JUGE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || (flags & ByteCode.UNSIGNED_MARK) == 0)
                        setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.MALLOC -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, virtualMachine.memory.allocateMemory(getRegister(size)));
                }
                case ByteCode.FREE -> {
                    byte ptr = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    virtualMachine.memory.freeMemory(getRegister(ptr));
                }
                case ByteCode.REALLOC -> {
                    byte ptr = virtualMachine.memory.getByte(pc++);
                    byte size = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, virtualMachine.memory.reallocateMemory(getRegister(ptr), getRegister(size)));
                }
                case ByteCode.ADD -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) + getRegister(operand2));
                }
                case ByteCode.SUB -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) - getRegister(operand2));
                }
                case ByteCode.MUL -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) * getRegister(operand2));
                }
                case ByteCode.DIV -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) / getRegister(operand2));
                }
                case ByteCode.MOD -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) % getRegister(operand2));
                }
                case ByteCode.AND -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) & getRegister(operand2));
                }
                case ByteCode.OR -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) | getRegister(operand2));
                }
                case ByteCode.XOR -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) ^ getRegister(operand2));
                }
                case ByteCode.NOT -> {
                    byte operand = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, ~getRegister(operand));
                }
                case ByteCode.NEG -> {
                    byte operand = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, -getRegister(operand));
                }
                case ByteCode.SHL -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) << getRegister(operand2));
                }
                case ByteCode.SHR -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) >> getRegister(operand2));
                }
                case ByteCode.USHR -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, getRegister(operand1) >>> getRegister(operand2));
                }
                case ByteCode.INC -> {
                    byte operand = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(operand, getRegister(operand) + 1);
                }
                case ByteCode.DEC -> {
                    byte operand = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(operand, getRegister(operand) - 1);
                }
                case ByteCode.ADD_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) + Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.SUB_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) - Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.MUL_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) * Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.DIV_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) / Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.MOD_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Double.doubleToLongBits(Double.longBitsToDouble(getRegister(operand1)) % Double.longBitsToDouble(getRegister(operand2))));
                }
                case ByteCode.ADD_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) + Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.SUB_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) - Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.MUL_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) * Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.DIV_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) / Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.MOD_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(result, Float.floatToIntBits(Float.intBitsToFloat((int) getRegister(operand1)) % Float.intBitsToFloat((int) getRegister(operand2))));
                }
                case ByteCode.ATOMIC_ADD -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp + getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SUB -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp - getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MUL -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp * getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DIV -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp / getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MOD -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp % getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_AND -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp & getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_OR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp | getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_XOR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp ^ getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_NOT -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = ~virtualMachine.memory.getLong(address);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_NEG -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = -virtualMachine.memory.getLong(address);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SHL -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp <<= getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SHR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp >>= getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_USHR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    long temp = virtualMachine.memory.getLong(address);
                    temp >>>= getRegister(operand2);
                    setRegister(result, temp);
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_INC -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = virtualMachine.memory.getLong(address) + 1;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DEC -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand);
                    long temp = virtualMachine.memory.getLong(address) - 1;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = virtualMachine.memory.getDouble(address);
                    temp += Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = virtualMachine.memory.getDouble(address);
                    temp -= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_DOUBLE -> {
                    virtualMachine.memory.unlock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = virtualMachine.memory.getDouble(address);
                    temp *= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = virtualMachine.memory.getDouble(address);
                    temp /= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    double temp = virtualMachine.memory.getDouble(address);
                    temp %= Double.longBitsToDouble(getRegister(operand2));
                    setRegister(result, Double.doubleToLongBits(temp));
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = virtualMachine.memory.getFloat(address);
                    temp += Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = virtualMachine.memory.getFloat(address);
                    temp -= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = virtualMachine.memory.getFloat(address);
                    temp *= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = virtualMachine.memory.getFloat(address);
                    temp /= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte result = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(operand1);
                    float temp = virtualMachine.memory.getFloat(address);
                    temp %= Float.intBitsToFloat((int) getRegister(operand2));
                    setRegister(result, Float.floatToIntBits(temp));
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.CAS -> {
                    byte operand1 = virtualMachine.memory.getByte(pc++);
                    byte operand2 = virtualMachine.memory.getByte(pc++);
                    byte operand3 = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long value1 = getRegister(operand1);
                    long value2 = getRegister(operand2);
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK) | 1;
                        setRegister(operand1, getRegister(operand3));
                    } else {
                        long signedResult = getRegister(operand1) - getRegister(operand2);
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                        setRegister(operand2, getRegister(operand1));
                    }
                }
                case ByteCode.INVOKE -> {
                    byte address = virtualMachine.memory.getByte(pc++);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    virtualMachine.memory.setLong(sp, pc);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    setRegister(ByteCode.PC_REGISTER, getRegister(address));
                }
                case ByteCode.INVOKE_IMMEDIATE -> {
                    long address = virtualMachine.memory.getLong(pc);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    virtualMachine.memory.setLong(sp, pc + 8);
                    setRegister(ByteCode.SP_REGISTER, sp);
                    setRegister(ByteCode.PC_REGISTER, address);
                }
                case ByteCode.RETURN -> {
                    long sp = getRegister(ByteCode.SP_REGISTER);
                    setRegister(ByteCode.PC_REGISTER, virtualMachine.memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.GET_RESULT -> {
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, result);
                }
                case ByteCode.SET_RESULT -> {
                    byte value = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    result = getRegister(value);
                }
                case ByteCode.TYPE_CAST -> {
                    byte types = virtualMachine.memory.getByte(pc++);
                    byte source = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
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
                    byte source = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Double.doubleToLongBits(getRegister(source)));
                }
                case ByteCode.DOUBLE_TO_LONG -> {
                    byte source = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, (long) Double.longBitsToDouble(getRegister(source)));
                }
                case ByteCode.DOUBLE_TO_FLOAT -> {
                    byte source = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Float.floatToIntBits((float) Double.longBitsToDouble(getRegister(source))));
                }
                case ByteCode.FLOAT_TO_DOUBLE -> {
                    byte source = virtualMachine.memory.getByte(pc++);
                    byte target = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(target, Double.doubleToLongBits(Float.intBitsToFloat((int) (getRegister(source) & 0xFFFFFFFFL))));
                }
                case ByteCode.OPEN -> {
                    byte pathRegister = virtualMachine.memory.getByte(pc++);
                    byte flagsRegister = virtualMachine.memory.getByte(pc++);
                    byte modeRegister = virtualMachine.memory.getByte(pc++);
                    byte resultRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(pathRegister);
                    StringBuilder path = new StringBuilder();
                    char c;
                    while ((c = (char) virtualMachine.memory.getByte(address++)) != '\0') path.append(c);
                    try {
                        setRegister(resultRegister, virtualMachine.open(path.toString(), (int) (getRegister(flagsRegister) & 0xFFFFFFFFL), (int) (getRegister(modeRegister) & 0xFFFFFFFFL)));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CLOSE -> {
                    byte fdRegister = virtualMachine.memory.getByte(pc++);
                    byte resultRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(resultRegister, virtualMachine.close(getRegister(fdRegister)));
                }
                case ByteCode.READ -> {
                    byte fdRegister = virtualMachine.memory.getByte(pc++);
                    byte buFFerRegister = virtualMachine.memory.getByte(pc++);
                    byte countRegister = virtualMachine.memory.getByte(pc++);
                    byte resultRegister = virtualMachine.memory.getByte(pc++);
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
                    for (int i = 0; i < readCount; i++) virtualMachine.memory.setByte(buFFerAddress + i, buFFer[i]);
                }
                case ByteCode.WRITE -> {
                    byte fdRegister = virtualMachine.memory.getByte(pc++);
                    byte buFFerRegister = virtualMachine.memory.getByte(pc++);
                    byte countRegister = virtualMachine.memory.getByte(pc++);
                    byte resultRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(buFFerRegister);
                    long count = getRegister(countRegister);
                    byte[] buFFer = new byte[(int) count];
                    for (int i = 0; i < count; i++) buFFer[i] = virtualMachine.memory.getByte(address + i);
                    try {
                        setRegister(resultRegister, virtualMachine.write(getRegister(fdRegister), buFFer));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CREATE_FRAME -> {
                    long size = virtualMachine.memory.getLong(pc);
                    setRegister(ByteCode.PC_REGISTER, pc + 8);
                    long sp = getRegister(ByteCode.SP_REGISTER) - 8;
                    virtualMachine.memory.setLong(sp, getRegister(ByteCode.BP_REGISTER));
                    setRegister(ByteCode.BP_REGISTER, sp);
                    setRegister(ByteCode.SP_REGISTER, sp - size);
                }
                case ByteCode.DESTROY_FRAME -> {
                    long size = virtualMachine.memory.getLong(pc);
                    setRegister(ByteCode.PC_REGISTER, pc + 8);
                    long sp = getRegister(ByteCode.SP_REGISTER) + size;
                    setRegister(ByteCode.BP_REGISTER, virtualMachine.memory.getLong(sp));
                    setRegister(ByteCode.SP_REGISTER, sp + 8);
                }
                case ByteCode.EXIT -> {
                    byte exitCodeRegister = virtualMachine.memory.getByte(pc);
                    virtualMachine.exit(getRegister(exitCodeRegister));
                    running = false;
                }
                case ByteCode.EXIT_IMMEDIATE -> {
                    long exitCode = virtualMachine.memory.getLong(pc);
                    virtualMachine.exit(exitCode);
                    running = false;
                }
                case ByteCode.GET_FIELD_ADDRESS -> {
                    byte objectRegister = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getLong(pc);
                    pc += 8;
                    byte targetRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    setRegister(targetRegister, getRegister(objectRegister) + offset);
                }
                case ByteCode.GET_LOCAL_ADDRESS -> {
                    long offset = virtualMachine.memory.getLong(pc);
                    byte targetRegister = virtualMachine.memory.getByte(pc + 8);
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                    setRegister(targetRegister, getRegister(ByteCode.BP_REGISTER) - offset);
                }
                case ByteCode.GET_PARAMETER_ADDRESS -> {
                    long offset = virtualMachine.memory.getLong(pc);
                    byte targetRegister = virtualMachine.memory.getByte(pc + 8);
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                    setRegister(targetRegister, getRegister(ByteCode.BP_REGISTER) + offset);
                }
                case ByteCode.CREATE_THREAD -> {
                    long entryPoint = virtualMachine.memory.getLong(pc);
                    byte resultRegister = virtualMachine.memory.getByte(pc + 8);
                    setRegister(resultRegister, virtualMachine.createThread(entryPoint));
                    setRegister(ByteCode.PC_REGISTER, pc + 9);
                }
                case ByteCode.THREAD_CONTROL -> {
                    byte threadIDRegister = virtualMachine.memory.getByte(pc++);
                    byte command = virtualMachine.memory.getByte(pc++);
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
                            byte register = virtualMachine.memory.getByte(pc++);
                            byte target = virtualMachine.memory.getByte(pc++);
                            setRegister(ByteCode.PC_REGISTER, pc);
                            setRegister(target, threadHandle.executionUnit.getRegister(register));
                        }
                        case ByteCode.TC_SET_REGISTER -> {
                            byte register = virtualMachine.memory.getByte(pc++);
                            byte value = virtualMachine.memory.getByte(pc++);
                            setRegister(ByteCode.PC_REGISTER, pc);
                            threadHandle.executionUnit.setRegister(register, getRegister(value));
                        }
                        default -> throw new RuntimeException("Unknown command: " + command);
                    }
                }
                case ByteCode.LOAD_FIELD -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    byte objectRegister = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getLong(pc);
                    pc += 8;
                    byte targetRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(objectRegister) + offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> virtualMachine.memory.getByte(address) & 0xFF;
                        case 2 -> virtualMachine.memory.getShort(address) & 0xFFFF;
                        case 4 -> virtualMachine.memory.getInt(address) & 0xFFFFFFFFL;
                        case 8 -> virtualMachine.memory.getLong(address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_FIELD -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    byte objectRegister = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getLong(pc);
                    pc += 8;
                    byte valueRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(objectRegister) + offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> virtualMachine.memory.setByte(address, (byte) (value & 0xFF));
                        case 2 -> virtualMachine.memory.setShort(address, (short) (value & 0xFFFF));
                        case 4 -> virtualMachine.memory.setInt(address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> virtualMachine.memory.setLong(address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.LOAD_LOCAL -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getByte(pc);
                    pc += 8;
                    byte targetRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) - offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> virtualMachine.memory.getByte(address) & 0xFF;
                        case 2 -> virtualMachine.memory.getShort(address) & 0xFFFF;
                        case 4 -> virtualMachine.memory.getInt(address) & 0xFFFFFFFFL;
                        case 8 -> virtualMachine.memory.getLong(address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_LOCAL -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getByte(pc);
                    pc += 8;
                    byte valueRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) - offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> virtualMachine.memory.setByte(address, (byte) (value & 0xFF));
                        case 2 -> virtualMachine.memory.setShort(address, (short) (value & 0xFFFF));
                        case 4 -> virtualMachine.memory.setInt(address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> virtualMachine.memory.setLong(address, value);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    }
                }
                case ByteCode.LOAD_PARAMETER -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getByte(pc);
                    pc += 8;
                    byte targetRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) + offset;
                    setRegister(targetRegister, switch (size) {
                        case 1 -> virtualMachine.memory.getByte(address) & 0xFF;
                        case 2 -> virtualMachine.memory.getShort(address) & 0xFFFF;
                        case 4 -> virtualMachine.memory.getInt(address) & 0xFFFFFFFFL;
                        case 8 -> virtualMachine.memory.getLong(address);
                        default -> throw new IllegalStateException("Unexpected size: " + size);
                    });
                }
                case ByteCode.STORE_PARAMETER -> {
                    byte size = virtualMachine.memory.getByte(pc++);
                    long offset = virtualMachine.memory.getByte(pc);
                    pc += 8;
                    byte valueRegister = virtualMachine.memory.getByte(pc++);
                    setRegister(ByteCode.PC_REGISTER, pc);
                    long address = getRegister(ByteCode.BP_REGISTER) + offset;
                    long value = getRegister(valueRegister);
                    switch (size) {
                        case 1 -> virtualMachine.memory.setByte(address, (byte) (value & 0xFF));
                        case 2 -> virtualMachine.memory.setShort(address, (short) (value & 0xFFFF));
                        case 4 -> virtualMachine.memory.setInt(address, (int) (value & 0xFFFFFFFFL));
                        case 8 -> virtualMachine.memory.setLong(address, value);
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