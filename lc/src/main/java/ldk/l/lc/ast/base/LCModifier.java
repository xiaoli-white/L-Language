package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCModifier extends LCAstNode {
    public long flags;
    public String[] attributes;
    public Long bitRange;

    public LCModifier(Position pos) {
        this(0, new String[0], null, pos, false);
    }

    public LCModifier(long flags, Position pos) {
        this(flags, new String[0], null, pos, false);
    }

    public LCModifier(long flags, String[] attributes, Long bitRange, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.flags = flags;
        this.attributes = attributes;
        this.bitRange = bitRange;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitModifier(this, additional);
    }

    @Override
    public String toString() {
        return "LCModifier{" +
                "flags=" + flags +
                ", attributes=" + Arrays.toString(attributes) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCModifier clone() throws CloneNotSupportedException {
        return new LCModifier(this.flags, this.attributes, this.bitRange, this.position.clone(), this.isErrorNode);
    }
}