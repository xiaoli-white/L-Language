package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;

import java.util.Objects;

public abstract class LCCaseLabel extends LCAstNode {
    public LCCaseLabel(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    @Override
    public abstract LCCaseLabel clone() throws CloneNotSupportedException;

    public static class LCDefaultCaseLabel extends LCCaseLabel {
        public LCDefaultCaseLabel(Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
        }

        @Override
        public LCDefaultCaseLabel clone() throws CloneNotSupportedException {
            return new LCDefaultCaseLabel(position.clone(), isErrorNode);
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

    public static class LCConstantCaseLabel extends LCCaseLabel {
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

        @Override
        public LCConstantCaseLabel clone() throws CloneNotSupportedException {
            return new LCConstantCaseLabel(expression.clone(), position.clone(), isErrorNode);
        }
    }

    public static class LCTypeCaseLabel extends LCCaseLabel {
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

        @Override
        public LCTypeCaseLabel clone() throws CloneNotSupportedException {
            return new LCTypeCaseLabel(typeExpression.clone(), position.clone(), isErrorNode);
        }
    }
}
