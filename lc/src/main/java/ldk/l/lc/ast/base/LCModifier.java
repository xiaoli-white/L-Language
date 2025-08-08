package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.List;

public final class LCModifier extends LCAstNode {
    public long flags;
    public List<String> attributes;
    public Long bitRange;

    public LCModifier(Position pos) {
        this(0, new ArrayList<>(), null, pos, false);
    }

    public LCModifier(long flags, Position pos) {
        this(flags, new ArrayList<>(), null, pos, false);
    }

    public LCModifier(long flags, List<String> attributes, Long bitRange, Position pos, boolean isErrorNode) {
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
                ", attributes=" + attributes +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}