package ldk.l.lc.util.symbol;

public class TemplateTypeParameterSymbol extends Symbol {
    public TemplateTypeParameterSymbol(String name) {
        super(name, null, SymbolKind.TemplateTypeParameter);
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitTemplateTypeParameterSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "TemplateTypeParameterSymbol{" +
                "name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }
}
