package ldk.l.litec.token;

import ldk.l.litec.token.CharStream;
import ldk.l.litec.token.Token;
import ldk.l.litec.token.TokenKind;
import ldk.l.litec.token.Tokens;
import ldk.l.litec.util.Position;
import ldk.l.litec.util.error.ErrorStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class Scanner {
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
            List<Token> tokens = new ArrayList<>();
            char ch;
            while (!this.stream.eof()) {
                boolean hasNewLine = this.skipWhiteSpaces();
                Position beginPosition = this.stream.getPosition();
                ch = this.stream.peek();

                if (ch == '"') {
                    tokens.add(this.parseStringLiteral().setNewLine(hasNewLine));
                } else if (ch == '\'') {
                    tokens.add(this.parseCharLiteral().setNewLine(hasNewLine));
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
                            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                            tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position, hasNewLine));
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
                            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                            tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position, hasNewLine));
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
                            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                            tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position, hasNewLine));
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
                        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                        tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position, hasNewLine));
                    } else if (ch1 == 'f' || ch1 == 'F') {
                        this.stream.addPos(1);
                        literal.append(ch1);

                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                        tokens.add(new Token(TokenKind.DecimalLiteral, literal.toString(), position, hasNewLine));
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
                        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                        tokens.add(new Token(TokenKind.DecimalLiteral, literal.toString(), position, hasNewLine));
                    } else {
                        Position endPosition = this.stream.getPosition();
                        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                        tokens.add(new Token(TokenKind.IntegerLiteral, literal.toString(), position, hasNewLine));
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
                        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                        tokens.add(new Token(TokenKind.DecimalLiteral, literal.toString(), position, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, ".", beginPosition, Tokens.Operator.Dot, hasNewLine));
                    }
                } else if (ch == '(') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.OpenParen, hasNewLine));
                } else if (ch == ')') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.CloseParen, hasNewLine));
                } else if (ch == '{') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.OpenBrace, hasNewLine));
                } else if (ch == '}') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.CloseBrace, hasNewLine));
                } else if (ch == '[') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.OpenBracket, hasNewLine));
                } else if (ch == ']') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.CloseBracket, hasNewLine));
                } else if (ch == ':') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.Colon, hasNewLine));
                } else if (ch == ';') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.SemiColon, hasNewLine));
                } else if (ch == ',') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Separator.Comma, hasNewLine));
                } else if (ch == '?') {
                    this.stream.addPos(1);
                    char ch2 = this.stream.peek();
                    if (ch2 == '.') {
                        this.stream.addPos(1);
                        tokens.add(new Token(TokenKind.Operator, ch, beginPosition, Tokens.Operator.QuestionMarkDot, hasNewLine));
                    } else if (ch2 == ':') {
                        this.stream.addPos(1);
                        tokens.add(new Token(TokenKind.Operator, ch, beginPosition, Tokens.Operator.Elvis, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Operator.QuestionMark, hasNewLine));
                    }
                } else if (ch == '@') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Separator, ch, beginPosition, Tokens.Operator.At, hasNewLine));
                } else if (ch == '/') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '*') {
                        this.skipMultipleLineComments();
                    } else if (ch1 == '/') {
                        this.skipSingleLineComment();
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "/=", position, Tokens.Operator.DivideAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "/", beginPosition, Tokens.Operator.Divide, hasNewLine));
                    }
                } else if (ch == '+') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '+') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "++", position, Tokens.Operator.Inc, hasNewLine));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "+=", position, Tokens.Operator.PlusAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "+", beginPosition, Tokens.Operator.Plus, hasNewLine));
                    }
                } else if (ch == '-') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '-') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "--", position, Tokens.Operator.Dec, hasNewLine));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "-=", position, Tokens.Operator.MinusAssign, hasNewLine));
                    } else if (ch1 == '>') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "->", position, Tokens.Operator.MemberAccess, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "-", beginPosition, Tokens.Operator.Minus, hasNewLine));
                    }
                } else if (ch == '*') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "*=", position, Tokens.Operator.MultiplyAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "*", beginPosition, Tokens.Operator.Multiply, hasNewLine));
                    }
                } else if (ch == '%') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "%=", position, Tokens.Operator.ModulusAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "%", beginPosition, Tokens.Operator.Modulus, hasNewLine));
                    }
                } else if (ch == '>') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, ">=", position, Tokens.Operator.GreaterEqual, hasNewLine));
                    } else if (ch1 == '>') {
                        this.stream.addPos(1);
                        ch1 = this.stream.peek();
                        if (ch1 == '>') {
                            this.stream.addPos(1);
                            ch1 = this.stream.peek();
                            if (ch1 == '=') {
                                this.stream.addPos(1);
                                Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                                tokens.add(new Token(TokenKind.Operator, ">>>=", position, Tokens.Operator.RightShiftLogicalAssign, hasNewLine));
                            } else {
                                Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                                tokens.add(new Token(TokenKind.Operator, ">>>", position, Tokens.Operator.RightShiftLogical, hasNewLine));
                            }
                        } else if (ch1 == '=') {
                            this.stream.addPos(1);
                            Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                            tokens.add(new Token(TokenKind.Operator, ">>=", position, Tokens.Operator.LeftShiftArithmeticAssign, hasNewLine));
                        } else {
                            Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                            tokens.add(new Token(TokenKind.Operator, ">>", position, Tokens.Operator.RightShiftArithmetic, hasNewLine));
                        }
                    } else {
                        tokens.add(new Token(TokenKind.Operator, ">", beginPosition, Tokens.Operator.Greater, hasNewLine));
                    }
                } else if (ch == '<') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "<=", position, Tokens.Operator.LessEqual, hasNewLine));
                    } else if (ch1 == '<') {
                        this.stream.addPos(1);
                        ch1 = this.stream.peek();
                        if (ch1 == '=') {
                            this.stream.addPos(1);
                            Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                            tokens.add(new Token(TokenKind.Operator, "<<=", position, Tokens.Operator.LeftShiftArithmeticAssign, hasNewLine));
                        } else {
                            Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                            tokens.add(new Token(TokenKind.Operator, "<<", position, Tokens.Operator.LeftShiftArithmetic, hasNewLine));
                        }
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "<", beginPosition, Tokens.Operator.Less, hasNewLine));
                    }
                } else if (ch == '=') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "==", position, Tokens.Operator.Equal, hasNewLine));
                    } else if (ch1 == '>') {
                        this.stream.addPos(1);

                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);

                        tokens.add(new Token(TokenKind.Operator, "=>", position, Tokens.Operator.Arrow, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "=", beginPosition, Tokens.Operator.Assign, hasNewLine));
                    }
                } else if (ch == '!') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "!=", position, Tokens.Operator.NotEqual, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "!", beginPosition, Tokens.Operator.Not, hasNewLine));
                    }
                } else if (ch == '|') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '|') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "||", position, Tokens.Operator.Or, hasNewLine));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "|=", position, Tokens.Operator.BitOrAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "|", beginPosition, Tokens.Operator.BitOr, hasNewLine));
                    }
                } else if (ch == '&') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '&') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "&&", position, Tokens.Operator.And, hasNewLine));
                    } else if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "&=", position, Tokens.Operator.BitAndAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "&", beginPosition, Tokens.Operator.BitAnd, hasNewLine));
                    }
                } else if (ch == '^') {
                    this.stream.addPos(1);
                    char ch1 = this.stream.peek();
                    if (ch1 == '=') {
                        this.stream.addPos(1);
                        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                        tokens.add(new Token(TokenKind.Operator, "^=", position, Tokens.Operator.BitXorAssign, hasNewLine));
                    } else {
                        tokens.add(new Token(TokenKind.Operator, "^", beginPosition, Tokens.Operator.BitXor, hasNewLine));
                    }
                } else if (ch == '~') {
                    this.stream.addPos(1);
                    tokens.add(new Token(TokenKind.Operator, "~", beginPosition, Tokens.Operator.BitNot, hasNewLine));
                } else if (ch == '`') {
                    this.stream.addPos(1);
                    StringBuilder builder = new StringBuilder(String.valueOf(this.stream.next()));

                    while (!this.stream.eof() && this.stream.peek() != '`') {
                        builder.append(this.stream.next());
                    }

                    Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                    tokens.add(new Token(TokenKind.Identifier, builder.toString(), position));
                } else {
                    tokens.add(this.parseIdentifier().setNewLine(hasNewLine));
                }
            }
            Position pos = new Position(this.stream.pos, this.stream.pos, this.stream.line, this.stream.line, this.stream.col, this.stream.col);
            tokens.add(new Token(TokenKind.EOF, "", pos, Tokens.Others.EOF, true));
            return tokens.toArray(new Token[0]);
        }
    }

    private Token parseIdentifier() {
        Position beginPosition = this.stream.getPosition();
        StringBuilder builder = new StringBuilder(String.valueOf(this.stream.next()));

        while (!this.stream.eof() && Token.isIdentifier(this.stream.peek())) {
            builder.append(this.stream.next());
        }

        String text = builder.toString();

        Position position = new Position(beginPosition.beginPos(), this.stream.pos - 1, beginPosition.beginLine(), this.stream.col == 0 ? this.stream.line - 1 : this.stream.line, beginPosition.beginCol(), this.stream.col - 1);

        if (text.equals("true")) {
            return new Token(TokenKind.BooleanLiteral, text, position, Tokens.BaseLiteral.True);
        } else if (text.equals("false")) {
            return new Token(TokenKind.BooleanLiteral, text, position, Tokens.BaseLiteral.False);
        } else if (text.equals("null")) {
            return new Token(TokenKind.NullLiteral, text, position, Tokens.BaseLiteral.Null);
        } else if (keywordMap.containsKey(text)) {
            return new Token(TokenKind.Keyword, text, position, keywordMap.get(text));
        } else {
            return new Token(TokenKind.Identifier, text, position);
        }
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
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new Token(TokenKind.StringLiteral, literal.toString(), position);
    }

    private Token parseCharLiteral() {
        Position beginPosition = this.stream.getPosition();
        this.stream.addPos(1);

        char c = parseAChar();

        Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
        if (this.stream.peek() != '\'') {
            errorStream.printError(true, beginPosition, 3);
        } else {
            this.stream.addPos(1);
        }

        return new Token(TokenKind.CharLiteral, String.valueOf(c), position);
    }

    private char parseAChar() {
        Position beginPosition = this.stream.getPosition();

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
                Position position = new Position(beginPosition.beginPos(), this.stream.pos, beginPosition.beginLine(), this.stream.line, beginPosition.beginCol(), this.stream.col);
                this.errorStream.printError(true, position, 4);

                return ch2;
            }
        } else {
            return ch;
        }
    }

    private boolean skipWhiteSpaces() {
        boolean hasNewLine = false;
        while (!this.stream.eof()) {
            char c = this.stream.peek();
            if (Token.isWhiteSpace(c)) {
                this.stream.addPos(1);
                if (c == '\n') hasNewLine = true;
            } else {
                break;
            }
        }
        return hasNewLine;
    }

    private void skipSingleLineComment() {
        do {
            this.stream.addPos(1);
        } while (!(this.stream.eof()) && this.stream.peek() != '\n');
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