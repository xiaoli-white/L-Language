package ldk.l.lg.ir.base;

public enum IRCondition {
    Equal("e"),
    NotEqual("ne"),
    Greater("g"),
    GreaterEqual("ge"),
    Less("l"),
    LessEqual("le");
    public final String text;

    IRCondition(String text) {
        this.text = text;
    }
}
