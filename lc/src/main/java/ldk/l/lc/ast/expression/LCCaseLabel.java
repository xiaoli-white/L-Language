package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;

public abstract class LCCaseLabel extends LCAstNode {
    public LCCaseLabel(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public static final class LCDefaultCaseLabel extends LCCaseLabel {
        public LCDefaultCaseLabel(Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitDefaultCaseLabel(this, additional);
        }

        @Override
        public String toString() {
            return "LCDefaultCaseLabel{" +
                    "pos=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }
    }

    public static final class LCConstantCaseLabel extends LCCaseLabel {
        public LCExpression expression;

        public LCConstantCaseLabel(LCExpression expression, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.expression = expression;
            this.expression.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitConstantCaseLabel(this, additional);
        }

        @Override
        public String toString() {
            return "LCConstantCaseLabel{" +
                    "expression=" + expression +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }
    }

    public static final class LCTypeCaseLabel extends LCCaseLabel {
        public LCTypeExpression typeExpression;

        public LCTypeCaseLabel(LCTypeExpression typeExpression, Position position, boolean isErrorNode) {
            super(position, isErrorNode);
            this.typeExpression = typeExpression;
            this.typeExpression.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitTypeCaseLabel(this, additional);
        }

        @Override
        public String toString() {
            return "LCTypeCaseLabel{" +
                    "typeExpression=" + typeExpression +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }
    }
}
