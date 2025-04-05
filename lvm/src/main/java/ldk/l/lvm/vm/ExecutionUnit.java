package ldk.l.lvm.vm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

public final class ExecutionUnit implements Runnable {
    public final VirtualMachine virtualMachine;
    public long[] registers = new long[40];
    public static long flags = 0;
    public static long result = 0;
    public boolean running = false;

    public ExecutionUnit(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void init(long stackStart, long entryPoint) {
        registers[ByteCode.BP_REGISTER] = stackStart;
        registers[ByteCode.SP_REGISTER] = stackStart;
        registers[ByteCode.PC_REGISTER] = entryPoint;
    }

    public void execute() {
        running = true;
        while (running) {
            byte code = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
//            System.out.printf("%d: %s\n", registers[ByteCode.PC_REGISTER] - 1, ByteCode.getInstructionName(code));
            switch (code) {
                case ByteCode.NOP -> {
                }
                case ByteCode.PUSH_1 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[ByteCode.SP_REGISTER]--;
                    virtualMachine.memory.setByte(registers[ByteCode.SP_REGISTER], (byte) registers[register]);
                }
                case ByteCode.PUSH_2 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[ByteCode.SP_REGISTER] -= 2;
                    virtualMachine.memory.setShort(registers[ByteCode.SP_REGISTER], (short) registers[register]);
                }
                case ByteCode.PUSH_4 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[ByteCode.SP_REGISTER] -= 4;
                    virtualMachine.memory.setInt(registers[ByteCode.SP_REGISTER], (int) registers[register]);
                }
                case ByteCode.PUSH_8 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[ByteCode.SP_REGISTER] -= 8;
                    virtualMachine.memory.setLong(registers[ByteCode.SP_REGISTER], registers[register]);
                }
                case ByteCode.POP_1 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[register] = (virtualMachine.memory.getByte(registers[ByteCode.SP_REGISTER]) & 0xff);
                    registers[ByteCode.SP_REGISTER]++;
                }
                case ByteCode.POP_2 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[register] = (virtualMachine.memory.getShort(registers[ByteCode.SP_REGISTER]) & 0xffff);
                    registers[ByteCode.SP_REGISTER] += 2;
                }
                case ByteCode.POP_4 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[register] = (virtualMachine.memory.getInt(registers[ByteCode.SP_REGISTER]) & 0xffffffffL);
                    registers[ByteCode.SP_REGISTER] += 4;
                }
                case ByteCode.POP_8 -> {
                    byte register = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[register] = virtualMachine.memory.getLong(registers[ByteCode.SP_REGISTER]);
                    registers[ByteCode.SP_REGISTER] += 8;
                }
                case ByteCode.LOAD_1 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = virtualMachine.memory.getByte(registers[address]) & 0xff;
                }
                case ByteCode.LOAD_2 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = virtualMachine.memory.getShort(registers[address]) & 0xffff;
                }
                case ByteCode.LOAD_4 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = virtualMachine.memory.getInt(registers[address]) & 0xffffffffL;
                }
                case ByteCode.LOAD_8 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = virtualMachine.memory.getLong(registers[address]);
                }
                case ByteCode.STORE_1 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    virtualMachine.memory.setByte(registers[address], (byte) (registers[source] & 0xff));
                }
                case ByteCode.STORE_2 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    virtualMachine.memory.setShort(registers[address], (short) (registers[source] & 0xffff));
                }
                case ByteCode.STORE_4 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    virtualMachine.memory.setInt(registers[address], (int) (registers[source] & 0xffffffffL));
                }
                case ByteCode.STORE_8 -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    virtualMachine.memory.setLong(registers[address], registers[source]);
                }
                case ByteCode.CMP -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long value1 = registers[operand1];
                    long value2 = registers[operand2];
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK) | 1;
                    } else {
                        long signedResult = registers[operand1] - registers[operand2];
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                    }
                }
                case ByteCode.ATOMIC_CMP -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long temp = virtualMachine.memory.getLong(registers[operand1]);
                    long value2 = registers[operand2];
                    if (temp == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK) | 1;
                    } else {
                        long signedResult = registers[operand1] - registers[operand2];
                        long unsignedResult = new BigInteger(Long.toUnsignedString(temp)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                    }
                    virtualMachine.memory.unlock();
                }
                case ByteCode.MOV_E -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_NE -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_L -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_LE -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_G -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_GE -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_UL -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_ULE -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_UG -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV_UGE -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        registers[target] = registers[value];
                }
                case ByteCode.MOV -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = registers[source];
                }
                case ByteCode.MOV_IMMEDIATE1 -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = value;
                }
                case ByteCode.MOV_IMMEDIATE2 -> {
                    short value = virtualMachine.memory.getShort(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] += 2;
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = value;
                }
                case ByteCode.MOV_IMMEDIATE4 -> {
                    int value = virtualMachine.memory.getInt(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] += 4;
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = value;
                }
                case ByteCode.MOV_IMMEDIATE8 -> {
                    long value = virtualMachine.memory.getLong(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] += 8;
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = value;
                }
                case ByteCode.JUMP -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JUMP_IMMEDIATE -> {
                    long address = virtualMachine.memory.getLong(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] = address;
                }
                case ByteCode.JE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JNE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JL -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JLE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 1)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JG -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JGE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.CARRY_MARK) >> 1) == 0)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JUL -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JULE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 1)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JUG -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 0 && ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.JUGE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    if ((flags & ByteCode.ZERO_MARK) == 1 || ((flags & ByteCode.UNSIGNED_MARK) >> 2) == 0)
                        registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.MALLOC -> {
                    byte size = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = virtualMachine.memory.allocateMemory(registers[size]);
                }
                case ByteCode.FREE -> {
                    byte ptr = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    virtualMachine.memory.freeMemory(registers[ptr]);
                }
                case ByteCode.REALLOC -> {
                    byte ptr = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte size = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = virtualMachine.memory.reallocateMemory(registers[ptr], registers[size]);
                }
                case ByteCode.ADD -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] + registers[operand2];
                }
                case ByteCode.SUB -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] - registers[operand2];
                }
                case ByteCode.MUL -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] * registers[operand2];
                }
                case ByteCode.DIV -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] / registers[operand2];
                }
                case ByteCode.MOD -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] % registers[operand2];
                }
                case ByteCode.AND -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] & registers[operand2];
                }
                case ByteCode.OR -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] | registers[operand2];
                }
                case ByteCode.XOR -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] ^ registers[operand2];
                }
                case ByteCode.NOT -> {
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = ~registers[operand];
                }
                case ByteCode.NEG -> {
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = -registers[operand];
                }
                case ByteCode.SHL -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] << registers[operand2];
                }
                case ByteCode.SHR -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] >> registers[operand2];
                }
                case ByteCode.USHR -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = registers[operand1] >>> registers[operand2];
                }
                case ByteCode.INC -> {
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[operand]++;
                }
                case ByteCode.DEC -> {
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[operand]--;
                }
                case ByteCode.ADD_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Double.doubleToLongBits(Double.longBitsToDouble(registers[operand1]) + Double.longBitsToDouble(registers[operand2]));
                }
                case ByteCode.SUB_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Double.doubleToLongBits(Double.longBitsToDouble(registers[operand1]) - Double.longBitsToDouble(registers[operand2]));
                }
                case ByteCode.MUL_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Double.doubleToLongBits(Double.longBitsToDouble(registers[operand1]) * Double.longBitsToDouble(registers[operand2]));
                }
                case ByteCode.DIV_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Double.doubleToLongBits(Double.longBitsToDouble(registers[operand1]) / Double.longBitsToDouble(registers[operand2]));
                }
                case ByteCode.MOD_DOUBLE -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Double.doubleToLongBits(Double.longBitsToDouble(registers[operand1]) % Double.longBitsToDouble(registers[operand2]));
                }
                case ByteCode.ADD_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Float.floatToIntBits(Float.intBitsToFloat((int) registers[operand1]) + Float.intBitsToFloat((int) registers[operand2]));
                }
                case ByteCode.SUB_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Float.floatToIntBits(Float.intBitsToFloat((int) registers[operand1]) - Float.intBitsToFloat((int) registers[operand2]));
                }
                case ByteCode.MUL_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Float.floatToIntBits(Float.intBitsToFloat((int) registers[operand1]) * Float.intBitsToFloat((int) registers[operand2]));
                }
                case ByteCode.DIV_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Float.floatToIntBits(Float.intBitsToFloat((int) registers[operand1]) / Float.intBitsToFloat((int) registers[operand2]));
                }
                case ByteCode.MOD_FLOAT -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[result] = Float.floatToIntBits(Float.intBitsToFloat((int) registers[operand1]) % Float.intBitsToFloat((int) registers[operand2]));
                }
                case ByteCode.ATOMIC_ADD -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp + registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SUB -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp - registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MUL -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp * registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DIV -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp / registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MOD -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp % registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_AND -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp & registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_OR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp | registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_XOR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp = temp ^ registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_NOT -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand];
                    long temp = ~virtualMachine.memory.getLong(address);
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_NEG -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand];
                    long temp = -virtualMachine.memory.getLong(address);
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SHL -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp <<= registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SHR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp >>= registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_USHR -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    long temp = virtualMachine.memory.getLong(address);
                    temp >>>= registers[operand2];
                    registers[result] = temp;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_INC -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand];
                    long temp = virtualMachine.memory.getLong(address) + 1;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DEC -> {
                    virtualMachine.memory.lock();
                    byte operand = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand];
                    long temp = virtualMachine.memory.getLong(address) - 1;
                    virtualMachine.memory.setLong(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    double temp = virtualMachine.memory.getDouble(address);
                    temp += Double.longBitsToDouble(registers[operand2]);
                    registers[result] = Double.doubleToLongBits(temp);
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    double temp = virtualMachine.memory.getDouble(address);
                    temp -= Double.longBitsToDouble(registers[operand2]);
                    registers[result] = Double.doubleToLongBits(temp);
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_DOUBLE -> {
                    virtualMachine.memory.unlock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    double temp = virtualMachine.memory.getDouble(address);
                    temp *= Double.longBitsToDouble(registers[operand2]);
                    registers[result] = Double.doubleToLongBits(temp);
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    double temp = virtualMachine.memory.getDouble(address);
                    temp /= Double.longBitsToDouble(registers[operand2]);
                    registers[result] = Double.doubleToLongBits(temp);
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_DOUBLE -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    double temp = virtualMachine.memory.getDouble(address);
                    temp %= Double.longBitsToDouble(registers[operand2]);
                    registers[result] = Double.doubleToLongBits(temp);
                    virtualMachine.memory.setDouble(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_ADD_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    float temp = virtualMachine.memory.getFloat(address);
                    temp += Float.intBitsToFloat((int) registers[operand2]);
                    registers[result] = Float.floatToIntBits(temp);
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_SUB_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    float temp = virtualMachine.memory.getFloat(address);
                    temp -= Float.intBitsToFloat((int) registers[operand2]);
                    registers[result] = Float.floatToIntBits(temp);
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MUL_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    float temp = virtualMachine.memory.getFloat(address);
                    temp *= Float.intBitsToFloat((int) registers[operand2]);
                    registers[result] = Float.floatToIntBits(temp);
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_DIV_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    float temp = virtualMachine.memory.getFloat(address);
                    temp /= Float.intBitsToFloat((int) registers[operand2]);
                    registers[result] = Float.floatToIntBits(temp);
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.ATOMIC_MOD_FLOAT -> {
                    virtualMachine.memory.lock();
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte result = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[operand1];
                    float temp = virtualMachine.memory.getFloat(address);
                    temp %= Float.intBitsToFloat((int) registers[operand2]);
                    registers[result] = Float.floatToIntBits(temp);
                    virtualMachine.memory.setFloat(address, temp);
                    virtualMachine.memory.unlock();
                }
                case ByteCode.CAS -> {
                    byte operand1 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand2 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte operand3 = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long value1 = registers[operand1];
                    long value2 = registers[operand2];
                    if (value1 == value2) {
                        flags = (flags & ~ByteCode.ZERO_MARK) | 1;
                        registers[operand1] = registers[operand3];
                    } else {
                        long signedResult = registers[operand1] - registers[operand2];
                        long unsignedResult = new BigInteger(Long.toUnsignedString(value1)).compareTo(new BigInteger(Long.toUnsignedString(value2)));
                        flags = (flags & ~ByteCode.ZERO_MARK & ~ByteCode.CARRY_MARK & ~ByteCode.UNSIGNED_MARK) | ((signedResult < 0 ? 1 : 0) << 1) | ((unsignedResult < 0 ? 1 : 0) << 2);
                        registers[operand2] = registers[operand1];
                    }
                }
                case ByteCode.INVOKE -> {
                    byte address = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[ByteCode.SP_REGISTER] -= 8;
                    virtualMachine.memory.setLong(registers[ByteCode.SP_REGISTER], registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] = registers[address];
                }
                case ByteCode.INVOKE_IMMEDIATE -> {
                    long address = virtualMachine.memory.getLong(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] += 8;
                    registers[ByteCode.SP_REGISTER] -= 8;
                    virtualMachine.memory.setLong(registers[ByteCode.SP_REGISTER], registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] = address;
                }
                case ByteCode.RETURN -> {
                    registers[ByteCode.PC_REGISTER] = virtualMachine.memory.getLong(registers[ByteCode.SP_REGISTER]);
                    registers[ByteCode.SP_REGISTER] += 8;
                }
                case ByteCode.GET_RESULT -> {
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = result;
//                    System.out.println(result);
                }
                case ByteCode.SET_RESULT -> {
                    byte value = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    result = registers[value];
                }
                case ByteCode.LONG_TO_BYTE -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (byte) registers[source];
                }
                case ByteCode.LONG_TO_SHORT -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (short) registers[source];
                }
                case ByteCode.LONG_TO_INT -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (int) registers[source];
                }
                case ByteCode.BYTE_TO_LONG -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (registers[source] & 0xffL);
                }
                case ByteCode.SHORT_TO_LONG -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (registers[source] & 0xffffL);
                }
                case ByteCode.INT_TO_LONG -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (registers[source] & 0xffffffffL);
                }
                case ByteCode.LONG_TO_DOUBLE -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = Double.doubleToLongBits(registers[source]);
                }
                case ByteCode.DOUBLE_TO_LONG -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = (long) Double.longBitsToDouble(registers[source]);
                }
                case ByteCode.DOUBLE_TO_FLOAT -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = Float.floatToIntBits((float) Double.longBitsToDouble(registers[source]));
                }
                case ByteCode.FLOAT_TO_DOUBLE -> {
                    byte source = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte target = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[target] = Double.doubleToLongBits(Float.intBitsToFloat((int) registers[source]));
                }
                case ByteCode.OPEN -> {
                    byte pathRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte flagsRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte modeRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte resultRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[pathRegister];
                    StringBuilder path = new StringBuilder();
                    char c;
                    while ((c = (char) virtualMachine.memory.getByte(address++)) != '\0') path.append(c);
                    try {
                        registers[resultRegister] = virtualMachine.open(path.toString(), (int) registers[flagsRegister], (int) registers[modeRegister]);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CLOSE -> {
                    byte fdRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte resultRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    registers[resultRegister] = virtualMachine.close((int) registers[fdRegister]);
                }
                case ByteCode.READ -> {
                    byte fdRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte bufferRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte countRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte resultRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long bufferAddress = registers[bufferRegister];
                    long count = registers[countRegister];
                    byte[] buffer = new byte[(int) count];
                    int readCount;
                    try {
                        readCount = virtualMachine.read((int) registers[fdRegister], buffer, (int) count);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    registers[resultRegister] = readCount;
                    for (int i = 0; i < readCount; i++) virtualMachine.memory.setByte(bufferAddress + i, buffer[i]);
                }
                case ByteCode.WRITE -> {
                    byte fdRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte bufferRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte countRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    byte resultRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]++);
                    long address = registers[bufferRegister];
                    long count = registers[countRegister];
                    byte[] buffer = new byte[(int) count];
                    for (int i = 0; i < count; i++) buffer[i] = virtualMachine.memory.getByte(address + i);
                    try {
                        registers[resultRegister] = virtualMachine.write((int) registers[fdRegister], buffer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ByteCode.CREATE_FRAME -> {
                    long size = virtualMachine.memory.getLong(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] += 8;
                    registers[ByteCode.SP_REGISTER] -= 8;
                    virtualMachine.memory.setLong(registers[ByteCode.SP_REGISTER], registers[ByteCode.BP_REGISTER]);
                    registers[ByteCode.BP_REGISTER] = registers[ByteCode.SP_REGISTER];
                    registers[ByteCode.SP_REGISTER] -= size;
                }
                case ByteCode.DESTROY_FRAME -> {
                    long size = virtualMachine.memory.getLong(registers[ByteCode.PC_REGISTER]);
                    registers[ByteCode.PC_REGISTER] += 8;
                    registers[ByteCode.SP_REGISTER] += size;
                    registers[ByteCode.BP_REGISTER] = virtualMachine.memory.getLong(registers[ByteCode.SP_REGISTER]);
                    registers[ByteCode.SP_REGISTER] += 8;
                }
                case ByteCode.EXIT -> {
                    byte exitCodeRegister = virtualMachine.memory.getByte(registers[ByteCode.PC_REGISTER]);
                    virtualMachine.exit(registers[exitCodeRegister]);
                    running = false;
                }
                case ByteCode.EXIT_IMMEDIATE -> {
                    long exitCode = virtualMachine.memory.getLong(registers[ByteCode.PC_REGISTER]);
                    virtualMachine.exit(exitCode);
                    running = false;
                }
            }
        }
    }

    @Override
    public void run() {
        this.execute();
    }
}