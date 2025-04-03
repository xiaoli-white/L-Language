package com.xiaoli.bcg.bytecode.operand.immediate;

import com.xiaoli.bcg.bytecode.operand.BCOperand;

public abstract class BCImmediate extends BCOperand {
    public String comment;

    public BCImmediate(String comment) {
        this.comment = comment;
    }
}
