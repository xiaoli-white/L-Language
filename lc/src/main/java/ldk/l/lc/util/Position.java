package ldk.l.lc.util;

import java.util.Objects;

public class Position implements Cloneable {
    public int beginPos;
    public int endPos;
    public int beginLine;
    public int endLine;
    public int beginCol;
    public int endCol;

    public Position(int beginPos, int endPos, int beginLine, int endLine, int beginCol, int endCol) {
        this.beginPos = beginPos;
        this.endPos = endPos;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.beginCol = beginCol;
        this.endCol = endCol;
    }

    @Override
    public String toString() {
        return "(bp:" + this.beginPos + ", ep:" + this.endPos + ", bl:" + this.beginLine + ", el:" + this.endLine + ", bc:" + this.beginCol + ", ec:" + this.endCol + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position that)) return false;
        return beginPos == that.beginPos && endPos == that.endPos && beginLine == that.beginLine && endLine == that.endLine && beginCol == that.beginCol && endCol == that.endCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginPos, endPos, beginLine, endLine, beginCol, endCol);
    }

    @Override
    public Position clone() throws CloneNotSupportedException {
        return (Position) super.clone();
    }

    public static Position origin = new Position(-1, -1, -1, -1, -1, -1);
}