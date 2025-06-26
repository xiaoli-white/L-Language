package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.object.AnnotationSymbol;

import java.util.Arrays;
import java.util.Objects;

public final class LCAnnotationDeclaration extends LCObjectDeclaration {
    public AnnotationSymbol symbol = null;
    public LCAnnotationBody annotationBody;

    public LCAnnotationDeclaration(String name, LCAnnotationBody annotationBody, Position position, boolean isErrorNode) {
        super(name, new LCTypeParameter[0], null, position, isErrorNode);
        this.annotationBody = annotationBody;
        this.annotationBody.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitAnnotationDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCAnnotationDeclaration{" +
                "symbol=" + symbol +
                ", annotationBody=" + annotationBody +
                ", scope=" + scope +
                ", modifier=" + modifier +
                ", name='" + name + '\'' +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", body=" + body +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCAnnotationDeclaration clone() throws CloneNotSupportedException {
        return new LCAnnotationDeclaration(name, annotationBody.clone(), position.clone(), isErrorNode);
    }

    public static class LCAnnotationFieldDeclaration extends LCAstNode {
        public AnnotationSymbol.AnnotationFieldSymbol symbol = null;
        public String name;
        public LCTypeExpression typeExpression;
        public LCExpression defaultValue;

        public LCAnnotationFieldDeclaration(String name, LCTypeExpression typeExpression, LCExpression defaultValue, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.name = name;

            this.typeExpression = typeExpression;
            if (this.typeExpression != null) this.typeExpression.parentNode = this;

            this.defaultValue = defaultValue;
            if (this.defaultValue != null) this.defaultValue.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitAnnotationFieldDeclaration(this, additional);
        }

        @Override
        public String toString() {
            return "LCAnnotationFieldDeclaration{" +
                    "symbol=" + symbol +
                    ", name='" + name + '\'' +
                    ", typeExpression=" + typeExpression +
                    ", defaultValue=" + defaultValue +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCAnnotationFieldDeclaration clone() throws CloneNotSupportedException {
            return new LCAnnotationFieldDeclaration(name, typeExpression != null ? typeExpression.clone() : null, defaultValue != null ? defaultValue.clone() : null, position.clone(), isErrorNode);
        }
    }

    public static class LCAnnotationBody extends LCAstNode {
        public Scope scope = null;
        public LCAnnotationFieldDeclaration[] fields;

        public LCAnnotationBody(LCAnnotationFieldDeclaration[] fields, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.fields = fields;
            for (LCAnnotationFieldDeclaration field : fields) field.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitAnnotationBody(this, additional);
        }

        @Override
        public String toString() {
            return "LCAnnotationBody{" +
                    "fields=" + Arrays.toString(fields) +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCAnnotationBody clone() throws CloneNotSupportedException {
            return new LCAnnotationBody(Arrays.copyOf(fields, fields.length), position.clone(), isErrorNode);
        }
    }

    public static class LCAnnotation extends LCAstNode {
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
}
