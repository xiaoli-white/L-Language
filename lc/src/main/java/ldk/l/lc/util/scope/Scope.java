package ldk.l.lc.util.scope;

import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.util.symbol.Symbol;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Scope {
    public LinkedHashMap<String, Symbol> name2symbol = new LinkedHashMap<>();
    public Scope enclosingScope;
    public LCAstNode node;

    public Scope(LCAstNode node, Scope enclosingScope) {
        this.node = node;
        this.enclosingScope = enclosingScope;
    }

    public void enter(String name, Symbol symbol) {
        this.name2symbol.put(name, symbol);
    }

    public boolean hasSymbol(String name) {
        return this.name2symbol.containsKey(name);
    }

    public Symbol getSymbol(String name) {
        return this.name2symbol.get(name);
    }

    public Symbol getSymbolCascade(String name) {
        Symbol symbol = this.getSymbol(name);
        if (symbol != null) {
            return symbol;
        } else if (this.enclosingScope != null) {
            return this.enclosingScope.getSymbolCascade(name);
        } else {
            return null;
        }
    }
}