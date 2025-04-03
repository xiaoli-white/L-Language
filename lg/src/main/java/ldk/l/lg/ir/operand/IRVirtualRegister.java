package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public class IRVirtualRegister extends IROperand {
    public final String name;

    public IRVirtualRegister(String name) {
        this.name = name;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitVirtualRegister(this, additional);
    }

    @Override
    public String toString() {
        return "%" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IRVirtualRegister that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
