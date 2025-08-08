package ldk.l.lc.ast.file;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCSourceFileProxy extends LCExpression {
    public LCSourceFile sourceFile;

    public LCSourceFileProxy(LCSourceFile lcSourceFile, boolean isErrorNode) {
        super(Position.origin, isErrorNode);
        this.sourceFile = lcSourceFile;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSourceFileProxy(this, additional);
    }

    @Override
    public String toString() {
        return "LCSourceFileProxy{" +
                "sourceFile='" + sourceFile.filepath + "'" +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
