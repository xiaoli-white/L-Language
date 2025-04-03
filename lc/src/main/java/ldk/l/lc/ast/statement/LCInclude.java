package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.expression.literal.LCIntegerLiteral;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCInclude extends LCStatement {
    public String filepath;
    public LCIntegerLiteral beginLine;
    public LCIntegerLiteral endLine;

    public LCInclude(String filepath, LCIntegerLiteral beginLine, LCIntegerLiteral endLine, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.filepath = filepath;

        this.beginLine = beginLine;
        if (this.beginLine != null) this.beginLine.parentNode = this;

        this.endLine = endLine;
        if (this.endLine != null) this.endLine.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitInclude(this, additional);
    }

    @Override
    public String toString() {
        return "LCInclude{" +
                "filepath='" + filepath + '\'' +
                ", beginLine=" + beginLine +
                ", endLine=" + endLine +
                ", annotations=" + Arrays.toString(annotations) +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCInclude clone() throws CloneNotSupportedException {
        return new LCInclude(filepath, beginLine != null ? beginLine.clone() : null, endLine != null ? endLine.clone() : null, position.clone(), isErrorNode);
    }
}
