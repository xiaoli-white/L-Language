package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

import java.util.ArrayList;

public final class LCGoto extends LCStatement {
    public String label;

    public LCGoto(String label, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.label = label;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitGoto(this, additional);
    }

    @Override
    public String toString() {
        return "LCGoto{" +
                "label='" + label + '\'' +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCGoto clone() throws CloneNotSupportedException {
        LCGoto lcGoto = new LCGoto(label, position.clone(), isErrorNode);
        lcGoto.annotations = annotations != null ? new ArrayList<>(annotations) : null;
        return lcGoto;
    }
}
