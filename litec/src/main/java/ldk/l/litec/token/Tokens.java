package ldk.l.litec.token;

public class Tokens {
    public interface TokenCode {
        String getCode();
    }

    public enum Keyword implements TokenCode {
        Class("class"),
        Func("func"),
        Var("var"),

        If("if"),
        Else("else"),
        Return("return"),
        For("for"),
        While("while"),
        Do("do"),
        Loop("loop"),
        Break("break"),
        Continue("continue"),

        Instanceof("instanceof"),
        As("as"),
        Is("is"),
        ;
        private final String code;

        Keyword(String name) {
            this.code = name;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum Type implements TokenCode {
        Void("void"),
        Int("int"),
        Float("float"),
        Boolean("boolean"),
        Auto("auto");

        private final String code;

        Type(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum Separator implements TokenCode {
        OpenParen("("),
        CloseParen(")"),
        OpenBracket("["),
        CloseBracket("]"),
        OpenBrace("{"),
        CloseBrace("}"),
        Colon(":"),
        Comma(","),
        SemiColon(";");

        private final String code;

        Separator(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum Operator implements TokenCode {
        QuestionMark("?"),
        Dot("."),
        At("@"),
        LeftShiftArithmetic("<<"),
        RightShiftArithmetic(">>"),
        RightShiftLogical(">>>"),
        BitNot("~"),
        BitAnd("&"),
        BitOr("|"),
        BitXor("^"),

        Not("!"),
        And("&&"),
        Or("||"),

        Assign("="),
        MultiplyAssign("*="),
        DivideAssign("/="),
        ModulusAssign("%="),
        PlusAssign("+="),
        MinusAssign("-="),
        LeftShiftArithmeticAssign("<<="),
        RightShiftArithmeticAssign(">>="),
        RightShiftLogicalAssign(">>>="),
        BitAndAssign("&="),
        BitXorAssign("^="),
        BitOrAssign("!="),

        Arrow("=>"),
        MemberAccess("->"),

        Inc("++"),
        Dec("--"),

        Plus("+"),
        Minus("-"),
        Multiply("*"),
        Divide("/"),
        Modulus("%"),

        Equal("=="),
        NotEqual("!="),
        Greater(">"),
        GreaterEqual(">="),
        Less("<"),
        LessEqual("<="),

        QuestionMarkDot("?."),
        Elvis("?:");

        private final String code;

        Operator(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum Others implements TokenCode {
        EOF("<End-Of-File>"),
        OTHERS("<Others>");

        private final String code;

        Others(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum BaseLiteral implements TokenCode {
        True("true"),
        False("false"),
        Null("null");

        private final String code;

        BaseLiteral(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }
}
