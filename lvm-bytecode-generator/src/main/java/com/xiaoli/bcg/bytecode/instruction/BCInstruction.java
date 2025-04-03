package com.xiaoli.bcg.bytecode.instruction;

import com.xiaoli.bcg.bytecode.BCVisitor;
import com.xiaoli.bcg.bytecode.operand.BCOperand;
import ldk.l.lvm.vm.ByteCode;

import java.util.ArrayList;
import java.util.List;

public class BCInstruction {
    public byte code;
    public BCOperand operand1;
    public BCOperand operand2;
    public BCOperand operand3;
    public BCOperand operand4;
    public final List<Long> allocatedRegisters;

    public BCInstruction(byte code, BCOperand operand1, BCOperand operand2, BCOperand operand3, BCOperand operand4) {
        this.code = code;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
        this.operand4 = operand4;
        this.allocatedRegisters = new ArrayList<>();
    }

    public BCInstruction(byte code, BCOperand operand1, BCOperand operand2, BCOperand operand3) {
        this(code, operand1, operand2, operand3, null);
    }

    public BCInstruction(byte code, BCOperand operand1, BCOperand operand2) {
        this(code, operand1, operand2, null);
    }

    public BCInstruction(byte code, BCOperand operand) {
        this(code, operand, null, null);
    }

    public BCInstruction(byte code) {
        this(code, null, null, null);
    }

    public Object visit(BCVisitor visitor, Object additional) {
        return visitor.visitInstruction(this, additional);
    }

    @Override
    public String toString() {
        return ByteCode.getInstructionName(code) + (operand1 != null ? " " + operand1 : "") + (operand2 != null ? ", " + operand2 : "") + (operand3 != null ? ", " + operand3 : "") + (operand4 != null ? ", " + operand4 : "");
    }

    public byte[] toByteCode() {
        byte[] byteCode = new byte[(int) getLength()];
        byteCode[0] = code;
        long offset = 1;
        if (operand1 != null) {
            byte[] bytes = operand1.toByteCode();
            for (byte b : bytes) byteCode[(int) offset++] = b;
        }
        if (operand2 != null) {
            byte[] bytes = operand2.toByteCode();
            for (byte b : bytes) byteCode[(int) offset++] = b;
        }
        if (operand3 != null) {
            byte[] bytes = operand3.toByteCode();
            for (byte b : bytes) byteCode[(int) offset++] = b;
        }
        if (operand4 != null) {
            byte[] bytes = operand4.toByteCode();
            for (byte b : bytes) byteCode[(int) offset++] = b;
        }
        return byteCode;
    }

    public long getLength() {
        return 1 + (operand1 != null ? operand1.getLength() : 0) + (operand2 != null ? operand2.getLength() : 0) + (operand3 != null ? operand3.getLength() : 0) + (operand4 != null ? operand4.getLength() : 0);
    }
}
