package ldk.l.lc.ast;

import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.ast.file.LCSourceFileProxy;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.LCImport;
import ldk.l.lc.semantic.types.ArrayType;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.MethodSymbol;
import ldk.l.lc.util.symbol.object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LCAstUtil {
    public static MethodSymbol getMainMethod(LCObjectDeclaration lcObjectDeclaration) {
        MethodSymbol methodSymbol = null;
        for (MethodSymbol method : Objects.requireNonNull(LCAstUtil.getObjectSymbol(lcObjectDeclaration)).getMethods()) {
            if (LCFlags.hasPublic(method.flags) && LCFlags.hasStatic(method.flags) && method.name.equals("main") && method.getNumParams() == 1
                    && (method.declaration.callSignature.parameterList.parameters[0].theType.equals(new ArrayType(SystemTypes.String_Type)))
                    && method.declaration.returnType.equals(SystemTypes.VOID)) {
                if (methodSymbol == null) {
                    methodSymbol = method;
                } else {
                    // TODO dump error
                }
            }
        }
        return methodSymbol;
    }

    public static LCObjectDeclaration[] getObjectDeclarationsByName(LCAst ast, LCObjectDeclaration objectDeclaration, String name) {
        ArrayList<LCObjectDeclaration> objectDeclarations = new ArrayList<>();

        return objectDeclarations.toArray(new LCObjectDeclaration[0]);
    }

    public static LCObjectDeclaration getObjectDeclarationByFullName(LCObjectDeclaration[] objectDeclarations, String _package, String name) {
        return LCAstUtil.getObjectDeclarationByFullName(objectDeclarations, _package + "." + name);
    }

    public static LCObjectDeclaration getObjectDeclarationByFullName(LCObjectDeclaration[] objectDeclarations, String fullName) {
        for (LCObjectDeclaration objectDeclaration : objectDeclarations) {
            if (objectDeclaration.getFullName().equals(fullName))
                return objectDeclaration;
        }
        return null;
    }

    public static LCImport[] getImportStatements(LCObjectDeclaration[] objectDeclarations, LCObjectDeclaration currentObjectDeclaration) {
        ArrayList<LCImport> lcImports = new ArrayList<>();
        for (LCObjectDeclaration objectDeclaration : objectDeclarations) {
            if (!objectDeclaration.equals(currentObjectDeclaration)) {
                if (objectDeclaration.parentNode != null && objectDeclaration.parentNode.equals(currentObjectDeclaration)) {
                    String fullName = objectDeclaration.getFullName();
                    lcImports.add(new LCImport(LCImport.LCImportKind.Normal, fullName, Position.origin, false));
                } else {
                    lcImports.add(new LCImport(LCImport.LCImportKind.Normal, objectDeclaration.getPackageName() + "." + objectDeclaration.name, Position.origin, false));
                }
            }
        }

        return lcImports.toArray(new LCImport[0]);
    }

    public static LCSourceFile getSourceFile(LCAstNode node) {
        while (node.parentNode != null) {
            node = node.parentNode;
            if (node instanceof LCSourceFile) return ((LCSourceFile) node);
        }
        return null;
    }

    public static LCObjectDeclaration getObjectDeclarationByName(LCAstNode node, String name) {
        LCSourceFile sourceFile = LCAstUtil.getSourceFile(node);
        if (sourceFile == null) return null;

        for (LCObjectDeclaration objectDeclaration : sourceFile.getObjectDeclarations()) {
            if (objectDeclaration.name.equals(name) && inCommonRange(objectDeclaration, node)) {
                return objectDeclaration;
            } else if (objectDeclaration.getRealName().equals(name)) {
                return objectDeclaration;
            } else if (objectDeclaration.getFullName().equals(name)) {
                return objectDeclaration;
            }
        }
        if (sourceFile instanceof LCSourceCodeFile lcSourceCodeFile) {
            for (LCSourceFileProxy lcSourceFileProxy : lcSourceCodeFile.proxies) {
                LCObjectDeclaration[] objectDeclarations = lcSourceFileProxy.sourceFile.getObjectDeclarations();
                for (LCObjectDeclaration objectDeclaration : objectDeclarations) {
                    if (objectDeclaration.getRealName().equals(name)) return objectDeclaration;
                    else if (objectDeclaration.getFullName().equals(name)) return objectDeclaration;
                }
            }
        }
        return null;
    }

    public static LCObjectDeclaration getObjectDeclarationByFullName(LCAstNode node, String fullName) {
        LCSourceFile sourceFile = LCAstUtil.getSourceFile(node);
        if (sourceFile == null) return null;
        for (LCObjectDeclaration objectDeclaration : sourceFile.getObjectDeclarations()) {
            if (objectDeclaration.getFullName().equals(fullName)) return objectDeclaration;
        }
        if (sourceFile instanceof LCSourceCodeFile lcSourceCodeFile) {
            for (LCSourceFileProxy lcSourceFileProxy : lcSourceCodeFile.proxies) {
                LCObjectDeclaration[] objectDeclarations = lcSourceFileProxy.sourceFile.getObjectDeclarations();
                for (LCObjectDeclaration objectDeclaration : objectDeclarations) {
                    if (objectDeclaration.getFullName().equals(fullName)) return objectDeclaration;
                }
            }
        }
        return null;
    }

    public static ObjectSymbol getObjectSymbol(LCObjectDeclaration lcObjectDeclaration) {
        return switch (lcObjectDeclaration) {
            case LCClassDeclaration lcClassDeclaration -> lcClassDeclaration.symbol;
            case LCInterfaceDeclaration lcInterfaceDeclaration -> lcInterfaceDeclaration.symbol;
            case LCEnumDeclaration lcEnumDeclaration -> lcEnumDeclaration.symbol;
            case LCRecordDeclaration lcRecordDeclaration -> lcRecordDeclaration.symbol;
            case LCAnnotationDeclaration lcAnnotationDeclaration -> lcAnnotationDeclaration.symbol;
        };
    }

    public static LCObjectDeclaration getObjectDeclaration(ObjectSymbol objectSymbol) {
        return switch (objectSymbol) {
            case ClassSymbol classSymbol -> classSymbol.declaration;
            case InterfaceSymbol interfaceSymbol -> interfaceSymbol.declaration;
            case EnumSymbol enumSymbol -> enumSymbol.declaration;
            case RecordSymbol recordSymbol -> recordSymbol.declaration;
            case AnnotationSymbol annotationSymbol -> annotationSymbol.declaration;
        };
    }

    public static Scope getObjectScope(LCObjectDeclaration lcObjectDeclaration) {
        return switch (lcObjectDeclaration) {
            case LCClassDeclaration lcClassDeclaration -> lcClassDeclaration.body.scope;
            case LCInterfaceDeclaration lcInterfaceDeclaration -> lcInterfaceDeclaration.body.scope;
            case LCEnumDeclaration lcEnumDeclaration -> lcEnumDeclaration.body.scope;
            case LCRecordDeclaration lcRecordDeclaration -> lcRecordDeclaration.body.scope;
            case LCAnnotationDeclaration lcAnnotationDeclaration -> lcAnnotationDeclaration.annotationBody.scope;
        };
    }

    public static LCObjectDeclaration[] getObjectDeclarations(LCObjectDeclaration lcObjectDeclaration) {
        if (lcObjectDeclaration.body == null) return new LCObjectDeclaration[0];

        ArrayList<LCObjectDeclaration> objectDeclarations = new ArrayList<>();

        for (LCStatement statement : lcObjectDeclaration.body.statements) {
            if (statement instanceof LCObjectDeclaration objectDeclaration) {
                objectDeclarations.add(objectDeclaration);
                objectDeclarations.addAll(List.of(getObjectDeclarations(objectDeclaration)));
            }
        }
        return objectDeclarations.toArray(new LCObjectDeclaration[0]);
    }

    public static boolean inCommonRange(LCAstNode node1, LCAstNode node2) {
        LCAstNode pA = node1.parentNode;
        LCAstNode pB = node2.parentNode;
        while (!Objects.equals(pA, pB)) {
            pA = pA != null ? pA.parentNode : node2.parentNode;
            pB = pB != null ? pB.parentNode : node1.parentNode;
        }
        return pA != null;
    }
}