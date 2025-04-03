package ldk.l.lc.token;

import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Scanner {
    private static final HashMap<String, Tokens.TokenCode> keywordMap = new HashMap<>() {{
        Arrays.stream(Tokens.Keyword.values()).forEach(keyword -> put(keyword.getCode(), keyword));
        Arrays.stream(Tokens.Type.values()).forEach(type -> put(type.getCode(), type));
    }};
    private final CharStream stream;
    private final ErrorStream errorStream;

    public Scanner(CharStream stream, ErrorStream errorStream) {
        this.stream = stream;
        this.errorStream = errorStream;
    }

    public Token[] scan() {
        if (this.stream.eof()) {
            Position pos = new Position(this.stream.pos, this.stream.pos, this.stream.line, this.stream.line, this.stream.col, this.stream.col);
            return new Token[]{new Token(TokenKind.EOF, "", pos, Tokens.Others.EOF)};
        } else {
            ArrayList<Token> tokens = new ArrayList<>();
            char ch;
            while (!this.stream.eof()) {
                this.skipWhiteSpaces();
                Position beginPosition = this.stream.getPosition();
                ch = this.stream.peek();

                if (ch == '"') {
                    tokens.add(this.parseStringLiteral());
                } else if (ch == '\'') {
                    tokens.add(this.parseCharLiteral());
                } else if (Character.isDigit(ch)) {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    StringBuilder literal = new StringBuilder();
                    if (ch == '0') {
                        if (ch1 == 'x' || ch1 == 'X') {
                            literal = new StringBuilder("0");
                            literal.append(ch1);

                            this.stream.addPos(1);
                            char ch2 = this.stream.peek();
                            while (Token.isHexadecimal(ch2)) {
                                this.stream.addPos(1);
                                literal.append(ch2);
                                ch2 = this.stream.peek();
                            }

                            if (ch2 == 'l' || ch2 == 'L') {
                                this.stream.addPos(1);
                                literal.append(ch2);
                            }

                            Position endPosition = this.stream.getPosition();
                            Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                            tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position));
                            continue;
                        } else if (ch1 == 'b' || ch1 == 'B') {
                            literal = new StringBuilder("0");
                            literal.append(ch1);

                            this.stream.addPos(1);
                            char ch2 = this.stream.peek();
                            while (Token.isBinary(ch2)) {
                                this.stream.addPos(1);
                                literal.append(ch2);
                                ch2 = this.stream.peek();
                            }

                            if (ch2 == 'l' || ch2 == 'L') {
                                this.stream.addPos(1);
                                literal.append(ch2);
                            }

                            Position endPosition = this.stream.getPosition();
                            Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                            tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position));
                            continue;
                        } else if (Token.isOctal(ch1)) {
                            literal = new StringBuilder("0");
                            literal.append(ch1);

                            this.stream.addPos(1);
                            char ch2 = this.stream.peek();
                            while (Token.isOctal(ch2)) {
                                this.stream.addPos(1);
                                literal.append(ch2);
                                ch2 = this.stream.peek();
                            }

                            if (ch2 == 'l' || ch2 == 'L') {
                                this.stream.addPos(1);
                                literal.append(ch2);
                            }

                            Position endPosition = this.stream.getPosition();
                            Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                            tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position));
                            continue;
                        } else {
                            literal = new StringBuilder("0");
                        }
                    } else if (ch >= '1' && ch <= '9') {
                        literal.append(ch);
                        while (Character.isDigit(ch1)) {
                            ch = this.stream.next();
                            literal.append(ch);
                            ch1 = this.stream.peek();
                        }
                    }

                    if (ch1 == 'l' || ch1 == 'L') {
                        this.stream.addPos(1);
                        literal.append(ch1);

                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                        tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position));
                    } else if (ch1 == 'f' || ch1 == 'F') {
                        this.stream.addPos(1);
                        literal.append(ch1);

                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                        tokens.add(new Token(TokenKind.DecimalLiteral, literal.toString(), position));
                    } else if (ch1 == '.') {
                        literal.append('.');
                        this.stream.addPos(1);
                        ch1 = this.stream.peek();
                        while (Character.isDigit(ch1)) {
                            ch = this.stream.next();
                            literal.append(ch);
                            ch1 = this.stream.peek();
                        }

                        if (ch1 == 'f' || ch1 == 'F') {
                            this.stream.addPos(1);
                            literal.append(ch1);
                        }

                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                        tokens.add(new Token(TokenKind.DecimalLiteral, literal.toString(), position));
                    } else {
                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                        tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position));
                    }
                } else if (ch == '.') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (Character.isDigit(ch1)) {
                        StringBuilder literal = new StringBuilder(".");
                        while (Character.isDigit(ch1)) {
                            ch = this.stream.next();
                            literal.append(ch);
                            ch1 = this.stream.peek();
                        }

                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
                        tokens.add(new Token(TokenKind.DecimalLiteral, literal.toString(), position));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, ".", beginPosition, Tokens.Operator.Dot));
                    }
                } else if (ch == '(') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.OpenParen));
                } else if (ch == ')') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.CloseParen));
                } else if (ch == '{') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.OpenBrace));
                } else if (ch == '}') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.CloseBrace));
                } else if (ch == '[') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.OpenBracket));
                } else if (ch == ']') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.CloseBracket));
                } else if (ch == ':') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.Colon));
                } else if (ch == ';') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.SemiColon));
                } else if (ch == ',') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.Comma));
                } else if (ch == '?') {
                    this.stream.addPos(1);
                    char ch2 = this.stream.peek();
                    if (ch2 == '.') {
                        this.stream.addPos(1);
                        tokens.add(new Token(TokenKind.Operator, ch, beginPosition, Tokens.Operator.QuestionMarkDot));
                    } else if (ch2 == ':') {
                        this.stream.addPos(1);
                        tokens.add(new Token(TokenKind.Operator, ch, beginPosition, Tokens.Operator.Elvis));
                    } else {
                        tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Operator.QuestionMark));
                    }
                } else if (ch == '@') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Operator.At));
                } else if (ch == '/') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '*') {
                        this.skipMultipleLineComments();
                    } else if (ch1 == '/') {
                        this.skipSingleLineComment();
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "/=", beginPosition, Tokens.Operator.DivideAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "/", beginPosition, Tokens.Operator.Divide));
                    }
                } else if (ch == '+') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '+') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "++", beginPosition, Tokens.Operator.Inc));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "+=", beginPosition, Tokens.Operator.PlusAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "+", beginPosition, Tokens.Operator.Plus));
                    }
                } else if (ch == '-') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '-') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "--", beginPosition, Tokens.Operator.Dec));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "-=", beginPosition, Tokens.Operator.MinusAssign));
                    } else if (ch1 == '>') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "->", beginPosition, Tokens.Operator.MemberAccess));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "-", beginPosition, Tokens.Operator.Minus));
                    }
                } else if (ch == '*') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "*=", beginPosition, Tokens.Operator.MultiplyAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "*", beginPosition, Tokens.Operator.Multiply));
                    }
                } else if (ch == '%') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "%=", beginPosition, Tokens.Operator.ModulusAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "%", beginPosition, Tokens.Operator.Modulus));
                    }
                } else if (ch == '>') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, ">=", beginPosition, Tokens.Operator.GreaterEqual));
                    } else if (ch1 == '>') {
                        this.stream.addPos(1);
                        ch1 = this.stream.peek();
                        if (ch1 == '>') {
                            this.stream.addPos(1);
                            ch1 = this.stream.peek();
                            if (ch1 == '=') {
                                this.stream.addPos(1);
                                beginPosition.endPos = this.stream.pos;
                                beginPosition.endLine = this.stream.line;
                                beginPosition.endCol = this.stream.col;
                                tokens.add(new Token(TokenKind.Operator, ">>>=", beginPosition, Tokens.Operator.RightShiftLogicalAssign));
                            } else {
                                beginPosition.endPos = this.stream.pos;
                                beginPosition.endLine = this.stream.line;
                                beginPosition.endCol = this.stream.col;
                                tokens.add(new Token(TokenKind.Operator, ">>>", beginPosition, Tokens.Operator.RightShiftLogical));
                            }
                        } else if (ch1 == '=') {
                            this.stream.addPos(1);
                            beginPosition.endPos = this.stream.pos;
                            beginPosition.endLine = this.stream.line;
                            beginPosition.endCol = this.stream.col;
                            tokens.add(new Token(TokenKind.Operator, ">>=", beginPosition, Tokens.Operator.LeftShiftArithmeticAssign));
                        } else {
                            beginPosition.endPos = this.stream.pos;
                            beginPosition.endLine = this.stream.line;
                            beginPosition.endCol = this.stream.col;
                            tokens.add(new Token(TokenKind.Operator, ">>", beginPosition, Tokens.Operator.RightShiftArithmetic));
                        }
                    } else {
                        tokens.add(new Token(TokenKind.Operator, ">", beginPosition, Tokens.Operator.Greater));
                    }
                } else if (ch == '<') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "<=", beginPosition, Tokens.Operator.LessEqual));
                    } else if (ch1 == '<') {
                        this.stream.addPos(1);
                        ch1 = this.stream.peek();
                        if (ch1 == '=') {
                            this.stream.addPos(1);
                            beginPosition.endPos = this.stream.pos;
                            beginPosition.endLine = this.stream.line;
                            beginPosition.endCol = this.stream.col;
                            tokens.add(new Token(TokenKind.Operator, "<<=", beginPosition, Tokens.Operator.LeftShiftArithmeticAssign));
                        } else {
                            beginPosition.endPos = this.stream.pos;
                            beginPosition.endLine = this.stream.line;
                            beginPosition.endCol = this.stream.col;
                            tokens.add(new Token(TokenKind.Operator, "<<", beginPosition, Tokens.Operator.LeftShiftArithmetic));
                        }
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "<", beginPosition, Tokens.Operator.Less));
                    }
                } else if (ch == '=') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "==", beginPosition, Tokens.Operator.Equal));
                    } else if (ch1 == '>') {
                        this.stream.addPos(1);

                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;

                        tokens.add(new Token(TokenKind.Operator, "=>", beginPosition, Tokens.Operator.Arrow));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "=", beginPosition, Tokens.Operator.Assign));
                    }
                } else if (ch == '!') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "!=", beginPosition, Tokens.Operator.NotEqual));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "!", beginPosition, Tokens.Operator.Not));
                    }
                } else if (ch == '|') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '|') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "||", beginPosition, Tokens.Operator.Or));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "|=", beginPosition, Tokens.Operator.BitOrAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "|", beginPosition, Tokens.Operator.BitOr));
                    }
                } else if (ch == '&') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '&') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "&&", beginPosition, Tokens.Operator.And));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "&=", beginPosition, Tokens.Operator.BitAndAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "&", beginPosition, Tokens.Operator.BitAnd));
                    }
                } else if (ch == '^') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        beginPosition.endPos = this.stream.pos;
                        beginPosition.endLine = this.stream.line;
                        beginPosition.endCol = this.stream.col;
                        tokens.add(new Token(TokenKind.Operator, "^=", beginPosition, Tokens.Operator.BitXorAssign));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "^", beginPosition, Tokens.Operator.BitXor));
                    }
                } else if (ch == '~') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Operator, "~", beginPosition, Tokens.Operator.BitNot));
                } else {
                    tokens.add(this.parseIdentifier());
                }
            }
            Position pos = new Position(this.stream.pos, this.stream.pos, this.stream.line, this.stream.line, this.stream.col, this.stream.col);
            tokens.add(new Token(TokenKind.EOF, "", pos, Tokens.Others.EOF));
            return tokens.toArray(new Token[0]);
        }
    }

    private Token parseIdentifier() {
        Position pos = this.stream.getPosition();
        Token token = new Token(TokenKind.Identifier, "", pos);

        StringBuilder text = new StringBuilder(String.valueOf(this.stream.next()));

        while (!this.stream.eof() && Token.isIdentifier(this.stream.peek())) {
            text.append(this.stream.next());
        }

        token.text = text.toString();

        pos.endPos = this.stream.pos - 1;
        pos.endLine = this.stream.line;
        pos.endCol = this.stream.col - 1;

        if (token.text.equals("true")) {
            token.kind = TokenKind.BooleanLiteral;
            token.code = Tokens.BaseLiteral.True;
        } else if (token.text.equals("false")) {
            token.kind = TokenKind.BooleanLiteral;
            token.code = Tokens.BaseLiteral.False;
        } else if (token.text.equals("null")) {
            token.kind = TokenKind.NullLiteral;
            token.code = Tokens.BaseLiteral.Null;
        } else if (token.text.equals("nullptr")) {
            token.kind = TokenKind.NullptrLiteral;
            token.code = Tokens.BaseLiteral.Nullptr;
        } else if (this.keywordMap.containsKey(token.text)) {
            token.kind = TokenKind.Keyword;
            token.code = this.keywordMap.get(token.text);
        }

        return token;
    }

    private Token parseStringLiteral() {
        Position beginPosition = this.stream.getPosition();

        StringBuilder literal = new StringBuilder();

        this.stream.addPos(1);

        boolean isMultiline;
        if (this.stream.peek() == '"' && this.stream.peek2() == '"') {
            this.stream.addPos(2);
            isMultiline = true;
        } else {
            isMultiline = false;
        }

        while (!(this.stream.eof()) && this.stream.peek() != '"' && (isMultiline || this.stream.peek() != '\n')) {
            literal.append(parseAChar());
        }

        if (this.stream.peek() == '"') {
            this.stream.addPos(1);
            if (isMultiline) {
                if (this.stream.peek() == '"' && this.stream.peek2() == '"') {
                    this.stream.addPos(2);
                } else {
                    this.errorStream.printError(true, this.stream.getPosition(), 2);
                }
            }
        } else {
            this.errorStream.printError(true, this.stream.getPosition(), 2);
        }

        Position endPosition = this.stream.getPosition();
        Position position = new Position(beginPosition.beginPos, endPosition.endPos, beginPosition.beginLine, endPosition.endLine, beginPosition.beginCol, endPosition.endCol);
        return new Token(TokenKind.StringLiteral, literal.toString(), position);
    }

    private Token parseCharLiteral() {
        Position pos = this.stream.getPosition();
        Token token = new Token(TokenKind.CharLiteral, "", pos);

        this.stream.addPos(1);

        token.text = String.valueOf(parseAChar());

        if (this.stream.peek() != '\'' || this.stream.peek() == '\n') {
            pos.endPos = this.stream.pos - 1;
            pos.endLine = this.stream.line;
            pos.endCol = this.stream.col - 1;
            errorStream.printError(true, pos, 3);
        } else {
            pos.endPos = this.stream.pos;
            pos.endLine = this.stream.line;
            pos.endCol = this.stream.col;
            this.stream.addPos(1);
        }

        return token;
    }

    private char parseAChar() {
        Position pos = this.stream.getPosition();

        char ch = this.stream.next();
        if (ch == '\\') {
            char ch2 = this.stream.next();
            if (ch2 == 'b') {
                return '\b';
            } else if (ch2 == 't') {
                return '\t';
            } else if (ch2 == 'n') {
                return '\n';
            } else if (ch2 == 'f') {
                return '\f';
            } else if (ch2 == 'r') {
                return '\r';
            } else if (ch2 == '"') {
                return '\"';
            } else if (ch2 == '\'') {
                return '\'';
            } else if (ch2 == '\\') {
                return '\\';
            } else if (Token.isOctal(ch2)) {
                StringBuilder builder = new StringBuilder(String.valueOf(ch2));
                char ch3 = this.stream.peek();
                if (Token.isOctal(ch3)) {
                    this.stream.addPos(1);
                    builder.append(ch3);
                    char ch4 = this.stream.peek();
                    if (Token.isOctal(ch4)) {
                        this.stream.addPos(1);
                        builder.append(ch4);
                    }
                }
                return Character.toChars(Integer.parseInt(builder.toString(), 8))[0];
            } else if (ch2 == 'u') {
                if (this.stream.length() >= 4) {
                    int codePoint = Integer.parseInt(this.stream.substring(this.stream.pos, this.stream.pos + 4), 16);
                    return Character.toChars(codePoint)[0];
                } else {
                    // TODO dump error
                    return '\0';
                }
            } else {
                pos.endPos = this.stream.pos - 1;
                pos.endLine = this.stream.line;
                pos.endCol = this.stream.col - 1;

                this.errorStream.printError(true, pos, 4);

                return ch2;
            }
        } else {
            return ch;
        }
    }

    private void skipWhiteSpaces() {
        while (!(this.stream.eof()) && Token.isWhiteSpace(this.stream.peek())) {
            this.stream.addPos(1);
        }
    }

    private void skipSingleLineComment() {
        this.stream.addPos(1);

        while (!(this.stream.eof()) && this.stream.peek() != '\n') {
            this.stream.addPos(1);
        }
    }

    private void skipMultipleLineComments() {
        int beginPos = this.stream.pos - 1;
        int beginCol = this.stream.col - 1;
        int thisLine = this.stream.line;
        this.stream.addPos(1);

        if (!this.stream.eof()) {
            char ch1 = this.stream.next();
            while (!this.stream.eof()) {
                char ch2 = this.stream.next();
                if (ch1 == '*' && ch2 == '/') {
                    return;
                }
                ch1 = ch2;
            }
        }

        this.errorStream.printError(true, new Position(beginPos, this.stream.pos, thisLine, thisLine, beginCol, -1), 5);
    }
}