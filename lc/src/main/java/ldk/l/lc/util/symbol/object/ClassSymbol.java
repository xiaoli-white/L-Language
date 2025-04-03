package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCClassDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ClassSymbol extends ObjectSymbol {
    public LCClassDeclaration declaration;
    public VariableSymbol[] properties;
    public MethodSymbol[] constructors;
    public MethodSymbol[] methods;
    public MethodSymbol destructor;
    public ClassSymbol extended = null;
    public InterfaceSymbol[] implementedInterfaces = null;
    public ClassSymbol[] permittedClasses = null;

    public ClassSymbol(LCClassDeclaration declaration, Type theType, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes, VariableSymbol[] properties, MethodSymbol[] constructors, MethodSymbol[] methods, MethodSymbol destructors) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Class, templateTypeParameters, typeParameters, flags, attributes);
        this.declaration = declaration;

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

        this.destructor = destructors;
        if (this.destructor != null) {
            this.destructor.objectSymbol = this;
            if (LCFlags.hasFinal(this.flags)) this.destructor.flags |= LCFlags.FINAL;
        }
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitClassSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "ClassSymbol{" +
                "properties=" + Arrays.toString(properties) +
                ", constructors=" + Arrays.toString(constructors) +
                ", destructor=" + destructor +
                ", methods=" + Arrays.toString(methods) +
                ", extended=" + extended +
                ", implementedInterfaces=" + Arrays.toString(implementedInterfaces) +
                ", _package='" + _package + '\'' +
                ", templateTypeParameters=" + Arrays.toString(templateTypeParameters) +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    public int getNumTotalProps() {
        int num = this.properties.length;
        if (this.extended != null) {
            num += this.extended.getNumTotalProps();
        }
        return num;
    }

    public int getNumTotalMethods() {
        int num = this.methods.length;
        if (this.extended != null) {
            num += this.extended.getNumTotalMethods();
        }
        return num;
    }

    public int getPropIndex(VariableSymbol prop) {
        int index = List.of(this.properties).indexOf(prop);
        if (index == -1) {
            if (this.extended != null) index = this.extended.getPropIndex(prop);
        } else {
            if (this.extended != null) index += this.extended.getNumTotalProps();
        }
        return index;
    }

    public MethodSymbol[] getSuperClassConstructors() {
        if (this.extended != null) {
            if (this.extended.constructors == null) {
                this.extended.constructors = this.extended.getSuperClassConstructors();
            }
            return this.extended.constructors;
        } else {
            return null;
        }
    }

    public VariableSymbol getPropertyCascade(String name) {
        for (VariableSymbol prop : this.properties) {
            if (Objects.equals(prop.name, name)) {
                return prop;
            }
        }

        if (this.extended != null) {
            return this.extended.getPropertyCascade(name);
        } else {
            return null;
        }
    }

    public MethodSymbol[] getMethodsCascade(String name) {
        ArrayList<MethodSymbol> methodSymbols = new ArrayList<>();
        for (MethodSymbol method : this.methods) {
            if (Objects.equals(method.name, name)) {
                methodSymbols.add(method);
            }
        }

        if (this.extended != null)
            methodSymbols.addAll(List.of(this.extended.getMethodsCascade(name)));

        return methodSymbols.toArray(new MethodSymbol[0]);
    }

    public MethodSymbol getMethodCascade(String simpleName) {
        for (MethodSymbol method : this.methods) {
            if (Objects.equals(method.getSimpleName(), simpleName)) {
                return method;
            }
        }
        MethodSymbol methodSymbol = null;
        if (this.extended != null)
            methodSymbol = this.extended.getMethodCascade(simpleName);
        if (methodSymbol != null) return methodSymbol;

        for (InterfaceSymbol interfaceSymbol : this.implementedInterfaces) {
            methodSymbol = interfaceSymbol.getDefaultMethodCascade(simpleName);
            if (methodSymbol != null) return methodSymbol;
        }
        return null;
    }

    @Override
    public MethodSymbol[] getMethods() {
        return this.methods;
    }

    public VariableSymbol[] getAllProperties() {
        List<VariableSymbol> properties = new ArrayList<>();
        if (this.extended != null) {
            properties.addAll(List.of(this.extended.getAllProperties()));
        }
        properties.addAll(List.of(this.properties));
        return properties.toArray(new VariableSymbol[0]);
    }
}