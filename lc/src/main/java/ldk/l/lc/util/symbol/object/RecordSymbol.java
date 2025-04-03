package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCRecordDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.Arrays;

public final class RecordSymbol extends ObjectSymbol {
    public LCRecordDeclaration declaration;
    public InterfaceSymbol[] implementedInterfaces = null;
    public VariableSymbol[] fields;
    public VariableSymbol[] properties;
    public MethodSymbol[] constructors;
    public MethodSymbol[] methods;
    public MethodSymbol destructor;

    public RecordSymbol(LCRecordDeclaration declaration, Type theType, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes, VariableSymbol[] fields, VariableSymbol[] properties, MethodSymbol[] constructors, MethodSymbol[] methods, MethodSymbol destructor) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Record, templateTypeParameters, typeParameters, flags, attributes);
        this.flags |= LCFlags.FINAL;

        this.declaration = declaration;

        this.fields = fields;
        for (VariableSymbol field : fields) field.objectSymbol = this;

        this.properties = properties;
        for (VariableSymbol property : properties) property.objectSymbol = this;

        this.constructors = constructors;
        for (MethodSymbol symbol : this.constructors)
            symbol.objectSymbol = this;

        this.methods = methods;
        for (MethodSymbol method : methods) {
            method.objectSymbol = this;
            method.flags |= LCFlags.FINAL;
        }

        this.destructor = destructor;
        if (this.destructor != null) {
            this.destructor.objectSymbol = this;
            this.destructor.flags |= LCFlags.FINAL;
        }
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitRecordSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "RecordSymbol{" +
                "implementedInterfaces=" + Arrays.toString(implementedInterfaces) +
                ", fields=" + Arrays.toString(fields) +
                ", properties=" + Arrays.toString(properties) +
                ", constructors=" + Arrays.toString(constructors) +
                ", destructor=" + destructor +
                ", methods=" + Arrays.toString(methods) +
                ", _package='" + _package + '\'' +
                ", templateTypeParameters=" + Arrays.toString(templateTypeParameters) +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    @Override
    public MethodSymbol[] getMethods() {
        return this.methods;
    }
}
