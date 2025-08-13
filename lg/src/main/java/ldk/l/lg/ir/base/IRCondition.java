package ldk.l.lg.ir.base;

public enum IRCondition {
    Equal("e"),
    NotEqual("ne"),
    Greater("g"),
    GreaterEqual("ge"),
    Less("l"),
    LessEqual("le"),
    IfTrue("if_true"),
    IfFalse("if_false");
    public final String text;

    IRCondition(String text) {
        this.text = text;
    }
}
