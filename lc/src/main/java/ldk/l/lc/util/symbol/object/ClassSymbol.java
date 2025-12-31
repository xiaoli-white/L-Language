package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCClassDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.*;

public final class ClassSymbol extends ObjectSymbol {
    public LCClassDeclaration declaration;
    public List<VariableSymbol> properties;
    public List<MethodSymbol> constructors;
    public List<MethodSymbol> methods;
    public MethodSymbol destructor;
    public ClassSymbol extended = null;
    public List<InterfaceSymbol> implementedInterfaces = null;
    public List<ClassSymbol> permittedClasses = null;
    private Map<String, String> virtualMethods = null;
    private Map<String, Map<String, String>> interfaceMethodMap = null;

    public ClassSymbol(LCClassDeclaration declaration, Type theType, List<TypeParameterSymbol> typeParameters, long flags, List<String> attributes, List<VariableSymbol> properties, List<MethodSymbol> constructors, List<MethodSymbol> methods, MethodSymbol destructor) {
        super(declaration.getRealPackageName(), declaration.name, theType, SymbolKind.Class, typeParameters, flags, attributes);
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

        this.destructor = destructor;
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
                "properties=" + properties +
                ", constructors=" + constructors +
                ", destructor=" + destructor +
                ", methods=" + methods +
                ", extended=" + extended +
                ", implementedInterfaces=" + implementedInterfaces +
                ", _package='" + _package + '\'' +
                ", typeParameters=" + typeParameters +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    public int getNumTotalProps() {
        int num = this.properties.size();
        if (this.extended != null) {
            num += this.extended.getNumTotalProps();
        }
        return num;
    }

    public int getNumTotalMethods() {
        int num = this.methods.size();
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

    public List<MethodSymbol> getSuperClassConstructors() {
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
    public List<MethodSymbol> getMethods() {
        return new ArrayList<>(this.methods);
    }

    public VariableSymbol[] getAllProperties() {
        List<VariableSymbol> properties = new ArrayList<>();
        if (this.extended != null) {
            properties.addAll(List.of(this.extended.getAllProperties()));
        }
        properties.addAll(this.properties.stream().filter(variableSymbol -> !LCFlags.hasStatic(variableSymbol.flags)).toList());
        return properties.toArray(new VariableSymbol[0]);
    }

    public Map<String, String> getVirtualMethods() {
        return getVirtualMethods(false);
    }

    public Map<String, String> getVirtualMethods(boolean forceRefresh) {
        if (!forceRefresh && virtualMethods != null) return virtualMethods;
        if (extended != null) {
            virtualMethods = new LinkedHashMap<>(extended.getVirtualMethods());
        } else {
            virtualMethods = new LinkedHashMap<>();
        }
        for (MethodSymbol methodSymbol : methods) {
            if (LCFlags.hasStatic(methodSymbol.flags)) continue;
            virtualMethods.put(methodSymbol.getSimpleName(), LCFlags.hasAbstract(methodSymbol.flags) ? "" : methodSymbol.getFullName());
        }
        if (destructor != null) {
            virtualMethods.put(destructor.getSimpleName(), destructor.getFullName());
        }
        return virtualMethods;
    }

    public Map<String, Map<String, String>> getInterfacesMethodMap() {
        return getInterfacesMethodMap(false);
    }

    public Map<String, Map<String, String>> getInterfacesMethodMap(boolean forceRefresh) {
        if (!forceRefresh && interfaceMethodMap != null) return interfaceMethodMap;
        if (extended != null) {
            interfaceMethodMap = new LinkedHashMap<>(extended.getInterfacesMethodMap());
        } else {
            interfaceMethodMap = new LinkedHashMap<>();
        }
        Queue<InterfaceSymbol> queue = new LinkedList<>(implementedInterfaces);
        while (!queue.isEmpty()) {
            InterfaceSymbol interfaceSymbol = queue.poll();
            Map<String, String> map = new LinkedHashMap<>();
            for (MethodSymbol methodSymbol : interfaceSymbol.methods) {
                MethodSymbol symbol = getMethodCascade(methodSymbol.getSimpleName());
                map.put(methodSymbol.getSimpleName(), symbol != null ? symbol.getFullName() : "");
            }
            map.put("<deinit>()V", getFullName() + ".<deinit>()V");
            interfaceMethodMap.put(interfaceSymbol.getFullName(), map);
            queue.addAll(interfaceSymbol.extendedInterfaces);
        }
        return interfaceMethodMap;
    }
}