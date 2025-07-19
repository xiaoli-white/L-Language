package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.expression.literal.LCIntegerLiteral;
import ldk.l.lc.util.Position;

public abstract class LCNativeSection extends LCAstNode {
    public String code;

    public LCNativeSection(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    @Override
    public abstract LCNativeSection clone() throws CloneNotSupportedException;

    public static final class LCNativeCode extends LCNativeSection {
        public LCNativeCode(String code, Position pos) {
            this(code, pos, false);
        }

        public LCNativeCode(String code, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.code = code;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitNativeCode(this, additional);
        }

        @Override
        public String toString() {
            return "LCNativeCode{" +
                    "code='" + code + '\'' +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCNativeCode clone() throws CloneNotSupportedException {
            return new LCNativeCode(code, position.clone(), isErrorNode);
        }
    }

    public static final class LCReferenceNativeFile extends LCNativeSection {
        public String name;
        public LCIntegerLiteral beginLine;
        public LCIntegerLiteral endLine;

        public LCReferenceNativeFile(String name, LCIntegerLiteral beginLine, LCIntegerLiteral endLine, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.code = null;

            this.name = name;

            this.beginLine = beginLine;
            this.beginLine.parentNode = this;

            this.endLine = endLine;
            this.endLine.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitReferenceNativeFile(this, additional);
        }

        @Override
        public String toString() {
            return "LCReferenceNativeFile{" +
                    "name='" + name + '\'' +
                    ", beginLine=" + beginLine +
                    ", endLine=" + endLine +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCReferenceNativeFile clone() throws CloneNotSupportedException {
            return new LCReferenceNativeFile(name, beginLine.clone(), endLine.clone(), position.clone(), isErrorNode);
        }
    }
}
