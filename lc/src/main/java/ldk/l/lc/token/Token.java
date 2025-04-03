package ldk.l.lc.token;

import ldk.l.lc.util.Position;

public class Token {
    public TokenKind kind;
    public String text;
    public Position pos;
    public Tokens.TokenCode code;

    public Token(TokenKind kind, char text, Position pos, Tokens.TokenCode code) {
        this(kind, String.valueOf(text), pos, code);
    }

    public Token(TokenKind kind, char text, Position pos) {
        this(kind, String.valueOf(text), pos, Tokens.Others.OTHERS);
    }

    public Token(TokenKind kind, String text, Position pos) {
        this(kind, text, pos, Tokens.Others.OTHERS);
    }

    public Token(TokenKind kind, String text, Position pos, Tokens.TokenCode code) {
        this.kind = kind;
        this.text = text;
        this.pos = pos;
        this.code = code;
    }

    @Override
    public String toString() {
        return "Token" + "@" + this.pos.toString() + "\t" + this.kind + " \t'" + this.text.replace("\n", "\\n") + "'";
    }

    public static boolean isWhiteSpace(char ch) {
        return (ch == ' ' || ch == '\n' || ch == '\t');
    }

    public static boolean isOctal(char ch) {
        return ch >= '0' && ch <= '7';
    }

    public static boolean isHexadecimal(char ch) {
        return Character.isDigit(ch) || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f');
    }

    public static boolean isBinary(char ch) {
        return ch == '0' || ch == '1';
    }

    public static boolean isIdentifier(char ch) {
        return (!Token.isWhiteSpace(ch)) && ch != '"' && ch != '\''
                && ch != '(' && ch != ')' && ch != '{' && ch != '}' && ch != '[' && ch != ']'
                && ch != ':' && ch != ';' && ch != ',' && ch != '?' && ch != '@'
                && ch != '+' && ch != '-' && ch != '*' && ch != '%' && ch != '<' && ch != '>'
                && ch != '=' && ch != '!' && ch != '&' && ch != '^' && ch != '~' && ch != '|'
                && ch != '.';
    }

    public static boolean isOperator(Token token) {
        if (token.kind == TokenKind.Operator)
            return true;
        return token.code == Tokens.Keyword.Is || token.code == Tokens.Keyword.In || token.code == Tokens.Keyword.Instanceof;
    }

    public static boolean isAssignOperator(Tokens.Operator operator) {
        return operator == Tokens.Operator.Assign || operator == Tokens.Operator.PlusAssign || operator == Tokens.Operator.MinusAssign || operator == Tokens.Operator.MultiplyAssign || operator == Tokens.Operator.DivideAssign || operator == Tokens.Operator.ModulusAssign
                || operator == Tokens.Operator.LeftShiftArithmeticAssign || operator == Tokens.Operator.RightShiftArithmeticAssign || operator == Tokens.Operator.RightShiftLogicalAssign || operator == Tokens.Operator.BitAndAssign || operator == Tokens.Operator.BitOrAssign || operator == Tokens.Operator.BitXorAssign;
    }

    public static boolean isRelationOperator(Tokens.Operator operator) {
        return operator == Tokens.Operator.Equal || operator == Tokens.Operator.NotEqual || operator == Tokens.Operator.Greater || operator == Tokens.Operator.GreaterEqual || operator == Tokens.Operator.Less || operator == Tokens.Operator.LessEqual;
    }

    public static boolean isArithmeticOperator(Tokens.Operator operator) {
        return operator == Tokens.Operator.Plus || operator == Tokens.Operator.Minus || operator == Tokens.Operator.Multiply || operator == Tokens.Operator.Divide || operator == Tokens.Operator.Modulus
                || operator == Tokens.Operator.BitAnd || operator == Tokens.Operator.BitOr || operator == Tokens.Operator.BitXor
                || operator == Tokens.Operator.LeftShiftArithmetic || operator == Tokens.Operator.RightShiftArithmetic || operator == Tokens.Operator.RightShiftLogical;
    }

    public static boolean isLogicalOperator(Tokens.Operator operator) {
        return operator == Tokens.Operator.Not || operator == Tokens.Operator.And || operator == Tokens.Operator.Or;
    }

    public static boolean isBaseType(Token token) {
        return token.code == Tokens.Type.Byte || token.code == Tokens.Type.Short || token.code == Tokens.Type.Int || token.code == Tokens.Type.Long
                || token.code == Tokens.Type.UByte || token.code == Tokens.Type.UShort || token.code == Tokens.Type.UInt || token.code == Tokens.Type.ULong
                || token.code == Tokens.Type.Float || token.code == Tokens.Type.Double || token.code == Tokens.Type.Char || token.code == Tokens.Type.Boolean;
    }
}