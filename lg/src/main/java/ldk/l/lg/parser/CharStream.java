package ldk.l.lg.parser;

public final class CharStream {
    public final String string;
    public int pos = 0;
    public int line = 0;
    public int col = 0;

    public CharStream(String string) {
        this.string = string;
    }

    public char peek() {
        if (this.eof()) {
            return this.string.charAt(this.pos - 1);
        } else {
            return this.string.charAt(this.pos);
        }
    }

    public char peek2() {
        if (this.eof()) {
            return this.string.charAt(this.pos - 1);
        } else if (this.pos + 1 == this.string.length()) {
            return this.string.charAt(this.pos);
        } else {
            return this.string.charAt(this.pos + 1);
        }
    }

    public char next() {
        char ch = this.string.charAt(this.pos);
        this.addPos(1);
        return ch;
    }

    public boolean eof() {
        return this.pos == this.string.length();
    }

    public CharStream addPos(int n) {
        for (int i = 0; i < n; i++) {
            char ch = this.string.charAt(this.pos++);
            if (eof()) {
                this.col++;
                break;
            } else {
                if (ch == '\n') {
                    this.line++;
                    this.col = 0;
                } else {
                    this.col++;
                }
            }
        }
        return this;
    }

    public void subPos(int n) {
        for (int i = 0; i < n; i++) {
            this.pos -= 1;
            if (this.peek() == '\n') {
                this.line--;
                this.col = this.string.split("\n")[this.line].length() - 1;
            } else {
                this.col--;
            }
        }
    }

    public boolean equalsString(String s) {
        return this.string.substring(this.pos).equals(s);
    }

    public boolean startsWith(String prefix) {
        return this.string.substring(this.pos).startsWith(prefix);
    }

    public boolean endsWith(String suffix) {
        return this.string.substring(this.pos).endsWith(suffix);
    }

    public String substring(int start) {
        return this.substring(this.pos + start, this.string.length());
    }

    public String substring(int start, int end) {
        return this.string.substring(this.pos + start, this.pos + end);
    }

    public int length() {
        return this.string.length() - this.pos;
    }
}