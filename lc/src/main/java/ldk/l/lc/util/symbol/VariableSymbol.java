package ldk.l.lc.util.symbol;

import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.List;

public final class VariableSymbol extends Symbol {
    public ObjectSymbol objectSymbol = null;
    public LCVariableDeclaration declaration;
    public long flags;
    public List<String> attributes;

    public VariableSymbol(LCVariableDeclaration declaration, Type theType, long flags, List<String> attributes) {
        super(declaration.name, theType, SymbolKind.Variable);
        this.declaration = declaration;

        this.flags = flags;
        this.attributes = attributes;
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitVariableSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "VariableSymbol{" +
                "flags=" + flags +
                ", attributes=" + attributes +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }
}