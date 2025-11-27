package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.structure.IRStructure;

import java.util.Objects;

public final class IRStructureType extends IRType {
    public IRStructure structure;
    public IRStructureType(IRStructure structure) {
        this.structure = structure;
    }
    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitStructureType(this, additional);
    }

    @Override
    public String toString() {
        return "structure "+structure.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IRStructureType that)) return false;
        return Objects.equals(structure, that.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(structure);
    }
}
