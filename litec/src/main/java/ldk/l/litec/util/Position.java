package ldk.l.litec.util;

import java.util.Objects;

public record Position(int beginPos, int endPos, int beginLine, int endLine, int beginCol,
                       int endCol) implements Cloneable {

    @Override
    public String toString() {
        return "(bp:" + this.beginPos + ", ep:" + this.endPos + ", bl:" + this.beginLine + ", el:" + this.endLine + ", bc:" + this.beginCol + ", ec:" + this.endCol + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position(int bPos, int ePos, int bLine, int eLine, int bCol, int eCol))) return false;
        return beginPos == bPos && endPos == ePos && beginLine == bLine && endLine == eLine && beginCol == bCol && endCol == eCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginPos, endPos, beginLine, endLine, beginCol, endCol);
    }

    @Override
    public Position clone() {
        return new Position(beginPos, endPos, beginLine, endLine, beginCol, endCol);
    }

    public static Position origin = new Position(-1, -1, -1, -1, -1, -1);
}