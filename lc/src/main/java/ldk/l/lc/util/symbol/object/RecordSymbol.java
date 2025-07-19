package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCRecordDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RecordSymbol extends ObjectSymbol {
    public LCRecordDeclaration declaration;
    public List<InterfaceSymbol> implementedInterfaces = null;
    public List<VariableSymbol> fields;
    public List<VariableSymbol> properties;
    public List<MethodSymbol> constructors;
    public List<MethodSymbol> methods;
    public MethodSymbol destructor;

    public RecordSymbol(LCRecordDeclaration declaration, Type theType, List<TypeParameterSymbol> typeParameters, long flags, List<String> attributes, List<VariableSymbol> fields, List<VariableSymbol> properties, List<MethodSymbol> constructors, List<MethodSymbol> methods, MethodSymbol destructor) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Record, typeParameters, flags, attributes);
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
                "implementedInterfaces=" + implementedInterfaces +
                ", fields=" + fields +
                ", properties=" + properties +
                ", constructors=" + constructors +
                ", destructor=" + destructor +
                ", methods=" + methods +
                ", _package='" + _package + '\'' +
                ", typeParameters=" + typeParameters +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    @Override
    public List<MethodSymbol> getMethods() {
        return new ArrayList<>(this.methods);
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
}
