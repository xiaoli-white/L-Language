package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCNative extends LCStatementWithScope {
    public LCResourceForNative[] resources;
    public LCNativeSection[] sections;

    public LCNative(LCNativeSection[] sections, Position pos) {
        this(sections, pos, false);
    }

    public LCNative(LCNativeSection[] sections, Position pos, boolean isErrorNode) {
        this(new LCResourceForNative[0], sections, pos, isErrorNode);
    }

    public LCNative(LCNative.LCResourceForNative[] resources, LCNativeSection[] sections, Position pos) {
        this(resources, sections, pos, false);
    }

    public LCNative(LCNative.LCResourceForNative[] resources, LCNativeSection[] sections, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.resources = resources;
        for (LCResourceForNative resource : resources) resource.parentNode = this;

        this.sections = sections;
        for (LCNativeSection lcNativeSection : this.sections) lcNativeSection.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNative(this, additional);
    }

    @Override
    public String toString() {
        return "LCNative{" +
                "resources=" + Arrays.toString(resources) +
                ", sections=" + Arrays.toString(sections) +
                ", scope=" + scope +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCNative clone() throws CloneNotSupportedException {
        return new LCNative(Arrays.copyOf(resources, resources.length), Arrays.copyOf(sections, sections.length), position.clone(), isErrorNode);
    }

    public static class LCResourceForNative extends LCAstNode {
        public LCExpression resource;
        public String name;

        public LCResourceForNative(LCExpression resource, String name, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.resource = resource;
            this.resource.parentNode = this;
            this.name = name;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitResourceForNative(this, additional);
        }

        @Override
        public String toString() {
            return "LCResourceForNative{" +
                    "resource=" + resource +
                    ", name='" + name + '\'' +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCResourceForNative clone() throws CloneNotSupportedException {
            return new LCResourceForNative(resource.clone(), name, position.clone(), isErrorNode);
        }
    }
}
