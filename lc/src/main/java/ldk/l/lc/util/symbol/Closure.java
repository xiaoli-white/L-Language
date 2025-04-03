package ldk.l.lc.util.symbol;

public class Closure {
    public VariableSymbol[] vars;
    public MethodSymbol[] methods;
    public Closure(){
        this.vars = new VariableSymbol[0];
        this.methods = new MethodSymbol[0];
    }
    public String toString() {
        StringBuilder str = new StringBuilder("closure{");
        for (int i = 0; i < this.vars.length; i++) {
            str.append(this.methods[i].name).append(".").append(this.vars[i].name);
            if (i < this.vars.length - 1)
                str.append(", ");
        }
        str.append("}");
        return str.toString();
    }
}
