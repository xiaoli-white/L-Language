package ldk.l.lc.util;

public class ConstValue {
    public final boolean isComplement;
    public final Object value;

    public ConstValue(Object value) {
        this(value, false);
    }

    public ConstValue(Object value, boolean isComplement) {
        this.isComplement = isComplement;
        this.value = value;
    }
}
