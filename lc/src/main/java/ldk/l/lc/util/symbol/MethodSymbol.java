package ldk.l.lc.util.symbol;

import ldk.l.lc.ast.expression.LCLambda;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.ArrayList;
import java.util.List;

public final class MethodSymbol extends Symbol {
    public LCMethodDeclaration declaration = null;
    public LCLambda lambdaDeclaration = null;
    public List<Type> parameterTypes;
    public Type returnType;
    public final List<VariableSymbol> vars;
    public MethodKind methodKind;
    public ObjectSymbol objectSymbol = null;
    public Closure closure = null;
    public long flags;
    public List<String> attributes;
    private static int lambdaCount = 0;

    public MethodSymbol(List<Type> parameterTypes, Type returnType, Type theType, MethodKind methodKind, long flags, List<String> attributes) {
        this("<lambda_" + lambdaCount + ">", parameterTypes, returnType, theType, methodKind, flags, attributes);
        lambdaCount++;
    }

    public MethodSymbol(String name, List<Type> parameterTypes, Type returnType, Type theType, MethodKind methodKind, long flags, List<String> attributes) {
        super(name, theType, SymbolKind.Method);
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.vars = new ArrayList<>();
        this.methodKind = methodKind;

        this.flags = flags;
        this.attributes = attributes;
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitMethodSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "MethodSymbol{" +
                "parameterTypes=" + parameterTypes +
                ", returnType=" + returnType +
                ", vars=" + vars +
                ", methodKind=" + methodKind +
                ", closure=" + closure +
                ", flags=" + flags +
                ", attributes=" + attributes +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    public boolean isMethod() {
        return this.methodKind == MethodKind.Method;
    }

    public String descriptor() {
        StringBuilder string = new StringBuilder();
        string.append("(");
        for (Type type : this.parameterTypes) string.append(type.toTypeSignature());
        string.append(")").append(this.returnType.toTypeSignature());
        return string.toString();
    }

    public String getSimpleName() {
        return this.name + this.descriptor();
    }

    public String getFullName() {
        return (this.objectSymbol != null && this.objectSymbol.getFullName() != null && !this.objectSymbol.getFullName().isEmpty() ? this.objectSymbol.getFullName() + "." : "") + this.getSimpleName();
    }

    public int getNumParams() {
        return this.declaration.parameterList.parameters.size();
    }

    public List<LCVariableDeclaration> getParams() {
        return new ArrayList<>(this.declaration.parameterList.parameters);
    }
}