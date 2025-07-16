package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.AnnotationSymbol;

import java.util.Arrays;

public class LCAnnotation extends LCAstNode {
    public String name;
    public LCAnnotationField[] arguments;
    public AnnotationSymbol symbol = null;

    public LCAnnotation(String name, LCAnnotationField[] arguments, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;
        this.arguments = arguments;

        for (LCAnnotationField argument : this.arguments) argument.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitAnnotation(this, additional);
    }

    @Override
    public String toString() {
        return "LCAnnotation{" +
                "name='" + name + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCAnnotation clone() throws CloneNotSupportedException {
        return new LCAnnotation(name, Arrays.copyOf(arguments, arguments.length), position.clone(), isErrorNode);
    }

    public static class LCAnnotationField extends LCAstNode {
        public String name;
        public LCExpression value;

        public LCAnnotationField(String name, LCExpression value, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.name = name;

            this.value = value;
            this.value.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitAnnotationField(this, additional);
        }

        @Override
        public String toString() {
            return "LCAnnotationField{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCAnnotationField clone() throws CloneNotSupportedException {
            return new LCAnnotationField(name, value.clone(), position.clone(), isErrorNode);
        }
    }
}
