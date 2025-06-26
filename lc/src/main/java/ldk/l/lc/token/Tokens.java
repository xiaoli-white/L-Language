package ldk.l.lc.token;

public class Tokens {
    public interface TokenCode {
        String getCode();
    }

    public enum Keyword implements TokenCode {
        Private("private"),
        Protected("protected"),
        Public("public"),
        Static("static"),
        Const("const"),
        Readonly("readonly"),
        Final("final"),
        Abstract("abstract"),
        Override("override"),
        Default("default"),
        Volatile("volatile"),

        Class("class"),
        Interface("interface"),
        Enum("enum"),
        Record("record"),

        Var("var"),
        Val("val"),
        Method("method"),
        Constructor("constructor"),
        Destructor("destructor"),

        Native("native"),

        Package("package"),
        Import("import"),
        This("this"),
        Super("super"),
        Extends("extends"),
        Implements("implements"),
        New("new"),
        Delete("delete"),
        If("if"),
        Else("else"),
        Return("return"),
        For("for"),
        Foreach("foreach"),
        While("while"),
        Do("do"),
        Loop("loop"),
        True("true"),
        False("false"),
        Clone("clone"),
        Typeof("typeof"),
        Sizeof("sizeof"),
        Instanceof("instanceof"),
        Malloc("malloc"),
        Free("free"),
        Realloc("realloc"),
        Goto("goto"),
        Break("break"),
        Continue("continue"),
        Static_cast("static_cast"),
        Dynamic_cast("dynamic_cast"),
        Reinterpret_cast("reinterpret_cast"),
        Assert("assert"),
        Operator("operator"),
        Typedef("typedef"),
        Vararg("vararg"),
        Synchronized("synchronized"),
        __Attribute__("__attribute__"),
        Throw("throw"),
        Throws("throws"),
        Try("try"),
        Catch("catch"),
        Finally("finally"),
        Switch("switch"),
        Case("case"),
        When("when"),
        In("in"),
        Include("include"),
        Lambda("lambda"),
        Sealed("sealed"),
        Non_sealed("non_sealed"),
        Permits("permits"),
        As("as"),
        __Dynamic__("__dynamic__"),
        __System_dynamic_library__("__system_dynamic_library__"),
        __System_static_library__("__system_static_library__"),
        Is("is"),
        Classof("classof"),
        Method_address_of("method_address_of"),
        __Platform__("__platform__"),
        __Static_init__("__static_init__"),
        __Init__("__init__"),
        Yield("yield"),
        Internal("internal"),
        Bit_range("bit_range"),
        With("with"),
        __Field__("__field__"),
        By("by"),
        __Type__("__type__"),
        Extern("extern"),
        Lateinit("lateinit"),
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
        Byte("byte"),
        Short("short"),
        Int("int"),
        Long("long"),
        UByte("ubyte"),
        UShort("ushort"),
        UInt("uint"),
        ULong("ulong"),
        Float("float"),
        Double("double"),
        Char("char"),
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
        Null("null"),
        Nullptr("nullptr");

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
