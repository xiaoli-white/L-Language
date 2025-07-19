package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.ArrayList;
import java.util.List;

public final class AnnotationSymbol extends ObjectSymbol {
    public LCAnnotationDeclaration declaration;
    public List<AnnotationFieldSymbol> fields;

    public AnnotationSymbol(LCAnnotationDeclaration declaration, Type theType, List<TypeParameterSymbol> typeParameters, long flags, List<String> attributes, List<AnnotationFieldSymbol> fields) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Annotation, typeParameters, flags, attributes);
        this.declaration = declaration;

        this.fields = fields;
        for (AnnotationFieldSymbol field : this.fields) field.annotationSymbol = this;
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitAnnotationSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "AnnotationSymbol{" +
                "fields=" + fields +
                ", _package='" + _package + '\'' +
                ", typeParameters=" + typeParameters +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    @Override
    public List<MethodSymbol> getMethods() {
        return new ArrayList<>();
    }

    public static final class AnnotationFieldSymbol extends Symbol {
        public AnnotationSymbol annotationSymbol = null;
        public LCAnnotationDeclaration.LCAnnotationFieldDeclaration declaration;

        public AnnotationFieldSymbol(LCAnnotationDeclaration.LCAnnotationFieldDeclaration declaration, Type theType) {
            super(declaration.name, theType, SymbolKind.AnnotationField);
            this.declaration = declaration;
        }

        @Override
        public Object accept(SymbolVisitor visitor, Object additional) {
            return visitor.visitAnnotationFieldSymbol(this, additional);
        }

        @Override
        public String toString() {
            return "AnnotationFieldSymbol{" +
                    "name='" + name + '\'' +
                    ", theType=" + theType +
                    '}';
        }
    }
}
