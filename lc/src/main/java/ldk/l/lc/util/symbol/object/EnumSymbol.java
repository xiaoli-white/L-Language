package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCEnumDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.Arrays;
import java.util.Objects;

public final class EnumSymbol extends ObjectSymbol {
    public LCEnumDeclaration declaration;
    public InterfaceSymbol[] implementedInterfaces = null;
    public EnumFieldSymbol[] fields;
    public VariableSymbol[] properties;
    public MethodSymbol[] constructors;
    public MethodSymbol[] methods;
    public MethodSymbol destructor;

    public EnumSymbol(LCEnumDeclaration declaration, Type theType, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes, EnumFieldSymbol[] fields, VariableSymbol[] properties, MethodSymbol[] constructors, MethodSymbol[] methods, MethodSymbol destructor) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Enum, templateTypeParameters, typeParameters, flags, attributes);
        this.flags |= LCFlags.FINAL;

        this.declaration = declaration;

        this.fields = fields;
        for (EnumFieldSymbol field : this.fields)
            field.enumSymbol = this;

        this.properties = properties;
        for (VariableSymbol prop : this.properties)
            prop.objectSymbol = this;

        this.constructors = constructors;
        for (MethodSymbol symbol : this.constructors)
            symbol.objectSymbol = this;

        this.methods = methods;
        for (MethodSymbol method : this.methods) {
            method.objectSymbol = this;
            if (LCFlags.hasFinal(this.flags)) method.flags |= LCFlags.FINAL;
        }

        this.destructor = destructor;
        if (this.destructor != null) {
            this.destructor.objectSymbol = this;
            if (LCFlags.hasFinal(this.flags)) this.destructor.flags |= LCFlags.FINAL;
        }
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitEnumSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "EnumSymbol{" +
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

    public MethodSymbol getMethodCascade(String simpleName) {
        for (MethodSymbol method : this.methods) {
            if (Objects.equals(method.getSimpleName(), simpleName)) {
                return method;
            }
        }
        // TODO
//        MethodSymbol methodSymbol = ((LCClassDeclaration)getAST(recordSymbol.declaration).getObjectDeclaration(SystemTypes.Record_Type.name)).symbol.getMethodCascade(simpleName);
//        if (methodSymbol != null) return methodSymbol;

        MethodSymbol methodSymbol;
        for (InterfaceSymbol interfaceSymbol : this.implementedInterfaces) {
            methodSymbol = interfaceSymbol.getDefaultMethodCascade(simpleName);
            if (methodSymbol != null) return methodSymbol;
        }
        return null;
    }

    public static class EnumFieldSymbol extends Symbol {
        public EnumSymbol enumSymbol = null;
        public LCEnumDeclaration.LCEnumFieldDeclaration declaration;
        public MethodSymbol constructor = null;

        public EnumFieldSymbol(LCEnumDeclaration.LCEnumFieldDeclaration declaration, Type theType) {
            super(declaration.name, theType, SymbolKind.EnumField);
            this.declaration = declaration;
        }

        @Override
        public Object accept(SymbolVisitor visitor, Object additional) {
            return visitor.visitEnumFieldSymbol(this, additional);
        }

        @Override
        public String toString() {
            return "EnumFieldSymbol{" +
                    "name='" + name + '\'' +
                    ", theType=" + theType +
                    '}';
        }

        public String getFullName() {
            return enumSymbol.getFullName() + "." + name;
        }
    }
}
