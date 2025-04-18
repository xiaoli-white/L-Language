package ldk.l.lc.parser;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.literal.*;
import ldk.l.lc.ast.expression.type.*;
import ldk.l.lc.ast.statement.LCTry;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.expression.LCMethodCall;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.*;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.token.Token;
import ldk.l.lc.token.TokenKind;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.util.option.Options;
import ldk.l.lc.util.symbol.MethodKind;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class Parser {
    private final LCAst ast;
    private final Options options;
    private final Token[] tokens;
    private int tokenIndex = 0;
    private final ErrorStream errorStream;
    private final Stack<List<LCClassDeclaration>> anonymousClassDeclarationsStack = new Stack<>();

    public Parser(LCAst ast, Options options, Token[] tokens, ErrorStream errorStream) {
        this.ast = ast;
        this.options = options;
        this.tokens = tokens;
        this.errorStream = errorStream;
    }

    public void parseAST(File file) {
        LCBlock body = this.parseBaseStatements(file.getName());
        LCSourceCodeFile sourceCodeFile = new LCSourceCodeFile(file.getPath(), body, body.position, body.isErrorNode);
        this.ast.addSourceFile(sourceCodeFile);

        if (this.ast.mainObjectDeclaration == null) {
            String filename = file.getName();
            this.ast.mainObjectDeclaration = sourceCodeFile.getObjectDeclarationByName(filename.substring(0, filename.length() - 2));
        }
    }

    private LCBlock parseBaseStatements(String fileNameWithoutExtension) {
        Position beginPosition = this.getPos();

        List<LCStatement> statements = new ArrayList<>();
        boolean isErrorNode = false;

        Token t = peek();
        if (t.code() == Tokens.Keyword.Package) {
            this.tokenIndex++;
            StringBuilder packageName = new StringBuilder();
            while (true) {
                Token t1 = peek();
                if (t1.kind() == TokenKind.Identifier) {
                    packageName.append(t1.text());
                    this.tokenIndex++;
                    if (peek().code() == Tokens.Operator.Dot) {
                        packageName.append(".");
                        this.tokenIndex++;
                    } else {
                        break;
                    }
                } else {
                    isErrorNode = true;
                    this.errorStream.printError(true, Position.origin, 0);
                    break;
                }
            }

            Position endPosition = this.getPos();
            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
            statements.add(new LCPackage(packageName.toString(), position, false));

            t = peek();
        }
        while (t.code() == Tokens.Keyword.Import || t.code() == Tokens.Keyword.Typedef || t.code() == Tokens.Keyword.Include || t.code() == Tokens.Separator.SemiColon) {
            switch (t.code()) {
                case Tokens.Keyword.Import -> statements.add(this.parseImport());
                case Tokens.Keyword.Typedef -> {
                    Position beginPos = this.getPos();
                    boolean t_isErrorNode = false;

                    this.tokenIndex++;

                    LCTypeExpression lcTypeExpression = this.parseTypeExpression();

                    Token name = this.next();
                    if (name.kind() != TokenKind.Identifier) {
                        t_isErrorNode = true;
                        // TODO dump error
                    }

                    Position endPos = this.getLastPos();
                    Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                    statements.add(new LCTypedef(lcTypeExpression, name.text(), position, t_isErrorNode));
                }
                case Tokens.Keyword.Include -> statements.add(this.parseInclude());
                default -> {
                    this.tokenIndex++;
                    statements.add(new LCEmptyStatement(t.position()));
                }
            }
            t = this.peek();
        }
        while (t.kind() != TokenKind.EOF) {
            LCAnnotationDeclaration.LCAnnotation[] annotations = this.parseAnnotations();
            LCModifier modifier = this.parseModifier();

            t = this.peek();
            LCObjectDeclaration objectDeclaration;
            switch (t.code()) {
                case Tokens.Keyword.Class -> objectDeclaration = this.parseClassDeclaration();
                case Tokens.Keyword.Interface -> {
                    modifier.flags |= LCFlags.INTERFACE;
                    objectDeclaration = this.parseInterfaceDeclaration();
                }
                case Tokens.Keyword.Enum -> {
                    modifier.flags |= LCFlags.ENUM;
                    objectDeclaration = this.parseEnumDeclaration();
                }
                case Tokens.Keyword.Record -> {
                    modifier.flags |= LCFlags.RECORD;
                    objectDeclaration = this.parseRecordDeclaration();
                }
                case Tokens.Keyword.Struct -> objectDeclaration = this.parseStructDeclaration();
                case null, default -> {
                    if (t.code() == Tokens.Operator.At && this.peek2().code() == Tokens.Keyword.Interface) {
                        modifier.flags |= LCFlags.ANNOTATION;
                        objectDeclaration = this.parseAnnotationDeclaration();
                    } else {
                        objectDeclaration = null;
                        // TODO dump error
                        this.skip();
                    }
                }
            }
            if (objectDeclaration != null) {
                objectDeclaration.setModifier(modifier);
                objectDeclaration.setAnnotations(annotations);

                if (LCFlags.hasPublic(objectDeclaration.modifier.flags) && !objectDeclaration.name.equals(fileNameWithoutExtension)) {
                    // TODO dump error
                }

                statements.add(objectDeclaration);
            }

            t = this.peek();
        }

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCBlock(statements, position, isErrorNode);
    }

    private LCImport parseImport() {
        Position beginPosition = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        LCImport.LCImportKind kind;
        Token t = this.peek();
        switch (t.code()) {
            case Tokens.Keyword.__Dynamic__ -> {
                this.tokenIndex++;
                kind = LCImport.LCImportKind.Dynamic;
            }
            case Tokens.Keyword.Static -> {
                this.tokenIndex++;
                kind = LCImport.LCImportKind.Static;
            }
            case Tokens.Keyword.Native -> {
                this.tokenIndex++;
                kind = LCImport.LCImportKind.Native;
            }
            case Tokens.Keyword.__System_dynamic_library__ -> {
                this.tokenIndex++;
                kind = LCImport.LCImportKind.SystemDynamicLibrary;
            }
            case Tokens.Keyword.__System_static_library__ -> {
                this.tokenIndex++;
                kind = LCImport.LCImportKind.SystemStaticLibrary;
            }
            case null, default -> kind = LCImport.LCImportKind.Normal;
        }

        String name;

        t = this.peek();
        if (kind == LCImport.LCImportKind.Normal) {
            StringBuilder stringBuilder = new StringBuilder();
            while (t.kind() == TokenKind.Identifier) {
                stringBuilder.append(t.text());
                this.tokenIndex++;

                if (this.peek().code() == Tokens.Operator.Dot) {
                    stringBuilder.append(".");
                    this.tokenIndex++;
                }

                t = this.peek();
            }
            name = stringBuilder.toString();
        } else {
            this.tokenIndex++;
            name = t.text();
            if (t.kind() != TokenKind.StringLiteral) {
                isErrorNode = true;
                // TODO dump error
            }
        }


        String alias;
        if (this.peek().code() == Tokens.Keyword.As) {
            this.tokenIndex++;
            Token tAlias = this.next();
            alias = tAlias.text();
            if (tAlias.kind() != TokenKind.Identifier) {
                isErrorNode = true;
                // TODO dump error
            }
        } else {
            alias = null;
        }

        Position endPosition = this.getLastPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCImport(kind, name, alias, position, isErrorNode);
    }

    private LCInclude parseInclude() {
        Position beginPosition = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        Token filepath = this.peek();
        if (filepath.kind() == TokenKind.StringLiteral) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        LCIntegerLiteral beginLine;
        LCIntegerLiteral endLine;
        if (this.peek().code() == Tokens.Separator.OpenBracket) {
            this.tokenIndex++;
            Token tBeginLine = this.peek();
            if (tBeginLine.kind() == TokenKind.IntegerLiteral) {
                this.tokenIndex++;
                beginLine = this.parseIntegerLiteral(tBeginLine);
            } else {
                beginLine = null;
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }

            if (this.peek().code() == Tokens.Separator.Colon) {
                this.tokenIndex++;
                Token tEndLine = this.peek();
                if (tBeginLine.kind() == TokenKind.IntegerLiteral) {
                    this.tokenIndex++;
                    endLine = this.parseIntegerLiteral(tEndLine);
                } else {
                    endLine = null;
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }
            } else {
                endLine = null;
            }

            if (this.peek().code() == Tokens.Separator.CloseBracket) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
        } else {
            beginLine = null;
            endLine = null;
        }

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCInclude(filepath.text(), beginLine, endLine, position, isErrorNode);
    }

    private LCClassDeclaration parseClassDeclaration() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 6);
        }
        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }

        LCTypeReferenceExpression extended = null;
        ArrayList<LCTypeReferenceExpression> implementedInterfaces = new ArrayList<>();
        ArrayList<LCTypeReferenceExpression> permittedClasses = new ArrayList<>();
        Token t1 = this.peek();
        label:
        while (t1.kind() != TokenKind.EOF) {
            switch (t1.code()) {
                case Tokens.Keyword.Extends -> {
                    this.tokenIndex++;
                    LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
                    if (lcTypeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                        if (extended == null) {
                            extended = lcTypeReferenceExpression;
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                        }
                    } else {
                        isErrorNode = true;
                        // TODO dump error
                    }
                }
                case Tokens.Keyword.Implements -> {
                    this.tokenIndex++;
                    Token t2 = this.peek();
                    while (t2.kind() != TokenKind.EOF) {
                        LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
                        if (lcTypeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                            implementedInterfaces.add(lcTypeReferenceExpression);
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                        }
                        if (this.peek().code() == Tokens.Separator.Comma) {
                            this.tokenIndex++;
                            t2 = this.peek();
                        } else {
                            break;
                        }
                    }
                }
                case Tokens.Keyword.Permits -> {
                    this.tokenIndex++;
                    Token t2 = this.peek();
                    while (t2.kind() != TokenKind.EOF) {
                        LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
                        if (lcTypeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                            permittedClasses.add(lcTypeReferenceExpression);
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                        }
                        if (this.peek().code() == Tokens.Separator.Comma) {
                            this.tokenIndex++;
                            t2 = this.peek();
                        } else {
                            break;
                        }
                    }
                }
                case null, default -> {
                    break label;
                }
            }
            t1 = this.peek();
        }

        LCExpression delegated;
        if (this.peek().code() == Tokens.Keyword.By) {
            this.tokenIndex++;
            delegated = this.parseExpression();
        } else {
            delegated = null;
        }

        LCBlock body;
        Token t2 = this.peek();
        if (t2.code() == Tokens.Separator.OpenBrace) {
            body = this.parseClassBody();
            if (body.isErrorNode)
                isErrorNode = true;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
            body = new LCBlock(List.of(), t2.position(), true);
        }

        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCClassDeclaration(name.text(), typeParameters, extended, implementedInterfaces.toArray(new LCTypeReferenceExpression[0]), permittedClasses.toArray(new LCTypeReferenceExpression[0]), delegated, body, position, isErrorNode);
    }

    public LCBlock parseClassBody() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        List<LCStatement> statements = this.parseStatementsOfClass();

        Token t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {  //'}'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 9);
            this.skip();
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCBlock(statements, pos, isErrorNode);
    }

    private LCInterfaceDeclaration parseInterfaceDeclaration() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            this.errorStream.printError(true, this.getPos(), 6);
            isErrorNode = true;
        }

        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }

        ArrayList<LCTypeReferenceExpression> superInterfaces = new ArrayList<>();
        Token tExtends = this.peek();
        if (tExtends.code() == Tokens.Keyword.Extends) {
            this.tokenIndex++;
            while (true) {
                LCTypeExpression superInterface = this.parseTypeExpression(false);
                if (superInterface instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                    superInterfaces.add(lcTypeReferenceExpression);
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                Token t = this.peek();
                if (t.code() == Tokens.Separator.OpenBrace) {
                    break;
                } else if (t.code() == Tokens.Separator.Comma) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }
            }
        }

        LCBlock body;
        Token t1 = this.peek();
        if (t1.code() == Tokens.Separator.OpenBrace) { // '{'
            body = this.parseInterfaceBody();
            if (body.isErrorNode)
                isErrorNode = true;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
            body = new LCBlock(List.of(), t1.position(), true);
        }

        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCInterfaceDeclaration(name.text(), typeParameters, superInterfaces.toArray(new LCTypeReferenceExpression[0]), body, position, isErrorNode);
    }

    private LCBlock parseInterfaceBody() {
        boolean isErrorNode = false;
        Position beginPos = this.getPos();

        this.tokenIndex++;

        List<LCStatement> statements = new ArrayList<>();

        Token t = this.peek();
        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseBrace) {
            LCModifier modifier = this.parseModifier();

            t = this.peek();
            switch (t.code()) {
                case Tokens.Keyword.Method -> {
                    LCMethodDeclaration lcMethodDeclaration;
                    if (LCFlags.hasDefault(modifier.flags)) {
                        lcMethodDeclaration = this.parseMethodDeclaration(MethodKind.Method, modifier.flags);
                    } else {
                        Position t_beginPos = this.getPos();
                        boolean t_isErrorNode = false;
                        this.tokenIndex++;

                        Token tName = this.next();
                        if (tName.kind() != TokenKind.Identifier) {
                            t_isErrorNode = true;
                            this.errorStream.printError(true, getPos(), 12);
                        }

                        LCTypeParameter[] typeParameters;
                        if (this.peek().code() == Tokens.Operator.Less) {
                            typeParameters = this.parseGenericParameters();
                        } else {
                            typeParameters = new LCTypeParameter[0];
                        }

                        LCMethodDeclaration.LCCallSignature callSignature;
                        Token t1 = this.peek();
                        if (t1.code() == Tokens.Separator.OpenParen) {
                            callSignature = this.parseCallSignature();
                        } else {
                            t_isErrorNode = true;
                            this.errorStream.printError(true, this.getPos(), 12);
                            this.skip();
                            Position endPos = this.getPos();
                            Position thePos = new Position(t_beginPos.beginPos(), endPos.endPos(), t_beginPos.beginLine(), endPos.endLine(), t_beginPos.beginCol(), endPos.endCol());
                            callSignature = new LCMethodDeclaration.LCCallSignature(null, thePos, true);
                        }

                        long initialFlags = 0;
                        if (this.peek().code() == Tokens.Keyword.Const) {
                            this.tokenIndex++;
                            initialFlags |= LCFlags.THIS_CONST;
                        }
                        if (this.peek().code() == Tokens.Keyword.Readonly) {
                            this.tokenIndex++;
                            initialFlags |= LCFlags.THIS_READONLY;
                        }
                        if (this.peek().code() == Tokens.Keyword.Final) {
                            this.tokenIndex++;
                            initialFlags |= LCFlags.THIS_FINAL;
                        }

                        LCTypeExpression returnTypeExpression;
                        if (this.peek().code() == Tokens.Separator.Colon) {
                            this.tokenIndex++;
                            returnTypeExpression = this.parseTypeExpression(false);
                        } else {
                            returnTypeExpression = null;

                            t_isErrorNode = true;
                            // TODO dump error
                        }

                        ArrayList<LCTypeReferenceExpression> throwsExceptions = new ArrayList<>();
                        if (this.peek().code() == Tokens.Keyword.Throws) {
                            this.tokenIndex++;
                            Token t3;
                            do {
                                LCTypeExpression te = this.parseTypeExpression();
                                if (te instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                                    throwsExceptions.add(lcTypeReferenceExpression);
                                } else {
                                    isErrorNode = true;
                                    // TODO dump error
                                }

                                t3 = this.peek();
                            } while (t3.code() == Tokens.Separator.Comma);
                        }

                        LCTypeReferenceExpression extendsObject;
                        if (this.peek().code() == Tokens.Keyword.Extends) {
                            this.tokenIndex++;
                            LCTypeExpression te = this.parseTypeExpression();
                            if (te instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                                extendsObject = lcTypeReferenceExpression;
                            } else {
                                extendsObject = null;
                                t_isErrorNode = true;
                                // TODO dump error
                            }
                        } else {
                            extendsObject = null;
                        }

                        lcMethodDeclaration = new LCMethodDeclaration(MethodKind.Method, tName.text(), typeParameters, callSignature, returnTypeExpression, initialFlags, throwsExceptions.toArray(new LCTypeReferenceExpression[0]), extendsObject);

                        Position endPos = this.getPos();
                        Position pos = new Position(t_beginPos.beginPos(), endPos.endPos(), t_beginPos.beginLine(), endPos.endLine(), t_beginPos.beginCol(), endPos.endCol());
                        lcMethodDeclaration.init(null, pos, t_isErrorNode);

                        if (!LCFlags.hasExtern(modifier.flags)) modifier.flags |= LCFlags.ABSTRACT;
                    }
                    lcMethodDeclaration.setModifier(modifier);
                    statements.add(lcMethodDeclaration);
                }
                case Tokens.Separator.SemiColon -> {
                    this.tokenIndex++;
                    statements.add(new LCEmptyStatement(t.position()));
                }
                case null, default -> {
                    Position t_beginPos = this.getPos();
                    System.err.println("Can not recognize a LCStatement starting with: " + this.peek().text());
                    this.skip();
                    Position t_endPos = getPos();
                    Position pos = new Position(t_beginPos.beginPos(), t_endPos.endPos(), t_beginPos.beginLine(), t_endPos.endLine(), t_beginPos.beginCol(), t_endPos.endCol());
                    statements.add(new LCErrorStatement(pos));
                }
            }
            t = this.peek();
        }

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {  //'}'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 9);
            this.skip();
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCBlock(statements, pos, isErrorNode);
    }

    private LCAnnotationDeclaration parseAnnotationDeclaration() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex += 2;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 6);
        }

        LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody;
        Token t2 = this.peek();
        if (t2.code() == Tokens.Separator.OpenBrace) { //'{'
            lcAnnotationBody = this.parseAnnotationBody();
            if (lcAnnotationBody.isErrorNode)
                isErrorNode = true;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
            lcAnnotationBody = new LCAnnotationDeclaration.LCAnnotationBody(new LCAnnotationDeclaration.LCAnnotationFieldDeclaration[0], t2.position(), true);
        }


        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCAnnotationDeclaration(name.text(), lcAnnotationBody, position, isErrorNode);
    }

    private LCAnnotationDeclaration.LCAnnotationBody parseAnnotationBody() {
        boolean isErrorNode = false;
        Position beginPos = this.getPos();

        this.tokenIndex++;

        ArrayList<LCAnnotationDeclaration.LCAnnotationFieldDeclaration> fields = new ArrayList<>();

        Token t = this.peek();
        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseBrace) {
            Position f_beginPosition = this.getPos();
            boolean t_isErrorNode = false;

            Token t1 = this.next();
            if (t1.kind() != TokenKind.Identifier) {
                t_isErrorNode = true;
                isErrorNode = true;
                // TODO dump error
            }

            LCTypeExpression lcTypeExpression;
            if (this.peek().code() == Tokens.Separator.Colon) {
                this.tokenIndex++;
                lcTypeExpression = this.parseTypeExpression();
            } else {
                lcTypeExpression = null;
                t_isErrorNode = true;
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }


            LCExpression defaultValue;
            Token t2 = this.peek();
            if (t2.code() == Tokens.Keyword.Default) {
                this.tokenIndex++;
                defaultValue = this.parseExpression();
            } else {
                defaultValue = null;
            }

            Position endPos = this.getPos();
            Position position = new Position(f_beginPosition.beginPos(), endPos.endPos(), f_beginPosition.beginLine(), endPos.endLine(), f_beginPosition.beginCol(), endPos.endCol());
            fields.add(new LCAnnotationDeclaration.LCAnnotationFieldDeclaration(t1.text(), lcTypeExpression, defaultValue, position, t_isErrorNode));

            t = this.peek();
        }

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {  //'}'
            this.tokenIndex++;
        } else {
            this.errorStream.printError(true, this.getPos(), 9);
            this.skip();
            isErrorNode = true;
        }

        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCAnnotationDeclaration.LCAnnotationBody(fields.toArray(new LCAnnotationDeclaration.LCAnnotationFieldDeclaration[0]), position, isErrorNode);
    }

    private LCEnumDeclaration parseEnumDeclaration() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 6);
        }

        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }

        List<LCTypeReferenceExpression> implementedInterfaces = new ArrayList<>();
        Token t1 = this.peek();
        while (t1.code() == Tokens.Keyword.Implements) {
            this.tokenIndex++;
            Token t2 = this.peek();
            while (t2.kind() != TokenKind.EOF) {
                LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
                if (lcTypeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                    implementedInterfaces.add(lcTypeReferenceExpression);
                } else {
                    isErrorNode = true;
                    // TODO dump error
                }
                if (this.peek().code() == Tokens.Separator.Comma) {
                    this.tokenIndex++;
                    t2 = this.peek();
                } else {
                    break;
                }
            }
            t1 = this.peek();
        }

        LCExpression delegated;
        if (this.peek().code() == Tokens.Keyword.By) {
            this.tokenIndex++;
            delegated = this.parseExpression();
        } else {
            delegated = null;
        }

        List<LCEnumDeclaration.LCEnumFieldDeclaration> fields = new ArrayList<>();
        Token t2 = this.peek();
        if (t2.code() == Tokens.Separator.OpenBrace) { //'{'
            this.tokenIndex++;
            t2 = this.peek();
            while (t2.kind() == TokenKind.Identifier) {
                if (this.peek2().code() == Tokens.Separator.OpenParen) {
                    LCMethodCall lcMethodCall = this.parseMethodCall();
                    fields.add(new LCEnumDeclaration.LCEnumFieldDeclaration(lcMethodCall.name, lcMethodCall.arguments, lcMethodCall.position, lcMethodCall.isErrorNode));
                } else {
                    this.tokenIndex++;
                    fields.add(new LCEnumDeclaration.LCEnumFieldDeclaration(t2.text(), new LCExpression[0], t2.position(), false));
                }

                if (this.peek().code() == Tokens.Separator.Comma) {
                    this.tokenIndex++;
                }
                t2 = this.peek();
            }

            if (this.peek().code() == Tokens.Separator.CloseBrace) {
                this.tokenIndex++;
            } else {
                this.skip();
                // TODO dump error
                isErrorNode = true;
            }
        } else {
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
        }


        LCBlock body;
        Token t3 = this.peek();
        if (t3.code() != Tokens.Separator.CloseBrace) {
            body = this.parseEnumBody();
        } else {
            body = new LCBlock(List.of(), new Position(t3.position().beginPos(), t3.position().beginPos(), t3.position().beginLine(), t3.position().beginLine(), t3.position().beginCol(), t3.position().beginCol()));
        }
        if (body.isErrorNode)
            isErrorNode = true;


        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCEnumDeclaration(name.text(), typeParameters, implementedInterfaces.toArray(new LCTypeReferenceExpression[0]), delegated, fields.toArray(new LCEnumDeclaration.LCEnumFieldDeclaration[0]), body, position, isErrorNode);
    }

    private LCBlock parseEnumBody() {
        boolean isErrorNode = false;
        Position beginPos = this.getPos();

        List<LCStatement> statements = this.parseStatementsOfClass();

        Token t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {  //'}'
            this.tokenIndex++;
        } else {
            this.skip();
            this.errorStream.printError(true, this.getPos(), 9);
            isErrorNode = true;
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCBlock(statements, pos, isErrorNode);
    }

    private LCRecordDeclaration parseRecordDeclaration() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 6);
        }

        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }

        LCVariableDeclaration[] fields;
        Token t1 = this.peek();
        if (t1.code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            LCMethodDeclaration.LCCallSignature lcCallSignature = this.parseCallSignature();
            fields = lcCallSignature.parameterList.parameters;
        } else {
            fields = new LCVariableDeclaration[0];
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        ArrayList<LCTypeReferenceExpression> implementedInterfaces = new ArrayList<>();
        Token t2 = this.peek();
        while (t2.code() == Tokens.Keyword.Implements) {
            this.tokenIndex++;
            Token t3 = this.peek();
            while (t3.kind() != TokenKind.EOF) {
                LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
                if (lcTypeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                    implementedInterfaces.add(lcTypeReferenceExpression);
                } else {
                    isErrorNode = true;
                    // TODO dump error
                }
                if (this.peek().code() == Tokens.Separator.Comma) {
                    this.tokenIndex++;
                    t3 = this.peek();
                } else {
                    break;
                }
            }
            t2 = this.peek();
        }

        LCExpression delegated;
        if (this.peek().code() == Tokens.Keyword.By) {
            this.tokenIndex++;
            delegated = this.parseExpression();
        } else {
            delegated = null;
        }

        LCBlock body;
        Token t3 = this.peek();
        if (t3.code() == Tokens.Separator.OpenBrace) { //'{'
            body = this.parseClassBody();
            if (body.isErrorNode)
                isErrorNode = true;
        } else {
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
            body = new LCBlock(List.of(), t2.position(), true);
        }

        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());

        return new LCRecordDeclaration(name.text(), typeParameters, fields, implementedInterfaces.toArray(new LCTypeReferenceExpression[0]), delegated, body, position, isErrorNode);
    }

    private LCStructDeclaration parseStructDeclaration() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 6);
        }

        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }

        LCBlock body;
        Token t1 = this.peek();
        if (t1.code() == Tokens.Separator.OpenBrace) { // '{'
            body = this.parseClassBody();
            if (body.isErrorNode)
                isErrorNode = true;
        } else {
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
            body = new LCBlock(List.of(), t1.position(), true);
        }

        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCStructDeclaration(name.text(), typeParameters, body, position, isErrorNode);
    }

    public LCTypeParameter[] parseGenericParameters() {
        this.tokenIndex++;

        ArrayList<LCTypeParameter> typeParameters = new ArrayList<>();
        Token t = this.peek();
        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Operator.Greater) {
            Position beginPosition = this.getPos();
            boolean isErrorNode = false;
            Token name = this.next();
            if (name.kind() != TokenKind.Identifier) {
                isErrorNode = true;
                // TODO dump error
            }

            LCTypeReferenceExpression extended = null;
            ArrayList<LCTypeReferenceExpression> implemented = new ArrayList<>();
            LCTypeReferenceExpression supered = null;
            LCTypeReferenceExpression _default = null;
            Token t2 = this.peek();
            while (t2.code() == Tokens.Keyword.Extends || t2.code() == Tokens.Keyword.Implements || t2.code() == Tokens.Keyword.Super || t2.code() == Tokens.Keyword.Default) {
                switch (t2.code()) {
                    case Tokens.Keyword.Extends -> {
                        this.tokenIndex++;
                        LCTypeExpression typeExpression = this.parseTypeExpression();
                        if (typeExpression instanceof LCTypeReferenceExpression typeReferenceExpression) {
                            extended = typeReferenceExpression;
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                            this.skip();
                        }
                    }
                    case Tokens.Keyword.Implements -> {
                        this.tokenIndex++;
                        Token t3 = this.peek();
                        while (t3.kind() != TokenKind.EOF) {
                            LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
                            if (lcTypeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                                implemented.add(lcTypeReferenceExpression);
                            } else {
                                isErrorNode = true;
                                // TODO dump error
                            }
                            if (this.peek().code() == Tokens.Separator.Comma) {
                                this.tokenIndex++;
                                t3 = this.peek();
                            } else {
                                break;
                            }
                        }
                    }
                    case Tokens.Keyword.Super -> {
                        this.tokenIndex++;
                        LCTypeExpression typeExpression = this.parseTypeExpression();
                        if (typeExpression instanceof LCTypeReferenceExpression typeReferenceExpression) {
                            supered = typeReferenceExpression;
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                            this.skip();
                        }
                    }
                    case Tokens.Keyword.Default -> {
                        this.tokenIndex++;
                        LCTypeExpression typeExpression = this.parseTypeExpression();
                        if (typeExpression instanceof LCTypeReferenceExpression typeReferenceExpression) {
                            _default = typeReferenceExpression;
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                            this.skip();
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected code: " + t2.code());
                }
                t2 = this.peek();
            }

            Position endPosition = this.getPos();
            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
            typeParameters.add(new LCTypeParameter(name.text(), extended, implemented.toArray(new LCTypeReferenceExpression[0]), supered, _default, position, isErrorNode));

            t = this.peek();
        }

        if (this.peek().code() == Tokens.Operator.Greater) {
            this.tokenIndex++;
        } else {
            // TODO dump error
            this.skip();
        }

        return typeParameters.toArray(new LCTypeParameter[0]);
    }

    private List<LCStatement> parseStatementsOfClass() {
        List<LCStatement> statements = new ArrayList<>();
        this.anonymousClassDeclarationsStack.push(new ArrayList<>());
        Token t = this.peek();
        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseBrace) {
            LCAnnotationDeclaration.LCAnnotation[] annotations = this.parseAnnotations();

            LCModifier modifier = this.parseModifier();

            LCStatement statement;
            t = this.peek();
            switch (t.code()) {
                case Tokens.Keyword.Include -> statement = this.parseInclude();
                case Tokens.Keyword.Class -> {
                    LCClassDeclaration lcClassDeclaration = this.parseClassDeclaration();
                    lcClassDeclaration.setModifier(modifier);
                    statement = lcClassDeclaration;
                }
                case Tokens.Keyword.Interface -> {
                    modifier.flags |= LCFlags.INTERFACE;
                    LCInterfaceDeclaration lcInterfaceDeclaration = this.parseInterfaceDeclaration();
                    lcInterfaceDeclaration.setModifier(modifier);
                    statement = lcInterfaceDeclaration;
                }
                case Tokens.Keyword.Enum -> {
                    modifier.flags |= LCFlags.ENUM;
                    LCEnumDeclaration lcEnumDeclaration = this.parseEnumDeclaration();
                    lcEnumDeclaration.setModifier(modifier);
                    statement = lcEnumDeclaration;
                }
                case Tokens.Keyword.Record -> {
                    modifier.flags |= LCFlags.RECORD;
                    LCRecordDeclaration lcRecordDeclaration = this.parseRecordDeclaration();
                    lcRecordDeclaration.setModifier(modifier);
                    statement = lcRecordDeclaration;
                }
                case Tokens.Keyword.Struct -> {
                    LCStructDeclaration lcStructDeclaration = this.parseStructDeclaration();
                    lcStructDeclaration.setModifier(modifier);
                    statement = lcStructDeclaration;
                }
                case Tokens.Keyword.Var -> {
                    LCVariableDeclaration lcVariableDeclaration = this.parseVariableDeclaration(false);
                    lcVariableDeclaration.setAnnotations(annotations);
                    lcVariableDeclaration.setModifier(modifier);
                    statement = lcVariableDeclaration;
                }
                case Tokens.Keyword.Val -> {
                    LCVariableDeclaration lcVariableDeclaration = this.parseVariableDeclaration(true);
                    lcVariableDeclaration.setAnnotations(annotations);
                    lcVariableDeclaration.setModifier(modifier);
                    statement = lcVariableDeclaration;
                }
                case Tokens.Keyword.Method -> {
                    LCMethodDeclaration lcMethodDeclaration = this.parseMethodDeclaration(MethodKind.Method, modifier.flags);
                    lcMethodDeclaration.setModifier(modifier);
                    statement = lcMethodDeclaration;
                }
                case Tokens.Keyword.Constructor -> {
                    LCMethodDeclaration lcMethodDeclaration = this.parseMethodDeclaration(MethodKind.Constructor, modifier.flags);
                    lcMethodDeclaration.setModifier(modifier);
                    statement = lcMethodDeclaration;
                }
                case Tokens.Keyword.Destructor -> {
                    LCMethodDeclaration lcMethodDeclaration = this.parseMethodDeclaration(MethodKind.Destructor, modifier.flags);
                    lcMethodDeclaration.setModifier(modifier);
                    statement = lcMethodDeclaration;
                }
                case Tokens.Separator.SemiColon -> {
                    this.tokenIndex++;
                    statement = new LCEmptyStatement(t.position());
                }
                case Tokens.Keyword.__Static_init__ -> {
                    Position beginPosition = this.getPos();
                    this.tokenIndex++;
                    boolean isErrorNode = false;
                    if (this.peek().code() != Tokens.Separator.OpenBrace) {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }
                    LCBlock body = this.parseMethodBlock();
                    Position endPosition = this.getPos();
                    Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                    statement = new LCInit(true, body, position, isErrorNode);
                }
                case Tokens.Keyword.__Init__ -> {
                    Position beginPosition = this.getPos();
                    this.tokenIndex++;
                    boolean isErrorNode = false;
                    if (this.peek().code() != Tokens.Separator.OpenBrace) {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }
                    LCBlock body = this.parseMethodBlock();
                    Position endPosition = this.getPos();
                    Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                    statement = new LCInit(false, body, position, isErrorNode);
                }
                default -> {
                    if (t.code() == Tokens.Operator.At && this.peek2().code() == Tokens.Keyword.Interface) {
                        modifier.flags |= LCFlags.ANNOTATION;
                        LCAnnotationDeclaration lcAnnotationDeclaration = this.parseAnnotationDeclaration();
                        lcAnnotationDeclaration.setModifier(modifier);
                        statement = lcAnnotationDeclaration;
                    } else {
                        Position beginPos = this.getPos();
                        System.err.println("Can not recognize a statement starting with: " + this.peek().text());
                        this.skip();
                        Position endPos = getPos();
                        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                        statement = new LCErrorStatement(pos);
                    }

                }
            }
            statement.setAnnotations(annotations);
            statements.add(statement);
            t = this.peek();
        }
        statements.addAll(0, this.anonymousClassDeclarationsStack.pop());

        return statements;
    }

    private LCAnnotationDeclaration.LCAnnotation[] parseAnnotations() {
        ArrayList<LCAnnotationDeclaration.LCAnnotation> annotations = new ArrayList<>();
        while (this.peek().code() == Tokens.Operator.At && this.peek2().code() != Tokens.Keyword.Interface) {
            annotations.add(this.parseAnnotation());
        }
        return annotations.toArray(new LCAnnotationDeclaration.LCAnnotation[0]);
    }

    private LCAnnotationDeclaration.LCAnnotation parseAnnotation() {
        Position beginPos = this.getPos();
        this.tokenIndex++;

        boolean isErrorNode = false;

        Token name = this.next();
        if (name.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            // TODO dump error
        }

        ArrayList<LCAnnotationDeclaration.LCAnnotation.LCAnnotationField> arguments = new ArrayList<>();
        Token t1 = this.peek();
        if (t1.code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            Token t2 = this.peek();
            while (t2.code() != Tokens.Separator.CloseParen && t2.kind() != TokenKind.EOF) {
                if (t2.kind() == TokenKind.Identifier && this.peek2().code() == Tokens.Operator.Assign) {
                    Position begin = this.getPos();
                    this.tokenIndex += 2;
                    LCExpression expression = this.parseExpression();
                    Position endPos = this.getPos();
                    Position position = new Position(begin.beginPos(), endPos.endPos(), begin.beginLine(), endPos.endLine(), begin.beginCol(), endPos.endCol());
                    arguments.add(new LCAnnotationDeclaration.LCAnnotation.LCAnnotationField(t2.text(), expression, position, false));
                } else {
                    LCExpression expression = this.parseExpression();
                    arguments.add(new LCAnnotationDeclaration.LCAnnotation.LCAnnotationField("value", expression, expression.position, false));
                    if (this.peek().code() != Tokens.Separator.CloseParen) {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }
                }
                t2 = this.peek();
            }

            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
        }

        Position endPos = this.getPos();
        Position position = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCAnnotationDeclaration.LCAnnotation(name.text(), arguments.toArray(new LCAnnotationDeclaration.LCAnnotation.LCAnnotationField[0]), position, isErrorNode);
    }

    private LCReturn parseReturn() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;
        LCExpression exp = null;

        this.tokenIndex++;

        Token token = this.peek();
        if (!token.newLine() && token.code() != Tokens.Separator.SemiColon) {
            exp = this.parseExpression();
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCReturn(exp, pos, isErrorNode);
    }

    private LCIf parseIf() {
        Position beginPos = this.getPos();
        this.tokenIndex++;
        boolean isErrorNode = false;

        LCExpression condition;
        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            condition = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after if condition.");
                this.skip();
            }
        } else {
            System.err.println("Expecting '(' after 'if'.");
            this.skip();
            Position endPos = this.getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            condition = new LCErrorExpression(pos);
        }

        LCStatement then = this.parseMethodBlockStatement();

        LCStatement _else;
        if (this.peek().code() == Tokens.Keyword.Else) {
            this.tokenIndex++;
            _else = this.parseMethodBlockStatement();
        } else {
            _else = null;
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCIf(condition, then, _else, pos, isErrorNode);
    }

    private LCMethodDeclaration parseMethodDeclaration(MethodKind methodKind, long flags) {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;
        this.tokenIndex++;

        String name;
        Token t = this.peek();
        if (methodKind == MethodKind.Constructor) {
            name = "<init>";
        } else if (methodKind == MethodKind.Destructor) {
            name = "<deinit>";
        } else if (methodKind == MethodKind.Getter) {
            name = "get";
        } else if (methodKind == MethodKind.Setter) {
            name = "set";
        } else {
            this.tokenIndex++;
            name = t.text();
            if (t.kind() != TokenKind.Identifier) {
                isErrorNode = true;
                this.errorStream.printError(true, getPos(), 12);
            }
        }

        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }

        LCMethodDeclaration.LCCallSignature callSignature;
        Token t1 = this.peek();
        if (t1.code() == Tokens.Separator.OpenParen) {
            callSignature = this.parseCallSignature();
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 12);
            this.skip();
            Position endPos = this.getPos();
            Position thePos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            callSignature = new LCMethodDeclaration.LCCallSignature(null, thePos, true);
        }

        if (methodKind == MethodKind.Destructor && callSignature.parameterList != null && callSignature.parameterList.parameters != null && callSignature.parameterList.parameters.length != 0) {
            isErrorNode = true;
            // TODO dump error
        }

        long initialFlags = 0;
        if (this.peek().code() == Tokens.Keyword.Const) {
            this.tokenIndex++;
            initialFlags |= LCFlags.THIS_CONST;
        }
        if (this.peek().code() == Tokens.Keyword.Readonly) {
            this.tokenIndex++;
            initialFlags |= LCFlags.THIS_READONLY;
        }
        if (this.peek().code() == Tokens.Keyword.Final) {
            this.tokenIndex++;
            initialFlags |= LCFlags.THIS_FINAL;
        }

        LCTypeExpression returnTypeExpression;
        if (this.peek().code() == Tokens.Separator.Colon) {
            this.tokenIndex++;
            returnTypeExpression = this.parseTypeExpression();
            if (methodKind != MethodKind.Method) {
                isErrorNode = true;
                // TODO dump error
            }
        } else {
            returnTypeExpression = null;
        }

        ArrayList<LCTypeReferenceExpression> throwsExceptions = new ArrayList<>();
        if (this.peek().code() == Tokens.Keyword.Throws) {
            this.tokenIndex++;
            Token t3 = this.peek();
            while (t3.kind() != TokenKind.EOF) {
                LCTypeExpression te = this.parseTypeExpression();
                if (te instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                    throwsExceptions.add(lcTypeReferenceExpression);
                } else {
                    isErrorNode = true;
                    // TODO dump error
                }

                if (this.peek().code() == Tokens.Separator.Comma) {
                    this.tokenIndex++;
                } else {
                    break;
                }

                t3 = this.peek();
            }
        }

        LCTypeReferenceExpression extendsObject;
        if (this.peek().code() == Tokens.Keyword.Extends) {
            this.tokenIndex++;
            LCTypeExpression te = this.parseTypeExpression();
            if (te instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                extendsObject = lcTypeReferenceExpression;
            } else {
                extendsObject = null;
                isErrorNode = true;
                // TODO dump error
            }
        } else {
            extendsObject = null;
        }

        LCMethodDeclaration lcMethodDeclaration = new LCMethodDeclaration(methodKind, name, typeParameters, callSignature, returnTypeExpression, initialFlags, throwsExceptions.toArray(new LCTypeReferenceExpression[0]), extendsObject);

        LCBlock methodBody;
        if (LCFlags.hasAbstract(flags) || LCFlags.hasExtern(flags)) {
            Position t_beginPos = this.getPos();
            Position thePos = new Position(t_beginPos.beginPos(), t_beginPos.beginPos(), t_beginPos.beginLine(), t_beginPos.beginLine(), t_beginPos.beginCol(), t_beginPos.beginCol());
            methodBody = new LCBlock(List.of(), thePos, isErrorNode);
        } else {
            t1 = peek();
            if (t1.code() == Tokens.Separator.OpenBrace) {
                methodBody = this.parseMethodBlock();
            } else if (t1.code() == Tokens.Operator.Assign) {
                this.tokenIndex++;

                Position t_beginPos = this.getPos();
                boolean t_isErrorNode = false;

                LCExpression expression = this.parseExpression();

                Position t_endPos = this.getPos();
                Position thePos = new Position(t_beginPos.beginPos(), t_endPos.endPos(), t_beginPos.beginLine(), t_endPos.endLine(), t_beginPos.beginCol(), t_endPos.endCol());
                methodBody = new LCBlock(List.of(new LCExpressionStatement(expression, expression.position, t_isErrorNode)), thePos, t_isErrorNode || isErrorNode);
            } else {
                Position t_beginPos = this.getPos();

                isErrorNode = true;
                this.errorStream.printError(true, getPos(), 12);
                this.skip();

                Position t_endPos = this.getPos();
                Position thePos = new Position(t_beginPos.beginPos(), t_endPos.endPos(), t_beginPos.beginLine(), t_endPos.endLine(), t_beginPos.beginCol(), t_endPos.endCol());
                methodBody = new LCBlock(List.of(), thePos, isErrorNode);
            }
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        lcMethodDeclaration.init(methodBody, pos, isErrorNode);

        return lcMethodDeclaration;
    }

    private LCBlock parseMethodBlock() {
        Position beginPos = this.getPos();

        this.tokenIndex++;

        List<LCStatement> statements = new ArrayList<>();
        Token t = this.peek();

        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseBrace) {
            statements.add(this.parseMethodBlockStatement());
            t = this.peek();
        }

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {
            this.tokenIndex++;
            Position endPos = this.getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            return new LCBlock(statements, pos);
        } else {
            System.err.println("Expecting '}' while parsing a LCBlock, but we got a " + t.text());
            this.skip();
            Position endPos = getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            return new LCBlock(statements, pos, true);
        }
    }

    private LCStatement parseMethodBlockStatement() {
        Token t1 = this.peek();

        ArrayList<String> labels = new ArrayList<>();
        while (t1.kind() == TokenKind.Identifier && this.peek2().code() == Tokens.Separator.Colon && this.peek3().code() != Tokens.Separator.Colon) {
            this.tokenIndex += 2;
            labels.add(t1.text());
            t1 = this.peek();
        }
        LCModifier modifier = parseModifier();
        Token t2 = this.peek();
        LCStatement statement = switch (t2.code()) {
            case Tokens.Keyword.Return -> this.parseReturn();
            case Tokens.Keyword.For -> this.parseFor();
            case Tokens.Keyword.Foreach -> this.parseForeach();
            case Tokens.Keyword.While -> this.parseWhile();
            case Tokens.Keyword.Do -> this.parseDoWhile();
            case Tokens.Keyword.Loop -> this.parseLoop();
            case Tokens.Keyword.Goto -> this.parseGoto();
            case Tokens.Keyword.Break -> this.parseBreak();
            case Tokens.Keyword.Continue -> this.parseContinue();
            case Tokens.Keyword.Throw -> this.parseThrow();
            case Tokens.Keyword.Try -> this.parseTry();
            case Tokens.Keyword.Assert -> this.parseAssert();
            case Tokens.Keyword.Native -> this.parseNative();
            case Tokens.Keyword.Switch -> this.parseSwitchStatement();
            case Tokens.Keyword.Include -> this.parseInclude();
            case Tokens.Keyword.Yield -> this.parseYield();
            case Tokens.Keyword.With -> this.parseWith();
            case Tokens.Keyword.Var -> {
                LCVariableDeclaration lcVariableDeclaration = this.parseVariableDeclaration(false);
                lcVariableDeclaration.setModifier(modifier);
                yield lcVariableDeclaration;
            }
            case Tokens.Keyword.Val -> {
                LCVariableDeclaration lcVariableDeclaration = this.parseVariableDeclaration(true);
                lcVariableDeclaration.setModifier(modifier);
                yield lcVariableDeclaration;
            }
            case Tokens.Separator.OpenBrace -> {
                LCBlock block = this.parseMethodBlock();
                yield new LCExpressionStatement(block, block.position, block.isErrorNode);
            }
            case null, default -> {
                if (t2.kind() == TokenKind.Identifier || t2.kind() == TokenKind.DecimalLiteral || t2.kind() == TokenKind.IntegerLiteral || t2.kind() == TokenKind.StringLiteral || t2.kind() == TokenKind.BooleanLiteral || t2.kind() == TokenKind.NullLiteral || t2.kind() == TokenKind.NullptrLiteral
                        || t2.kind() == TokenKind.Operator || t2.code() instanceof Tokens.Type || t2.code() == Tokens.Keyword.Synchronized
                        || t2.code() == Tokens.Keyword.If || t2.code() == Tokens.Separator.OpenParen || t2.code() == Tokens.Keyword.Typeof || t2.code() == Tokens.Keyword.Sizeof
                        || t2.code() == Tokens.Keyword.This || t2.code() == Tokens.Keyword.Super || t2.code() == Tokens.Keyword.Lambda
                        || t2.code() == Tokens.Keyword.Clone || t2.code() == Tokens.Keyword.New || t2.code() == Tokens.Keyword.Delete || t2.code() == Tokens.Keyword.Malloc || t2.code() == Tokens.Keyword.Free || t2.code() == Tokens.Keyword.Realloc
                        || t2.code() == Tokens.Operator.BitAnd || t2.code() == Tokens.Operator.Multiply || t2.code() == Tokens.Keyword.Static_cast || t2.code() == Tokens.Keyword.Dynamic_cast || t2.code() == Tokens.Keyword.Reinterpret_cast
                        || t2.code() == Tokens.Keyword.Destructor || t2.code() == Tokens.Keyword.Classof || t2.code() == Tokens.Keyword.Method_address_of || t2.code() == Tokens.Keyword.__Platform__ || t2.code() == Tokens.Keyword.__Field__ || t2.code() == Tokens.Keyword.__Type__) {
                    yield this.parseExpressionStatement();
                } else if (t2.code() == Tokens.Separator.SemiColon) {
                    this.tokenIndex++;
                    yield new LCEmptyStatement(t2.position());
                } else {
                    errorStream.printError(true, t2.position(), 1);
                    Position beginPos = this.getPos();
                    System.err.println("Can not recognize a LCStatement starting with: " + this.peek().text());
                    this.skip();
                    Position endPos = this.getPos();
                    Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                    yield new LCErrorStatement(pos);
                }
            }
        };
        statement.labels = labels.toArray(new String[0]);

        return statement;
    }

    private LCWith parseWith() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        List<LCVariableDeclaration> resources = new ArrayList<>();
        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            Token t = this.peek();
            while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseParen) {
                resources.add(this.parseVariableDeclaration(false));
                t = this.peek();
            }
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
        }

        LCStatement body = this.parseMethodBlockStatement();

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCWith(resources, body, position, isErrorNode);
    }

    private LCYield parseYield() {
        Position beginPosition = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        LCExpression value = this.parseExpression();

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCYield(value, position, isErrorNode);
    }

    private LCNative parseNative() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        LCNative.LCResourceForNative[] resources;
        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            resources = this.parseResourcesForNative();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
        } else {
            resources = new LCNative.LCResourceForNative[0];
        }

        if (this.peek().code() == Tokens.Separator.OpenBrace) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 8);
            this.skip();
        }

        LCNativeSection[] sections = this.parseNativeSections();

        if (this.peek().code() == Tokens.Separator.CloseBrace) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, this.getPos(), 9);
            this.skip();
        }

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCNative(resources, sections, position, isErrorNode);
    }

    private LCNative.LCResourceForNative[] parseResourcesForNative() {
        ArrayList<LCNative.LCResourceForNative> resources = new ArrayList<>();
        Token t = this.peek();
        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseParen) {
            Position beginPosition = this.getPos();
            boolean isErrorNode = false;
            LCExpression source = this.parseExpression();
            if (this.peek().code() == Tokens.Operator.Arrow) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
            Token name = this.next();
            if (name.kind() != TokenKind.Identifier) {
                isErrorNode = true;
                // TODO dump error
            }
            Position endPosition = this.getPos();
            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
            resources.add(new LCNative.LCResourceForNative(source, name.text(), position, isErrorNode));

            t = this.peek();
            if (t.code() == Tokens.Separator.Comma) {
                this.tokenIndex++;
            } else if (t.code() != Tokens.Separator.CloseParen) {
                isErrorNode = true;
//                            this.addError("Expecting ',' or '）' after a parameter", this.scanner.getLastPos());
                this.skip();
            }

            t = this.peek();
        }
        return resources.toArray(new LCNative.LCResourceForNative[0]);
    }

    private LCNativeSection[] parseNativeSections() {
        ArrayList<LCNativeSection> sections = new ArrayList<>();
        Token t = this.peek();
        while (t.kind() == TokenKind.StringLiteral || t.kind() == TokenKind.Identifier) {
            if (t.kind() == TokenKind.StringLiteral) {
                this.tokenIndex++;
                sections.add(new LCNativeSection.LCNativeCode(t.text(), t.position()));
            } else {
                Position beginPosition = this.getPos();
                boolean isErrorNode = false;

                String name = t.text();

                this.tokenIndex++;

                LCIntegerLiteral beginLine;
                LCIntegerLiteral endLine;
                if (this.peek().code() == Tokens.Separator.OpenBracket) {
                    this.tokenIndex++;
                    Token tBeginLine = this.peek();
                    if (tBeginLine.kind() == TokenKind.IntegerLiteral) {
                        this.tokenIndex++;
                        beginLine = this.parseIntegerLiteral(tBeginLine);
                    } else {
                        beginLine = null;
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }

                    if (this.peek().code() == Tokens.Separator.Colon) {
                        this.tokenIndex++;
                        Token tEndLine = this.peek();
                        if (tBeginLine.kind() == TokenKind.IntegerLiteral) {
                            this.tokenIndex++;
                            endLine = this.parseIntegerLiteral(tEndLine);
                        } else {
                            endLine = null;
                            isErrorNode = true;
                            // TODO dump error
                            this.skip();
                        }
                    } else {
                        endLine = null;
                    }

                    if (this.peek().code() == Tokens.Separator.CloseBracket) {
                        this.tokenIndex++;
                    } else {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }
                } else {
                    beginLine = null;
                    endLine = null;
                }

                Position endPosition = this.getPos();
                Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                sections.add(new LCNativeSection.LCReferenceNativeFile(name, beginLine, endLine, position, isErrorNode));
            }
            t = this.peek();
        }
        return sections.toArray(new LCNativeSection[0]);
    }

    private LCAssert parseAssert() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        LCExpression condition = this.parseExpression();

        LCExpression message;
        if (this.peek().code() == Tokens.Separator.Colon) {
            this.tokenIndex++;
            message = this.parseExpression();
        } else {
            message = null;
        }

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCAssert(condition, message, position, isErrorNode);
    }

    private LCTry parseTry() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        ArrayList<LCStatement> resources = new ArrayList<>();
        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            Token t = this.peek();
            while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseParen) {
                resources.add(this.parseMethodBlockStatement());
                t = this.peek();
            }
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
        }

        LCStatement base = this.parseMethodBlockStatement();

        ArrayList<LCTry.LCCatch> catchers = new ArrayList<>();
        while (this.peek().code() == Tokens.Keyword.Catch) {
            catchers.add(this.parseCatchBlock());
        }

        LCStatement _finally;
        if (this.peek().code() == Tokens.Keyword.Finally) {
            this.tokenIndex++;
            _finally = this.parseMethodBlockStatement();
        } else {
            _finally = null;
        }

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCTry(resources.toArray(new LCStatement[0]), base, catchers.toArray(new LCTry.LCCatch[0]), _finally, position, isErrorNode);
    }

    private LCTry.LCCatch parseCatchBlock() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }


        LCModifier modifier = this.parseModifier();

        Token tName = this.next();
        if (tName.kind() != TokenKind.Identifier) {
            isErrorNode = true;
            // TODO dump error
        }

        LCTypeExpression lcTypeExpression;
        Token t3 = this.peek();
        if (t3.code() == Tokens.Separator.Colon) {
            this.tokenIndex++;
            lcTypeExpression = this.parseTypeExpression(false);
        } else {
            lcTypeExpression = null;
        }

        Position endPos = this.getPos();
        Position pos = new Position(modifier.position.beginPos(), endPos.endPos(), modifier.position.beginLine(), endPos.endLine(), modifier.position.beginCol(), endPos.endCol());
        LCVariableDeclaration exceptionVariableDeclaration = new LCVariableDeclaration(modifier, false, tName.text(), lcTypeExpression, null, null, null, null, pos, false);


        if (this.peek().code() == Tokens.Separator.CloseParen) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        LCStatement then = this.parseMethodBlockStatement();

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCTry.LCCatch(exceptionVariableDeclaration, then, position, isErrorNode);
    }

    private LCThrow parseThrow() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        LCExpression expression = this.parseExpression();

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCThrow(expression, position, isErrorNode);
    }

    private LCSynchronized parseSynchronized() {
        Position beginPosition = this.getPos();
        boolean isErrorNode = false;
        this.tokenIndex++;

        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }
        LCExpression lock = this.parseExpression();
        if (this.peek().code() == Tokens.Separator.CloseParen) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        if (this.peek().code() != Tokens.Separator.OpenBrace) {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }
        LCBlock block = this.parseMethodBlock();

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCSynchronized(lock, block, position, isErrorNode);
    }

    private LCGoto parseGoto() {
        Position beginPos = getPos();
        this.tokenIndex++;

        Token t1 = this.peek();
        String label;
        boolean isErrorNode = false;
        if (t1.kind() == TokenKind.Identifier) {
            label = t1.text();
            this.tokenIndex++;
        } else {
            label = null;
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCGoto(label, pos, isErrorNode);
    }

    private LCBreak parseBreak() {
        Position beginPos = getPos();
        this.tokenIndex++;

        Token t1 = this.peek();
        String label;
        if (t1.kind() == TokenKind.Identifier) {
            label = t1.text();
            this.tokenIndex++;
        } else {
            label = null;
        }

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCBreak(label, pos, false);
    }

    private LCContinue parseContinue() {
        Position beginPos = this.getPos();
        this.tokenIndex++;

        Token t1 = this.peek();
        String label;
        if (t1.kind() == TokenKind.Identifier) {
            label = t1.text();
            this.tokenIndex++;
        } else {
            label = null;
        }

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCContinue(label, pos, false);
    }

    private LCFor parseFor() {
        Position beginPos = this.getPos();

        this.tokenIndex++;

        boolean isErrorNode = false;
        LCStatement init = null;
        LCExpression condition = null;
        LCExpression increment = null;

        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;

            if (this.peek().code() != Tokens.Separator.SemiColon) {
                init = this.parseMethodBlockStatement();
            }
            if (this.peek().code() == Tokens.Separator.SemiColon) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ';' after init part of for loop.");
                this.skip();
            }
            if (this.peek().code() != Tokens.Separator.SemiColon) {
                condition = this.parseExpression();
            }
            if (this.peek().code() == Tokens.Separator.SemiColon) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ';' after condition part of for loop.");
                this.skip();
            }

            if (this.peek().code() != Tokens.Separator.CloseParen) {
                increment = this.parseExpression();
            }

            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after increment part of for loop.");
                this.skip();
            }
        } else {
            System.err.println("Expecting '(' after 'for' in the for loop.");
            this.skip();
            isErrorNode = true;
        }

        LCStatement statement = this.parseMethodBlockStatement();

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCFor(init, condition, increment, statement, pos, isErrorNode);
    }

    private LCForeach parseForeach() {
        Position beginPos = this.getPos();

        this.tokenIndex++;

        boolean isErrorNode = false;
        LCVariableDeclaration init = null;
        LCExpression source = null;

        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;

            Token t1 = this.peek();
            if (t1.code() == Tokens.Keyword.Var) {
                init = this.parseVariableDeclaration(false);
                init.setModifier(new LCModifier(0, new Position(init.position.beginPos(), init.position.beginPos(), init.position.beginLine(), init.position.beginLine(), init.position.beginCol(), init.position.beginCol())));
            } else if (t1.code() == Tokens.Keyword.Val) {
                init = this.parseVariableDeclaration(true);
                init.setModifier(new LCModifier(0, new Position(init.position.beginPos(), init.position.beginPos(), init.position.beginLine(), init.position.beginLine(), init.position.beginCol(), init.position.beginCol())));
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }

            if (this.peek().code() == Tokens.Keyword.In) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting 'in' after variable declaration of for-each loop.");
                this.skip();
            }
            source = this.parseExpression();

            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after source part of for-each loop.");
                this.skip();
            }
        } else {
            System.err.println("Expecting '(' after 'foreach' in the for-each loop.");
            this.skip();
            isErrorNode = true;
        }

        LCStatement statement = this.parseMethodBlockStatement();

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCForeach(init, source, statement, pos, isErrorNode);
    }

    private LCWhile parseWhile() {
        Position beginPos = getPos();
        this.tokenIndex++;

        boolean isErrorNode = false;
        LCExpression condition = null;
        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            condition = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after the condition in the while loop.");
                this.skip();
            }
        } else {
            isErrorNode = true;
            System.err.println("Expecting '(' after 'while' in the while loop.");
            this.skip();
        }

        LCStatement body = this.parseMethodBlockStatement();

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCWhile(condition, body, pos, isErrorNode);
    }

    private LCDoWhile parseDoWhile() {
        Position beginPos = getPos();
        this.tokenIndex++;

        LCStatement body = this.parseMethodBlockStatement();

        boolean isErrorNode = false;

        if (this.peek().code() == Tokens.Keyword.While) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            System.err.println("Expecting 'while' after the body in the do-while loop.");
            this.skip();
        }

        LCExpression condition = null;
        if (this.peek().code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            condition = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after the condition in the do-while loop.");
                this.skip();
            }
        } else {
            isErrorNode = true;
            System.err.println("Expecting '(' after 'while' in the do-while loop.");
            this.skip();
        }

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCDoWhile(body, condition, pos, isErrorNode);
    }

    private LCLoop parseLoop() {
        Position beginPos = getPos();
        this.tokenIndex++;

        LCStatement body = this.parseMethodBlockStatement();

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCLoop(body, pos, false);
    }

    private LCMethodDeclaration.LCCallSignature parseCallSignature() {
        Position beginPos = getPos();
        this.tokenIndex++;

        LCParameterList parameterList = this.parseParameterList(false);

        Token t = peek();
        if (t.code() == Tokens.Separator.CloseParen) {
            this.tokenIndex++;

            Position endPos = getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            return new LCMethodDeclaration.LCCallSignature(parameterList, pos);
        } else {
            this.errorStream.printError(true, getPos(), 12);
            this.skip();
            Position endPos = getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            return new LCMethodDeclaration.LCCallSignature(parameterList, pos, true);
        }
    }

    private LCParameterList parseParameterList(boolean isMethodTypeExpression) {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;

        ArrayList<LCVariableDeclaration> params = new ArrayList<>();

        Token t = this.peek();
        while (t.kind() != TokenKind.EOF && t.code() != Tokens.Separator.CloseParen) {
            LCModifier modifier = this.parseModifier();
            boolean v_isErrorNode = false;

            Token t2 = this.peek();
            String varName;
            if (t2.kind() == TokenKind.Identifier) {
                this.tokenIndex++;
                varName = t2.text();
            } else {
                varName = null;
                if (!isMethodTypeExpression) {
                    v_isErrorNode = true;
                    isErrorNode = true;
                    // TODO dump error
                }
            }

            LCTypeExpression lcTypeExpression;
            if (varName == null) {
                lcTypeExpression = this.parseTypeExpression();
            } else {
                Token t3 = this.peek();
                if (t3.code() == Tokens.Separator.Colon) {
                    this.tokenIndex++;
                    lcTypeExpression = this.parseTypeExpression();
                } else {
                    lcTypeExpression = null;
                }
            }

            Position endPos = this.getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            params.add(new LCVariableDeclaration(modifier, false, varName, lcTypeExpression, null, null, null, null, pos, v_isErrorNode));

            t = this.peek();
            if (t.code() == Tokens.Separator.Comma) {
                this.tokenIndex++;
                t = this.peek();
            } else if (t.code() != Tokens.Separator.CloseParen) {
                isErrorNode = true;
//                            this.addError("Expecting ',' or '）' after a parameter", this.scanner.getLastPos());
                this.skip();
            }
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCParameterList(params.toArray(new LCVariableDeclaration[0]), pos, isErrorNode);
    }

    private LCVariableDeclaration parseVariableDeclaration(boolean isVal) {
        Position beginPos = this.getPos();
        this.tokenIndex++;

        Token t = this.peek();
        if (t.kind() == TokenKind.Identifier) {
            this.tokenIndex++;
            String varName = t.text();
            LCExpression init = null;

            LCTypeExpression lcTypeExpression;
            if (this.peek().code() == Tokens.Separator.Colon) {
                this.tokenIndex++;
                lcTypeExpression = this.parseTypeExpression();
            } else {
                lcTypeExpression = null;
            }

            LCTypeReferenceExpression extended;
            if (this.peek().code() == Tokens.Keyword.Extends) {
                this.tokenIndex++;
                LCTypeExpression typeExpression = this.parseTypeExpression();
                if (typeExpression instanceof LCTypeReferenceExpression typeReferenceExpression) {
                    extended = typeReferenceExpression;
                } else {
                    extended = null;
                }
            } else {
                extended = null;
            }

            LCExpression delegated;
            if (this.peek().code() == Tokens.Keyword.By) {
                this.tokenIndex++;
                delegated = this.parseExpression();
            } else {
                delegated = null;
            }

            if (this.peek().code() == Tokens.Operator.Assign) {
                this.tokenIndex++;
                init = this.parseExpression();
            }

            LCMethodDeclaration getter = null;
            LCMethodDeclaration setter = null;
            if (this.peek().code() == Tokens.Separator.OpenBrace) {
                this.tokenIndex++;
                if ("get".equals(this.peek().text())) {
                    getter = this.parseMethodDeclaration(MethodKind.Getter, 0);
                    getter.name = "<" + getter.name + "_" + varName + ">";
                    getter.setModifier(new LCModifier(Position.origin));
                }
                if ("set".equals(this.peek().text())) {
                    setter = this.parseMethodDeclaration(MethodKind.Setter, 0);
                    setter.name = "<" + setter.name + "_" + varName + ">";
                    setter.setModifier(new LCModifier(Position.origin));
                }
                if (this.peek().code() == Tokens.Separator.CloseBrace) {
                    this.tokenIndex++;
                } else {
                    this.errorStream.printError(true, getPos(), 9);
                }
            }

            Position endPos = this.getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            return new LCVariableDeclaration(isVal, varName, lcTypeExpression, extended, delegated, init, getter, setter, pos, false);
        } else {
            System.err.println("Expecting variable name in LCVariableDeclaration, while we meet " + t.text());
            this.skip();
            Position endPos = this.getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            return new LCVariableDeclaration(isVal, "unknown", null, null, null, null, null, pos, true);
        }
    }

    private LCExpressionStatement parseExpressionStatement() {
        LCExpression expression = this.parseExpression();

        Position endPos = this.getPos();
        Position pos = new Position(expression.position.beginPos(), endPos.endPos(), expression.position.beginLine(), endPos.endLine(), expression.position.beginCol(), endPos.endCol());
        return new LCExpressionStatement(expression, pos);
    }

    private LCExpression parseExpression() {
        return this.parseAssignment();
    }

    private LCExpression parseAssignment() {
        int assignPrec = this.getPrec(Tokens.Operator.Assign);

        LCExpression exp1 = this.parseBinary(assignPrec);
        Token t = this.peek();
        int tPrec = this.getPrec(t.code());
        Stack<LCExpression> expressionStack = new Stack<>();
        expressionStack.push(exp1);
        Stack<Tokens.Operator> operatorStack = new Stack<>();

        while (Token.isOperator(t) && tPrec == assignPrec) {
            operatorStack.push((Tokens.Operator) t.code());
            this.tokenIndex++;
            exp1 = this.parseBinary(assignPrec);
            expressionStack.push(exp1);
            t = this.peek();
            tPrec = this.getPrec(t.code());
        }

        LCExpression[] tempExpressionStack = expressionStack.toArray(new LCExpression[0]);
        Tokens.Operator[] tempOperatorStack = operatorStack.toArray(new Tokens.Operator[0]);
        exp1 = expressionStack.peek();
        if (!operatorStack.isEmpty()) {
            for (int i = expressionStack.size() - 2; i >= 0; i--) {
                exp1 = new LCBinary(tempOperatorStack[i], tempExpressionStack[i], exp1);
            }
        }
        return exp1;
    }

    private LCExpression parseBinary(int prec) {
        LCExpression expression1 = this.parseUnary();
        Token t = this.peek();
        int tPrec = this.getPrec(t.code());

        while (Token.isOperator(t) && tPrec > prec) {
            this.tokenIndex++;
            if (t.code() == Tokens.Keyword.Instanceof) {
                LCTypeExpression typeExpression = this.parseTypeExpression();
                LCTypeReferenceExpression typeReferenceExpression;
                if (typeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                    typeReferenceExpression = lcTypeReferenceExpression;
                } else {
                    typeReferenceExpression = null;
                    // TODO dump error
                }
                Position endPosition = this.getPos();
                Position position = new Position(t.position().beginPos(), endPosition.endPos(), t.position().beginLine(), endPosition.endLine(), t.position().beginCol(), endPosition.endCol());
                expression1 = new LCInstanceof(expression1, typeReferenceExpression, position, typeReferenceExpression == null);
            } else {
                LCExpression expression2 = this.parseBinary(tPrec);
                if (t.kind() == TokenKind.Operator) {
                    expression1 = new LCBinary((Tokens.Operator) t.code(), expression1, expression2);
                } else if (t.code() == Tokens.Keyword.Is) {
                    Position endPosition = this.getPos();
                    Position position = new Position(t.position().beginPos(), endPosition.endPos(), t.position().beginLine(), endPosition.endLine(), t.position().beginCol(), endPosition.endCol());
                    expression1 = new LCIs(expression1, expression2, position, false);
                } else if (t.code() == Tokens.Keyword.In) {
                    Position endPosition = this.getPos();
                    Position position = new Position(t.position().beginPos(), endPosition.endPos(), t.position().beginLine(), endPosition.endLine(), t.position().beginCol(), endPosition.endCol());
                    expression1 = new LCIn(expression1, expression2, position, false);
                }
            }
            t = this.peek();
            tPrec = this.getPrec(t.code());
        }
        return expression1;
    }

    private LCExpression parseUnary() {
        Position beginPosition = this.getPos();
        Token t = this.peek();

        LCExpression expression;
        if (t.code() == Tokens.Operator.Plus || t.code() == Tokens.Operator.Minus || t.code() == Tokens.Operator.Inc || t.code() == Tokens.Operator.Dec || t.code() == Tokens.Operator.Not || t.code() == Tokens.Operator.BitNot) {
            this.tokenIndex++;
            expression = this.parseUnary();
            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            return new LCUnary((Tokens.Operator) t.code(), true, expression, pos);
        } else {
            expression = this.parsePrimary();
            Token t1 = this.peek();
            if (t1.code() == Tokens.Operator.Inc || t1.code() == Tokens.Operator.Dec) {
                this.tokenIndex++;
                Position endPos = getPos();
                Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
                return new LCUnary((Tokens.Operator) t1.code(), false, expression, pos);
            } else if (t1.code() == Tokens.Operator.Not && this.peek2().code() == Tokens.Operator.Not) {
                this.tokenIndex += 2;
                Position endPosition = this.getPos();
                Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                return new LCNotNullAssert(expression, position, false);
            } else {
                return expression;
            }
        }
    }

    private LCExpression parsePrimary() {
        Position beginPosition = this.getPos();
        Token t = this.peek();

        LCExpression expression;

        if (t.kind() == TokenKind.IntegerLiteral || t.kind() == TokenKind.DecimalLiteral || t.kind() == TokenKind.NullLiteral || t.kind() == TokenKind.NullptrLiteral ||
                t.kind() == TokenKind.BooleanLiteral || t.kind() == TokenKind.CharLiteral || t.kind() == TokenKind.StringLiteral) {
            expression = this.parseLiteral();
        } else if (t.code() instanceof Tokens.Type) {
            expression = this.parseTypeExpression();
        } else if (t.kind() == TokenKind.Identifier) {
            int tokenIndex = this.tokenIndex;
            if (this.peek2().code() == Tokens.Operator.Less) {
                this.tokenIndex += 2;
                this.skipToEndGreater();
            }
            if (!this.isEndOfFile() && this.peek2().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex = tokenIndex;
                expression = this.parseMethodCall();
            } else {
                this.tokenIndex = tokenIndex + 1;
                Position endPos = getPos();
                Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
                expression = new LCVariable(t.text(), pos);
            }
        } else if (t.code() == Tokens.Keyword.__Type__) {
            this.tokenIndex++;
            if (this.peek().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
            } else {
                // TODO dump error
            }
            expression = this.parseTypeExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                // TODO dump error
            }
        } else if (t.code() == Tokens.Keyword.Destructor) {
            expression = this.parseMethodCall();
        } else if (t.code() == Tokens.Keyword.Lambda) {
            expression = this.parseLambda();
        } else if (t.code() == Tokens.Keyword.Synchronized) {
            expression = this.parseSynchronized();
        } else if (t.code() == Tokens.Keyword.If) {
            expression = this.parseIf();
        } else if (t.code() == Tokens.Keyword.Switch) {
            expression = this.parseSwitchExpression();
        } else if (t.code() == Tokens.Separator.OpenBrace) {
            expression = this.parseMethodBlock();
        } else if (t.code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;
            expression = this.parseExpression();
            Token t1 = this.peek();
            if (t1.code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                expression.isErrorNode = true;
                System.err.println("Expecting a ')' at the end of a primary LCExpression, while we got a " + t.text());
                System.err.println(t.position());
                this.skip();
            }
        } else if (t.code() == Tokens.Operator.Multiply) {
            this.tokenIndex++;
            LCExpression theExpression = this.parseExpression();
            Position endPos = this.getLastPos();
            Position pos = new Position(t.position().beginPos(), endPos.endPos(), t.position().beginLine(), endPos.endLine(), t.position().beginCol(), endPos.endCol());
            expression = new LCDereference(theExpression, pos, false);
        } else if (t.code() == Tokens.Keyword.New) {
            this.tokenIndex++;
            boolean isErrorNode = false;
            LCExpression place;
            if (this.peek().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
                place = this.parseExpression();
                if (this.peek().code() == Tokens.Separator.CloseParen) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }
            } else {
                place = null;
            }

            LCTypeExpression lcTypeExpression = this.parseTypeExpression(true);
            Token t1 = this.peek();
            if (lcTypeExpression != null) {
                if (t1.code() == Tokens.Separator.OpenParen) {
                    if (lcTypeExpression instanceof LCPredefinedTypeExpression) {
                        this.tokenIndex++;
                        Position pos = new Position(t.position().beginPos(), lcTypeExpression.position.endPos(), t.position().beginLine(), lcTypeExpression.position.endLine(), t.position().beginLine(), lcTypeExpression.position.endCol());
                        expression = new LCErrorExpression(pos);
                        this.skip();
                    } else {
                        if (this.peek().code() == Tokens.Separator.OpenParen) {
                            this.tokenIndex++;
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                            this.skip();
                        }
                        ArrayList<LCExpression> params = new ArrayList<>();
                        Token t2 = this.peek();
                        while (t2.code() != Tokens.Separator.CloseParen && t2.kind() != TokenKind.EOF) {
                            LCExpression exp = this.parseExpression();
                            params.add(exp);
                            if (exp != null && exp.isErrorNode) {
//                this.addError("Error parsing parameter for method call "+name, this.scanner.getLastPos());
                            }
                            t2 = this.peek();
                            if (t2.code() == Tokens.Separator.Comma) {
                                tokenIndex++;
                                t2 = this.peek();
                            } else if (t2.code() != Tokens.Separator.CloseParen) {
                                System.err.println("Expecting a comma at the end of a parameter, while we got a " + t1.text());
                                this.skip();
                                break;
                            }
                        }
                        if (this.peek().code() == Tokens.Separator.CloseParen) {
                            this.tokenIndex++;
                        } else {
                            isErrorNode = true;
                            // TODO dump error
                            this.skip();
                        }
                        if (this.peek().code() == Tokens.Separator.OpenBrace) {
                            if (lcTypeExpression instanceof LCTypeReferenceExpression typeReferenceExpression) {
                                LCBlock body = this.parseClassBody();
                                try {
                                    List<LCClassDeclaration> anonymousClassDeclarations = this.anonymousClassDeclarationsStack.peek();
                                    String anonymousClassName = "<class_" + anonymousClassDeclarations.size() + ">";
                                    LCClassDeclaration classDeclaration = new LCClassDeclaration(anonymousClassName, new LCTypeParameter[0], typeReferenceExpression.clone(), new LCTypeReferenceExpression[0], new LCTypeReferenceExpression[0], null, body, body.position.clone(), false);
                                    classDeclaration.setModifier(new LCModifier(Position.origin));
                                    classDeclaration.setAnnotations(new LCAnnotationDeclaration.LCAnnotation[0]);
                                    anonymousClassDeclarations.add(classDeclaration);
                                    lcTypeExpression = new LCTypeReferenceExpression(anonymousClassName, classDeclaration.position.clone(), false);
                                } catch (CloneNotSupportedException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                isErrorNode = true;
                                // TODO dump error
                            }
                        }
                        Position endPosition = this.getPos();
                        Position pos = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                        expression = new LCNewObject(place, lcTypeExpression, params.toArray(new LCExpression[0]), pos, isErrorNode);
                    }
                } else if (t1.code() == Tokens.Separator.OpenBracket) {
                    ArrayList<LCExpression> dimensions = new ArrayList<>();
                    while (this.peek().code() == Tokens.Separator.OpenBracket) {
                        this.tokenIndex++;
                        if (this.peek().code() != Tokens.Separator.CloseBracket) {
                            dimensions.add(this.parseExpression());
                            if (this.peek().code() == Tokens.Separator.CloseBracket) {
                                this.tokenIndex++;
                            } else {
                                isErrorNode = true;
                                // TODO dump error
                                this.skip();
                            }
                        } else {
                            dimensions.add(null);
                            this.tokenIndex++;
                        }
                    }
                    List<LCExpression> elements;
                    if (this.peek().code() == Tokens.Separator.OpenBrace) {
                        this.tokenIndex++;
                        elements = new ArrayList<>();
                        Token t2 = this.peek();
                        while (t2.code() != Tokens.Separator.CloseBrace) {
                            elements.add(this.parseExpression());
                            t2 = this.peek();
                            if (t2.code() == Tokens.Separator.Comma) {
                                this.tokenIndex++;
                            } else if (t2.code() != Tokens.Separator.CloseBrace) {
                                isErrorNode = true;
                                // TODO dump error
                                this.skip();
                            }
                        }
                        this.tokenIndex++;
                    } else {
                        elements = null;
                    }
                    Position endPosition = this.getPos();
                    Position position = new Position(t.position().beginPos(), endPosition.endPos(), t.position().beginLine(), endPosition.endLine(), t.position().beginCol(), endPosition.endCol());
                    expression = new LCNewArray(place, lcTypeExpression, dimensions.toArray(new LCExpression[0]), elements != null ? elements.toArray(new LCExpression[0]) : null, position, isErrorNode);
                } else {
                    Position pos = new Position(t.position().beginPos(), lcTypeExpression.position.endPos(), t.position().beginLine(), lcTypeExpression.position.endLine(), t.position().beginLine(), lcTypeExpression.position.endCol());
                    expression = new LCErrorExpression(pos);
                    this.skip();
                }
            } else {
//                this.addError("Invalid syntax to 'New' an object",t1.pos);
                Position endPos = this.getPos();
                Position pos = new Position(t.position().beginPos(), endPos.endPos(), t.position().beginLine(), endPos.endLine(), t.position().beginLine(), endPos.endCol());
                expression = new LCErrorExpression(pos);
                this.skip();
            }
        } else if (t.code() == Tokens.Keyword.Delete) {
            this.tokenIndex++;
            LCExpression exp1 = this.parseExpression();
            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCDelete(exp1, pos, false);
        } else if (t.code() == Tokens.Keyword.This) {
            if (this.peek2().code() == Tokens.Separator.OpenParen) {
                LCMethodCall lcMethodCall = this.parseMethodCall();
                lcMethodCall.name = "<init>";
                expression = new LCBinary(Tokens.Operator.Dot, new LCThis(t.position()), lcMethodCall, false);
            } else {
                this.tokenIndex++;
                expression = new LCThis(t.position());
            }
        } else if (t.code() == Tokens.Keyword.Super) {
            if (this.peek2().code() == Tokens.Separator.OpenParen) {
                LCMethodCall lcMethodCall = this.parseMethodCall();
                lcMethodCall.name = "<init>";
                expression = new LCBinary(Tokens.Operator.Dot, new LCSuper(t.position()), lcMethodCall, false);
            } else {
                this.tokenIndex++;
                expression = new LCSuper(t.position());
            }
        } else if (t.code() == Tokens.Keyword.Clone) {
            this.tokenIndex++;
            LCExpression theExpression = parseExpression();
            Position endPos = this.getLastPos();
            Position pos = new Position(t.position().beginPos(), endPos.endPos(), t.position().beginLine(), endPos.endLine(), t.position().beginCol(), endPos.endCol());
            expression = new LCClone(theExpression, pos);
        } else if (t.code() == Tokens.Keyword.Typeof) {
            this.tokenIndex++;
            LCExpression exp = this.parseExpression();
            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCTypeof(exp, pos);
        } else if (t.code() == Tokens.Keyword.Sizeof) {
            this.tokenIndex++;
            boolean isErrorNode = false;
            LCExpression exp;
            if (this.peek().code() == Tokens.Operator.Less) {
                this.tokenIndex++;
                exp = this.parseTypeExpression();
                if (this.peek().code() == Tokens.Operator.Greater) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }
            } else {
                exp = this.parseExpression();
            }
            Position endPos = this.getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCSizeof(exp, pos, isErrorNode);
        } else if (t.code() == Tokens.Operator.BitAnd) {
            this.tokenIndex++;
            LCExpression exp1 = this.parseExpression();
            Position endPos = this.getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCGetAddress(exp1, pos, false);
        } else if (t.code() == Tokens.Keyword.Malloc) {
            this.tokenIndex++;
            LCExpression size = this.parseExpression();
            Position endPos = this.getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCMalloc(size, pos, false);
        } else if (t.code() == Tokens.Keyword.Free) {
            this.tokenIndex++;
            LCExpression expression1 = this.parseExpression();
            Position endPos = this.getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCFree(expression1, pos, false);
        } else if (t.code() == Tokens.Keyword.Realloc) {
            this.tokenIndex++;
            boolean isErrorNode = false;
            LCExpression exp = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.Comma) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
            LCExpression size = this.parseExpression();
            Position endPos = this.getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCRealloc(exp, size, pos, isErrorNode);
        } else if (t.code() == Tokens.Keyword.Static_cast) {
            this.tokenIndex++;
            boolean isErrorNode = false;

            if (this.peek().code() == Tokens.Operator.Less) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '<' after 'static_cast'.");
                this.skip();
            }
            LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
            if (this.peek().code() == Tokens.Operator.Greater) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '>' after the type expression in the static_cast expression.");
                this.skip();
            }

            if (this.peek().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '(' after '>' in the static_cast expression.");
                this.skip();
            }
            LCExpression expression2 = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after the LCExpression in the static_cast LCExpression.");
                this.skip();
            }

            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCTypeCast(LCTypeCast.Kind.STATIC, lcTypeExpression, expression2, pos, isErrorNode);
        } else if (t.code() == Tokens.Keyword.Dynamic_cast) {
            this.tokenIndex++;
            boolean isErrorNode = false;

            if (this.peek().code() == Tokens.Operator.Less) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '<' after 'dynamic_cast'.");
                this.skip();
            }
            LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
            if (this.peek().code() == Tokens.Operator.Greater) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '>' after the type LCExpression in the dynamic_cast LCExpression.");
                this.skip();
            }

            if (this.peek().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '(' after '>' in the dynamic_cast LCExpression.");
                this.skip();
            }
            LCExpression expression2 = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after the LCExpression in the dynamic_cast LCExpression.");
                this.skip();
            }

            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCTypeCast(LCTypeCast.Kind.DYNAMIC, lcTypeExpression, expression2, pos, isErrorNode);
        } else if (t.code() == Tokens.Keyword.Reinterpret_cast) {
            this.tokenIndex++;
            boolean isErrorNode = false;

            if (this.peek().code() == Tokens.Operator.Less) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '<' after 'reinterpret_cast'.");
                this.skip();
            }
            LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
            if (this.peek().code() == Tokens.Operator.Greater) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '>' after the type LCExpression in the reinterpret_cast LCExpression.");
                this.skip();
            }

            if (this.peek().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '(' after '>' in the reinterpret_cast LCExpression.");
                this.skip();
            }
            LCExpression expression2 = this.parseExpression();
            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after the LCExpression in the reinterpret_cast LCExpression.");
                this.skip();
            }

            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCTypeCast(LCTypeCast.Kind.REINTERPRET, lcTypeExpression, expression2, pos, isErrorNode);
        } else if (t.code() == Tokens.Keyword.Classof) {
            this.tokenIndex++;
            LCTypeExpression typeExpression = this.parseTypeExpression();
            boolean isErrorNode;
            LCTypeReferenceExpression typeReferenceExpression;
            if (typeExpression instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                typeReferenceExpression = lcTypeReferenceExpression;
                isErrorNode = false;
            } else {
                typeReferenceExpression = null;
                isErrorNode = true;
                // TODO dump error
            }
            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCClassof(typeReferenceExpression, pos, isErrorNode);
        } else if (t.code() == Tokens.Keyword.Method_address_of) {
            this.tokenIndex++;
            boolean isErrorNode = false;

            if (this.peek().code() == Tokens.Operator.Less) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '<' after 'method_address_of'.");
                this.skip();
            }
            LCTypeExpression lcTypeExpression = this.parseTypeExpression(false);
            if (this.peek().code() == Tokens.Operator.Greater) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '>' after the type expression in the method_address_of expression.");
                this.skip();
            }

            if (this.peek().code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting '(' after '>' in the method_address_of expression.");
                this.skip();
            }

            Token name = this.next();
            if (name.kind() != TokenKind.Identifier) {
                isErrorNode = true;
                // TODO dump error
            }
            ArrayList<LCTypeExpression> paramTypeExpressions = new ArrayList<>();
            Token t2 = this.peek();
            if (t2.code() == Tokens.Separator.OpenParen) {
                this.tokenIndex++;
                t2 = this.peek();
                while (t2.code() != Tokens.Separator.CloseParen && t2.kind() != TokenKind.EOF) {
                    LCTypeExpression typeExpression = this.parseTypeExpression(false);
                    if (typeExpression != null) {
                        paramTypeExpressions.add(typeExpression);
                    } else {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }

                    t2 = this.peek();
                    if (t2.code() == Tokens.Separator.Comma) {
                        this.tokenIndex++;
                    } else if (t2.code() != Tokens.Separator.CloseParen && t2.kind() != TokenKind.EOF) {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }

                    t2 = this.peek();
                }

                if (t2.code() == Tokens.Separator.CloseParen) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }

            if (this.peek().code() == Tokens.Separator.CloseParen) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                System.err.println("Expecting ')' after the expression in the method_address_of expression.");
                this.skip();
            }

            Position endPosition = this.getPos();
            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
            expression = new LCGetAddress(lcTypeExpression, name.text(), paramTypeExpressions, position, isErrorNode);
        } else if (t.code() == Tokens.Keyword.__Platform__) {
            this.tokenIndex++;
            expression = new LCPlatform(this.options.getStringVar("platform"), t.position(), false);
        } else if (t.code() == Tokens.Keyword.__Field__) {
            this.tokenIndex++;
            expression = new LCField(t.position(), false);
        } else {
//            this.addError("Can not recognize a primary LCExpression starting with: " + t.text(), this.scanner.getLastPos());
            Position endPos = getPos();
            Position pos = new Position(beginPosition.beginPos(), endPos.endPos(), beginPosition.beginLine(), endPos.endLine(), beginPosition.beginCol(), endPos.endCol());
            expression = new LCErrorExpression(pos);
        }

        t = this.peek();

        loop:
        while (t.code() == Tokens.Separator.OpenParen || t.code() == Tokens.Separator.OpenBracket || t.code() == Tokens.Operator.QuestionMark) {
            switch (t.code()) {
                case Tokens.Separator.OpenParen -> {
                    this.tokenIndex++;
                    boolean isErrorNode = false;
                    ArrayList<LCExpression> params = new ArrayList<>();
                    Token t1 = this.peek();
                    while (t1.code() != Tokens.Separator.CloseParen && t1.kind() != TokenKind.EOF) {
                        LCExpression argument = this.parseExpression();
                        params.add(argument);

                        if (argument != null && argument.isErrorNode) {
                            System.err.println("Error parsing parameter for expression method call.");
                        }

                        t1 = this.peek();
                        if (t1.code() != Tokens.Separator.CloseParen) {
                            if (t1.code() == Tokens.Separator.Comma) {
                                t1 = this.next();
                            } else {
//                    this.addError("Expecting a comma at the end of a parameter, while we got a " + t1.text(), this.scanner.getLastPos());
                                this.skip();
                                Position endPos = this.getPos();
                                Position pos = new Position(expression.position.beginPos(), endPos.endPos(), expression.position.beginLine(), endPos.endLine(), expression.position.beginCol(), endPos.endCol());
                                expression = new LCMethodCall(expression, params.toArray(new LCExpression[0]), pos, true);
                                break;
                            }
                        }
                    }

                    t1 = this.peek();
                    if (t1.code() == Tokens.Separator.CloseParen) {
                        this.tokenIndex++;
                    } else {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }

                    Position endPos = this.getPos();
                    Position position = new Position(expression.position.beginPos(), endPos.endPos(), expression.position.beginLine(), endPos.endLine(), expression.position.beginCol(), endPos.endCol());
                    expression = new LCMethodCall(expression, params.toArray(new LCExpression[0]), position, isErrorNode);
                }
                case Tokens.Separator.OpenBracket -> {
                    this.tokenIndex++;
                    LCExpression expression1 = this.parseExpression();
                    boolean isErrorNode = (this.peek().code() != Tokens.Separator.CloseBracket);
                    if (isErrorNode) {
                        System.err.println("Expecting ']' when parsing ArrayType.");
                        expression.isErrorNode = true;
                        this.skip();
                        break loop;
                    } else {
                        this.tokenIndex++;
                        Position endPosition = this.getPos();
                        Position position = new Position(expression.position.beginPos(), endPosition.endPos(), expression.position.beginLine(), endPosition.endLine(), expression.position.beginCol(), endPosition.endCol());
                        expression = new LCArrayAccess(expression, expression1, position);
                    }
                }
                case Tokens.Operator.QuestionMark -> {
                    this.tokenIndex++;

                    boolean isErrorNode = false;
                    LCExpression then = this.parseExpression();

                    Token t1 = this.peek();
                    if (t1.code() == Tokens.Separator.Colon) {
                        this.tokenIndex++;
                    } else {
                        isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }

                    LCExpression _else = this.parseExpression();

                    Position endPosition = this.getPos();
                    Position position = new Position(expression.position.beginPos(), endPosition.endPos(), expression.position.beginLine(), endPosition.endLine(), expression.position.beginCol(), endPosition.endCol());
                    expression = new LCTernary(expression, then, _else, position, isErrorNode);
                }
                default -> throw new IllegalStateException("Unexpected value: " + t.code());
            }
            t = this.peek();
        }

        return expression;
    }

    private LCLambda parseLambda() {
        Position beginPosition = this.getPos();
        boolean isErrorNode = false;

        this.tokenIndex++;

        LCTypeParameter[] typeParameters;
        if (this.peek().code() == Tokens.Operator.Less) {
            typeParameters = this.parseGenericParameters();
        } else {
            typeParameters = new LCTypeParameter[0];
        }
        LCMethodDeclaration.LCCallSignature callSignature;
        Token t1 = this.peek();
        if (t1.code() == Tokens.Separator.OpenParen) {
            callSignature = this.parseCallSignature();
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, getPos(), 12);
            this.skip();
            Position endPosition = getPos();
            Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
            callSignature = new LCMethodDeclaration.LCCallSignature(null, position, true);
        }
        long initialFlags = 0;
        if (this.peek().code() == Tokens.Keyword.Const) {
            this.tokenIndex++;
            initialFlags |= LCFlags.THIS_CONST;
        }
        if (this.peek().code() == Tokens.Keyword.Readonly) {
            this.tokenIndex++;
            initialFlags |= LCFlags.THIS_READONLY;
        }
        if (this.peek().code() == Tokens.Keyword.Final) {
            this.tokenIndex++;
            initialFlags |= LCFlags.THIS_FINAL;
        }
        LCTypeExpression returnTypeExpression;
        if (this.peek().code() == Tokens.Separator.Colon) {
            this.tokenIndex++;
            returnTypeExpression = this.parseTypeExpression();
        } else {
            returnTypeExpression = null;
        }
        ArrayList<LCTypeReferenceExpression> throwsExceptions = new ArrayList<>();
        if (this.peek().code() == Tokens.Keyword.Throws) {
            this.tokenIndex++;
            Token t3 = this.peek();
            while (t3.kind() != TokenKind.EOF) {
                LCTypeExpression te = this.parseTypeExpression();
                if (te instanceof LCTypeReferenceExpression lcTypeReferenceExpression) {
                    throwsExceptions.add(lcTypeReferenceExpression);
                } else {
                    isErrorNode = true;
                    // TODO dump error
                }

                if (this.peek().code() == Tokens.Separator.Comma) {
                    this.tokenIndex++;
                } else {
                    break;
                }

                t3 = this.peek();
            }
        }

        if (this.peek().code() == Tokens.Operator.Arrow) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;

        }

        LCStatement body = this.parseMethodBlockStatement();

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCLambda(typeParameters, callSignature, returnTypeExpression, initialFlags, throwsExceptions.toArray(new LCTypeReferenceExpression[0]), body, position, isErrorNode);
    }

    private LCSwitchStatement parseSwitchStatement() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        Token t = this.peek();
        if (t.code() == Tokens.Separator.OpenParen) {  //'('
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        LCExpression selector = this.parseExpression();

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseParen) {  //')'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        t = this.peek();
        if (t.code() == Tokens.Separator.OpenBrace) {  //'{'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, t.position(), 8);
            this.skip();
        }

        LCCase[] cases = this.parseCases(false);

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {  //'}'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, t.position(), 9);
            this.skip();
        }

        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCSwitchStatement(selector, cases, position, isErrorNode);
    }

    private LCSwitchExpression parseSwitchExpression() {
        Position beginPosition = this.getPos();

        boolean isErrorNode = false;

        this.tokenIndex++;

        Token t = this.peek();
        if (t.code() == Tokens.Separator.OpenParen) {  //'('
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        LCExpression selector = this.parseExpression();

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseParen) {  //')'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        t = this.peek();
        if (t.code() == Tokens.Separator.OpenBrace) {  //'{'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, t.position(), 8);
            this.skip();
        }

        LCCase[] cases = this.parseCases(true);

        t = this.peek();
        if (t.code() == Tokens.Separator.CloseBrace) {  //'}'
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            this.errorStream.printError(true, t.position(), 9);
            this.skip();
        }


        Position endPosition = this.getPos();
        Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
        return new LCSwitchExpression(selector, cases, position, isErrorNode);
    }

    private LCCase[] parseCases(boolean isExpression) {
        ArrayList<LCCase> cases = new ArrayList<>();
        Token t = this.peek();
        while (t.code() == Tokens.Keyword.Case || t.code() == Tokens.Keyword.Default) {
            boolean isErrorNode = false;

            Position beginPosition = this.getPos();
            this.tokenIndex++;
            if (t.code() == Tokens.Keyword.Default) {
                Token t2 = this.peek();
                if (t2.code() == Tokens.Separator.Colon) {
                    this.tokenIndex++;
                    if (isExpression) {
                        isErrorNode = true;
                        // TODO dump error
                    }
                    ArrayList<LCStatement> statements = new ArrayList<>();
                    boolean completesNormally = false;
                    Token t3 = this.peek();
                    while (t3.code() != Tokens.Keyword.Case && t3.code() != Tokens.Keyword.Default && t3.code() != Tokens.Separator.CloseBrace) {
                        LCStatement statement = this.parseMethodBlockStatement();
                        statements.add(statement);
                        if (statement instanceof LCBreak) completesNormally = true;
                        t3 = this.peek();
                    }
                    Position endPosition = this.getPos();
                    Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                    cases.add(new LCCase(LCCase.LCCaseKind.STATEMENT, new LCCaseLabel[]{new LCCaseLabel.LCDefaultCaseLabel(t.position(), false)}, null, statements.toArray(new LCStatement[0]), completesNormally, position, isErrorNode));
                } else if (t2.code() == Tokens.Operator.Arrow) {
                    this.tokenIndex++;
                    LCStatement statement = this.parseMethodBlockStatement();
                    Position endPosition = this.getPos();
                    Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                    cases.add(new LCCase(LCCase.LCCaseKind.RULE, new LCCaseLabel[]{new LCCaseLabel.LCDefaultCaseLabel(t.position(), false)}, null, new LCStatement[]{statement}, true, position, isErrorNode));
                }
            } else {
                ArrayList<LCCaseLabel> labels = new ArrayList<>();
                while (true) {
                    Position label_beginPosition = this.getPos();
                    LCExpression expression = this.parseExpression();
                    Position label_endPosition = this.getPos();
                    Position label_position = new Position(label_beginPosition.beginPos(), label_endPosition.endPos(), label_beginPosition.beginLine(), label_endPosition.endLine(), label_beginPosition.beginCol(), label_endPosition.endCol());
                    labels.add(expression instanceof LCTypeExpression typeExpression ? new LCCaseLabel.LCTypeCaseLabel(typeExpression, label_position, typeExpression.isErrorNode) : new LCCaseLabel.LCConstantCaseLabel(expression, label_position, expression.isErrorNode));
                    if (this.peek().code() == Tokens.Separator.Comma) {
                        this.tokenIndex++;
                    } else {
                        break;
                    }
                }

                LCExpression guard;
                if (this.peek().code() == Tokens.Keyword.When) {
                    this.tokenIndex++;
                    guard = this.parseExpression();
                } else {
                    guard = null;
                }

                Token t2 = this.peek();
                if (t2.code() == Tokens.Separator.Colon) {
                    this.tokenIndex++;
                    if (isExpression) {
                        isErrorNode = true;
                        // TODO dump error
                    }
                    ArrayList<LCStatement> statements = new ArrayList<>();
                    boolean completesNormally = false;
                    Token t3 = this.peek();
                    while (t3.code() != Tokens.Keyword.Case && t3.code() != Tokens.Keyword.Default && t3.code() != Tokens.Separator.CloseBrace) {
                        LCStatement statement = this.parseMethodBlockStatement();
                        statements.add(statement);
                        if (statement instanceof LCBreak) completesNormally = true;
                        t3 = this.peek();
                    }
                    Position endPosition = this.getPos();
                    Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                    cases.add(new LCCase(LCCase.LCCaseKind.STATEMENT, labels.toArray(new LCCaseLabel[0]), guard, statements.toArray(new LCStatement[0]), completesNormally, position, isErrorNode));
                } else if (t2.code() == Tokens.Operator.Arrow) {
                    this.tokenIndex++;
                    LCStatement statement = this.parseMethodBlockStatement();
                    Position endPosition = this.getPos();
                    Position position = new Position(beginPosition.beginPos(), endPosition.endPos(), beginPosition.beginLine(), endPosition.endLine(), beginPosition.beginCol(), endPosition.endCol());
                    cases.add(new LCCase(LCCase.LCCaseKind.RULE, labels.toArray(new LCCaseLabel[0]), guard, new LCStatement[]{statement}, true, position, isErrorNode));
                }
            }
            t = this.peek();
        }
        return cases.toArray(new LCCase[0]);
    }

    private LCMethodCall parseMethodCall() {
        Position beginPos = this.getPos();
        boolean isErrorNode = false;
        ArrayList<LCTypeExpression> typeArguments = new ArrayList<>();
        ArrayList<LCExpression> params = new ArrayList<>();

        Token tName = this.next();
        String name;
        switch (tName.code()) {
            case Tokens.Keyword.This -> name = "<init>";
            case Tokens.Keyword.Super -> name = "<super>";
            case Tokens.Keyword.Destructor -> name = "<deinit>";
            case null, default -> {
                name = tName.text();
                if (tName.kind() != TokenKind.Identifier) {
                    isErrorNode = true;
                    // TODO dump error
                }
            }
        }

        if (this.peek().code() == Tokens.Operator.Less) {
            this.tokenIndex++;
            Token t = this.peek();
            while (t.kind() != TokenKind.EOF && t.code() != Tokens.Operator.Greater) {
                LCTypeExpression typeExpression = this.parseTypeExpression();
                typeArguments.add(typeExpression);
                t = this.peek();
            }
            if (t.code() == Tokens.Operator.Greater) {
                this.tokenIndex++;
            } else {
                isErrorNode = true;
                // TODO dump error
                this.skip();
            }
        }

        this.tokenIndex++;

        Token t1 = this.peek();
        while (t1.code() != Tokens.Separator.CloseParen && t1.kind() != TokenKind.EOF) {
            LCExpression expression = this.parseExpression();
            params.add(expression);

            if (expression != null && expression.isErrorNode) {
                System.err.println("Error parsing parameter for method call " + name);
            }

            t1 = this.peek();
            if (t1.code() != Tokens.Separator.CloseParen) {
                if (t1.code() == Tokens.Separator.Comma) {
                    t1 = this.next();
                } else {
//                    this.addError("Expecting a comma at the end of a parameter, while we got a " + t1.text(), this.scanner.getLastPos());
                    this.skip();
                    Position endPos = getPos();
                    Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                    return new LCMethodCall(name, tName.position(), typeArguments.toArray(new LCTypeExpression[0]), params.toArray(new LCExpression[0]), pos, true);
                }
            }
        }

        t1 = this.peek();
        if (t1.code() == Tokens.Separator.CloseParen) {
            this.tokenIndex++;
        } else {
            isErrorNode = true;
            // TODO dump error
            this.skip();
        }

        Position endPos = getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCMethodCall(name, tName.position(), typeArguments.toArray(new LCTypeExpression[0]), params.toArray(new LCExpression[0]), pos, isErrorNode);
    }

    private int getPrec(Tokens.TokenCode op) {
        return switch (op) {
            case Tokens.Operator.Assign, Tokens.Operator.PlusAssign, Tokens.Operator.MinusAssign,
                 Tokens.Operator.MultiplyAssign, Tokens.Operator.DivideAssign, Tokens.Operator.ModulusAssign,
                 Tokens.Operator.BitAndAssign,
                 Tokens.Operator.BitOrAssign, Tokens.Operator.BitXorAssign, Tokens.Operator.LeftShiftArithmeticAssign,
                 Tokens.Operator.RightShiftArithmeticAssign,
                 Tokens.Operator.RightShiftLogicalAssign -> 2;
            case Tokens.Operator.Or -> 4;
            case Tokens.Operator.And -> 5;
            case Tokens.Operator.BitOr -> 6;
            case Tokens.Operator.BitXor -> 7;
            case Tokens.Operator.BitAnd -> 8;
            case Tokens.Operator.Equal, Tokens.Operator.NotEqual -> 9;
            case Tokens.Operator.Greater, Tokens.Operator.GreaterEqual, Tokens.Operator.LessEqual,
                 Tokens.Operator.Less -> 10;
            case Tokens.Keyword.Is, Tokens.Keyword.In, Tokens.Keyword.Instanceof -> 11;
            case Tokens.Operator.LeftShiftArithmetic, Tokens.Operator.RightShiftArithmetic,
                 Tokens.Operator.RightShiftLogical -> 12;
            case Tokens.Operator.Elvis -> 13;
            case Tokens.Operator.Plus, Tokens.Operator.Minus -> 14;
            case Tokens.Operator.Divide, Tokens.Operator.Multiply, Tokens.Operator.Modulus -> 15;
            case Tokens.Operator.Dot, Tokens.Operator.MemberAccess, Tokens.Operator.QuestionMarkDot -> 16;
            default -> -1;
        };
    }

    private LCTypeExpression parseTypeExpression() {
        return this.parseTypeExpression(false);
    }

    private LCTypeExpression parseTypeExpression(boolean isNewExpression) {
        Position beginPos = this.getPos();
        Token t = this.peek();

        LCTypeExpression type;

        if (t.code() == Tokens.Type.Auto) {
            this.tokenIndex++;
            type = new LCAutoTypeExpression(t.position(), false);
        } else if (t.kind() == TokenKind.Identifier) {
            this.tokenIndex++;
            StringBuilder s = new StringBuilder(t.text());
            while (this.peek().kind() != TokenKind.EOF && this.peek().code() == Tokens.Operator.Dot) {
                s.append(".");
                this.tokenIndex++;
                t = this.peek();
                if (t.kind() == TokenKind.Identifier) {
                    s.append(t.text());
                    this.tokenIndex++;
                } else {
                    this.skip();
                }
            }

            Position endPos = this.getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            type = new LCTypeReferenceExpression(s.toString(), pos);
        } else if (!isNewExpression && t.code() == Tokens.Separator.OpenParen) {
            this.tokenIndex++;

            int tokenIndex = this.tokenIndex;
            int closeParenIndex = this.skipToEndCloseParen();
            this.tokenIndex = tokenIndex;
            if (this.tokens[closeParenIndex + 1].code() == Tokens.Separator.Colon) {
                LCParameterList parameterList = this.parseParameterList(true);
                boolean isErrorNode = (this.peek().code() != Tokens.Separator.CloseParen);
                if (isErrorNode) {
//                this.addError("Expecting ')' when parsing ParenthesizedPrimType.", this.scanner.getLastPos());
                    this.skip();
                } else {
                    this.tokenIndex++;
                }

                this.tokenIndex++;
                LCTypeExpression returnTypeExpression = this.parseTypeExpression();

                Position endPosition = this.getPos();
                Position position = new Position(beginPos.beginPos(), endPosition.endPos(), beginPos.beginLine(), endPosition.endLine(), beginPos.beginCol(), endPosition.endCol());
                type = new LCMethodPointerTypeExpression(parameterList, returnTypeExpression, position, isErrorNode);
            } else {
                LCTypeExpression typeExpression = this.parseTypeExpression();
                if (typeExpression == null) return null;

                boolean isErrorNode = (this.peek().code() != Tokens.Separator.CloseParen);
                if (isErrorNode) {
//                this.addError("Expecting ')' when parsing ParenthesizedPrimType.", this.scanner.getLastPos());
                    this.skip();
                } else {
                    this.tokenIndex++;
                }
                Position endPosition = this.getPos();
                Position position = new Position(beginPos.beginPos(), endPosition.endPos(), beginPos.beginLine(), endPosition.endLine(), beginPos.beginCol(), endPosition.endCol());
                type = new LCParenthesizedTypeExpression(typeExpression, position, isErrorNode);
            }
        } else if (Token.isBaseType(t) || t.code() == Tokens.Type.Void) {
            this.tokenIndex++;
            Position endPos = getPos();
            Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
            type = new LCPredefinedTypeExpression((Tokens.Type) t.code(), pos);
        } else {
            //                this.addError("Unsupported type LCExpression: " + t.text(), t.pos);
            type = null;
            this.tokenIndex++;
        }

        if (type != null) {
            t = this.peek();
            loop:
            while (t.code() == Tokens.Operator.Multiply || t.code() == Tokens.Operator.BitAnd || t.code() == Tokens.Operator.QuestionMark || t.code() == Tokens.Separator.OpenBracket) {
                this.tokenIndex++;
                switch (t.code()) {
                    case Tokens.Operator.Multiply -> {
                        Position endPos = getPos();
                        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                        type = new LCPointerTypeExpression(type, pos);
                    }
                    case Tokens.Operator.BitAnd -> {
                        Position endPos = this.getPos();
                        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                        type = new LCReferenceTypeExpression(type, pos);
                    }
                    case Tokens.Operator.QuestionMark -> {
                        Position endPosition = this.getPos();
                        Position position = new Position(beginPos.beginPos(), endPosition.endPos(), beginPos.beginLine(), endPosition.endLine(), beginPos.beginCol(), endPosition.endCol());
                        type = new LCNullableTypeExpression(type, position, type.isErrorNode);
                    }
                    case Tokens.Separator.OpenBracket -> {
                        if (isNewExpression) {
                            this.tokenIndex--;
                            break loop;
                        } else {
                            boolean isErrorNode = (this.peek().code() != Tokens.Separator.CloseBracket);
                            if (isErrorNode) {
                                type.isErrorNode = true;
                                System.err.println("Expecting ']' when parsing ArrayType.");
                                this.skip();
                                break loop;
                            } else {
                                this.tokenIndex++;
                                Position endPos = getPos();
                                Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
                                type = new LCArrayTypeExpression(type, pos);
                            }

                        }
                    }
                    default -> throw new IllegalStateException("Unexpected token: '" + t.text() + "'");
                }
                t = this.peek();
            }
            if (type instanceof LCTypeReferenceExpression typeReferenceExpression && this.peek().code() == Tokens.Operator.Less) {
                this.tokenIndex++;
                ArrayList<LCTypeExpression> typeArgs = new ArrayList<>();
                Token t1 = this.peek();
                while (t1.kind() != TokenKind.EOF && t1.code() != Tokens.Operator.Greater) {
                    typeArgs.add(this.parseTypeExpression());
                    t1 = this.peek();
                    if (t1.code() == Tokens.Separator.Comma) {
                        this.tokenIndex++;
                    } else if (t1.code() != Tokens.Operator.Greater) {
                        type.isErrorNode = true;
                        // TODO dump error
                        this.skip();
                    }
                }
                if (this.peek().code() == Tokens.Operator.Greater) {
                    this.tokenIndex++;
                } else {
                    type.isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }
                if (!typeArgs.isEmpty()) typeReferenceExpression.setTypeArgs(typeArgs.toArray(new LCTypeExpression[0]));
            }
        }

        return type;
    }

    private LCLiteral<?> parseLiteral() {
        Token t = this.peek();

        if (t.kind() == TokenKind.IntegerLiteral) {
            this.tokenIndex++;
            return this.parseIntegerLiteral(t);
        } else if (t.kind() == TokenKind.DecimalLiteral) {
            this.tokenIndex++;
            return this.parseDecimalLiteral(t);
        } else if (t.kind() == TokenKind.NullLiteral) {
            this.tokenIndex++;
            return new LCNullLiteral(t.position());
        } else if (t.kind() == TokenKind.NullptrLiteral) {
            this.tokenIndex++;
            return new LCNullptrLiteral(t.position());
        } else if (t.kind() == TokenKind.BooleanLiteral) {
            this.tokenIndex++;
            return new LCBooleanLiteral(Boolean.parseBoolean(t.text()), t.position());
        } else if (t.kind() == TokenKind.CharLiteral) {
            this.tokenIndex++;
            return new LCCharLiteral(t.text().charAt(0), t.position());
        } else {
            this.tokenIndex++;
            return new LCStringLiteral(t.text(), t.position());
        }
    }

    private LCIntegerLiteral parseIntegerLiteral(Token t) {
        String s = t.text();
        int radix;
        if (s.startsWith("0x") || s.startsWith("0X")) {
            radix = 16;
            s = s.substring(2);
        } else if (s.startsWith("0b") || s.startsWith("0B")) {
            radix = 2;
            s = s.substring(2);
        } else if (s.equals("0L") || s.equals("0l")) {
            radix = 10;
        } else if (s.startsWith("0") && s.length() != 1) {
            radix = 8;
            s = s.substring(1);
        } else {
            radix = 10;
        }

        if (s.endsWith("L") || s.endsWith("l")) {
            return new LCIntegerLiteral(Long.parseLong(s.substring(0, s.length() - 1), radix), true, t.position());
        } else {
            return new LCIntegerLiteral(Long.parseLong(s, radix), false, t.position());
        }
    }

    private LCDecimalLiteral parseDecimalLiteral(Token t) {
        return new LCDecimalLiteral(Double.parseDouble(t.text()), t.text().endsWith("F") || t.text().endsWith("f"), t.position());
    }

    private LCModifier parseModifier() {
        Position beginPos = this.getPos();

        long flags = 0;
        ArrayList<String> attributes = new ArrayList<>();
        Long bitRange = null;
        boolean isErrorNode = false;

        Token t = this.peek();
        while (true) {
            if (t.code() == Tokens.Keyword.__Attribute__) {
                this.tokenIndex++;
                if (this.peek().code() == Tokens.Separator.OpenParen) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                Token attribute = this.peek();
                if (attribute.kind() == TokenKind.StringLiteral) {
                    this.tokenIndex++;
                    attributes.add(attribute.text());
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                if (this.peek().code() == Tokens.Separator.CloseParen) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                t = this.peek();
                continue;
            } else if (t.code() == Tokens.Keyword.Bit_range) {
                this.tokenIndex++;
                if (this.peek().code() == Tokens.Separator.OpenParen) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                if (bitRange != null) {
                    isErrorNode = true;
                    // TODO dump error
                }
                Token bitRangeToken = this.peek();
                if (bitRangeToken.kind() == TokenKind.IntegerLiteral) {
                    this.tokenIndex++;
                    bitRange = (Long) this.parseIntegerLiteral(bitRangeToken).value;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                if (this.peek().code() == Tokens.Separator.CloseParen) {
                    this.tokenIndex++;
                } else {
                    isErrorNode = true;
                    // TODO dump error
                    this.skip();
                }

                t = this.peek();
                continue;
            }
            long flag = switch (t.code()) {
                case Tokens.Keyword.Private -> LCFlags.PRIVATE;
                case Tokens.Keyword.Protected -> LCFlags.PROTECTED;
                case Tokens.Keyword.Public -> LCFlags.PUBLIC;
                case Tokens.Keyword.Static -> LCFlags.STATIC;
                case Tokens.Keyword.Const -> LCFlags.CONST;
                case Tokens.Keyword.Readonly -> LCFlags.READONLY;
                case Tokens.Keyword.Final -> LCFlags.FINAL;
                case Tokens.Keyword.Abstract -> LCFlags.ABSTRACT;
                case Tokens.Keyword.Override -> LCFlags.OVERRIDE;
                case Tokens.Keyword.Default -> LCFlags.DEFAULT;
                case Tokens.Keyword.Volatile -> LCFlags.VOLATILE;
                case Tokens.Keyword.Vararg -> LCFlags.VARARG;
                case Tokens.Keyword.Synchronized -> LCFlags.SYNCHRONIZED;
                case Tokens.Keyword.Operator -> LCFlags.OPERATOR;
                case Tokens.Keyword.Sealed -> LCFlags.SEALED;
                case Tokens.Keyword.Non_sealed -> LCFlags.NON_SEALED;
                case Tokens.Keyword.Internal -> LCFlags.INTERNAl;
                case Tokens.Keyword.Extern -> LCFlags.EXTERN;
                case Tokens.Keyword.Lateinit -> LCFlags.LATEINIT;
                default -> 0;
            };
            if (flag == 0) break;

            flags |= flag;

            this.tokenIndex++;
            t = this.peek();
        }

        Position endPos = this.getPos();
        Position pos = new Position(beginPos.beginPos(), endPos.endPos(), beginPos.beginLine(), endPos.endLine(), beginPos.beginCol(), endPos.endCol());
        return new LCModifier(flags, attributes.toArray(new String[0]), bitRange, pos, isErrorNode);
    }


    private boolean isEndOfFile() {
        return this.tokenIndex >= this.tokens.length || this.peek().kind() == TokenKind.EOF;
    }

    private Token next() {
        return this.tokens[this.tokenIndex++];
    }

    private Token peek() {
        return this.tokens[this.tokenIndex];
    }

    private Token peek2() {
        return this.tokens[this.tokenIndex + 1];
    }

    private Token peek3() {
        return this.tokens[this.tokenIndex + 2];
    }

    private Position getPos() {
        return this.tokens[this.tokenIndex].position();
    }

    private Position getLastPos() {
        return this.tokens[this.tokenIndex - 1].position();
    }

    private void skip() {
        this.skip(new String[0]);
    }

    private void skip(String[] separators) {
        this.tokenIndex++;

        Token t = peek();
        while (t.kind() != TokenKind.EOF) {
            if (t.kind() == TokenKind.Keyword || t.code() == Tokens.Separator.Comma || t.code() == Tokens.Separator.SemiColon
                    || t.code() == Tokens.Separator.OpenBrace || t.code() == Tokens.Separator.CloseBrace
                    || t.code() == Tokens.Separator.OpenParen || t.code() == Tokens.Separator.CloseParen) {
                return;
            } else {
                for (String separator : separators) if (t.text().equals(separator)) return;
                this.tokenIndex++;
                t = peek();
            }
        }
    }

    private int skipToEndCloseParen() {
        int openParenCount = 1;
        Token t = peek();
        while (t.kind() != TokenKind.EOF) {
            if (t.code() == Tokens.Separator.OpenParen) {
                openParenCount++;
            } else if (t.code() == Tokens.Separator.CloseParen) {
                openParenCount--;
                if (openParenCount == 0)
                    return this.tokenIndex;
            }
            this.tokenIndex++;
            t = this.peek();
        }
        return this.tokenIndex;
    }

    private int skipToEndGreater() {
        int lessCount = 1;
        Token t = peek();
        while (t.kind() != TokenKind.EOF) {
            if (t.code() == Tokens.Operator.Less) {
                lessCount++;
            } else if (t.code() == Tokens.Operator.Greater) {
                lessCount--;
                if (lessCount == 0)
                    return this.tokenIndex;
            }
            this.tokenIndex++;
            t = this.peek();
        }
        return this.tokenIndex;
    }
}