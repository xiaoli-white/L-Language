package com.xiaoli.bcg.bytecode.operand.immediate;

import com.xiaoli.bcg.bytecode.operand.BCOperand;

public abstract sealed class BCImmediate extends BCOperand permits BCImmediate1, BCImmediate2, BCImmediate4, BCImmediate8 {
    public String comment;

    public BCImmediate(String comment) {
        this.comment = comment;
    }
}
