package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.expression.literal.LCNullLiteral;
import ldk.l.lc.ast.expression.literal.LCNullptrLiteral;
import ldk.l.lc.ast.expression.literal.LCStringLiteral;
import ldk.l.lc.ast.expression.type.*;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.LCTypedef;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.semantic.types.*;
import ldk.l.lc.util.error.ErrorStream;

import java.util.ArrayList;

public final class TypeResolver extends LCAstVisitor {
    private final ErrorStream errorStream;

    public TypeResolver(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public Object visitTypedef(LCTypedef lcTypedef, Object additional) {
        if (lcTypedef.typeExpression != null)
            this.visit(lcTypedef.typeExpression, additional);
        return super.visitTypedef(lcTypedef, additional);
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        super.visitClassDeclaration(lcClassDeclaration, additional);

        NamedType type = this.getAST(lcClassDeclaration).name2Type.get(lcClassDeclaration.getFullName());

        if (lcClassDeclaration.extended != null) {
            type.upperTypes.add((NamedType) lcClassDeclaration.extended.theType);
        }
        for (LCTypeReferenceExpression implementedInterface : lcClassDeclaration.implementedInterfaces) {
            type.upperTypes.add(this.visitTypeReferenceExpression(implementedInterface, null));
        }
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        NamedType type = this.getAST(lcInterfaceDeclaration).name2Type.get(lcInterfaceDeclaration.getFullName());

        for (LCTypeReferenceExpression extendedInterface : lcInterfaceDeclaration.extendedInterfaces) {
            type.upperTypes.add(this.visitTypeReferenceExpression(extendedInterface, null));
        }

        return super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        NamedType type = this.getAST(lcEnumDeclaration).name2Type.get(lcEnumDeclaration.getFullName());

        for (LCTypeReferenceExpression implementedInterface : lcEnumDeclaration.implementedInterfaces) {
            type.upperTypes.add(this.visitTypeReferenceExpression(implementedInterface, null));
        }

        return super.visitEnumDeclaration(lcEnumDeclaration, additional);
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        NamedType type = this.getAST(lcRecordDeclaration).name2Type.get(lcRecordDeclaration.getFullName());

        for (LCTypeReferenceExpression implementedInterface : lcRecordDeclaration.implementedInterfaces) {
            type.upperTypes.add(this.visitTypeReferenceExpression(implementedInterface, null));
        }

        return super.visitRecordDeclaration(lcRecordDeclaration, additional);
    }

    @Override
    public Object visitAnnotationFieldDeclaration(LCAnnotationDeclaration.LCAnnotationFieldDeclaration lcAnnotationFieldDeclaration, Object additional) {
        if (lcAnnotationFieldDeclaration.typeExpression != null)
            this.visit(lcAnnotationFieldDeclaration.typeExpression, additional);

        return super.visitAnnotationFieldDeclaration(lcAnnotationFieldDeclaration, additional);
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (lcMethodDeclaration.returnTypeExpression != null)
            lcMethodDeclaration.returnType = (Type) this.visit(lcMethodDeclaration.returnTypeExpression, additional);
        for (LCTypeReferenceExpression threwException : lcMethodDeclaration.threwExceptions)
            this.visitTypeReferenceExpression(threwException, additional);
        if (lcMethodDeclaration.extended != null)
            this.visitTypeReferenceExpression(lcMethodDeclaration.extended, additional);
        return super.visitMethodDeclaration(lcMethodDeclaration, additional);
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (lcVariableDeclaration.typeExpression != null)
            lcVariableDeclaration.theType = (Type) this.visit(lcVariableDeclaration.typeExpression, additional);
        if (lcVariableDeclaration.extended != null)
            this.visitTypeReferenceExpression(lcVariableDeclaration.extended, additional);

        if (lcVariableDeclaration.init != null)
            this.visit(lcVariableDeclaration.init, additional);
        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object additional) {
        if (lcLambda.returnTypeExpression != null)
            lcLambda.returnType = (Type) this.visit(lcLambda.returnTypeExpression, additional);
        for (LCTypeReferenceExpression threwException : lcLambda.threwExceptions)
            this.visitTypeReferenceExpression(threwException, additional);

        ArrayList<Type> paramTypes = new ArrayList<>();
        for (LCVariableDeclaration lcVariableDeclaration : lcLambda.parameterList.parameters) {
            paramTypes.add((Type) this.visit(lcVariableDeclaration.typeExpression, additional));
        }
        lcLambda.theType = new MethodPointerType(paramTypes.toArray(new Type[0]), lcLambda.returnType);

        return super.visitLambda(lcLambda, additional);
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object additional) {
        super.visitNewObject(lcNewObject, additional);
        lcNewObject.theType = lcNewObject.typeExpression.theType;
        return null;
    }

    @Override
    public Object visitNewArray(LCNewArray lcNewArray, Object additional) {
        super.visitNewArray(lcNewArray, additional);
        lcNewArray.theType = lcNewArray.typeExpression.theType;
        for (int i = 0; i < lcNewArray.dimensions.size(); i++) {
            lcNewArray.theType = new ArrayType(lcNewArray.theType);
        }
        return null;
    }

    @Override
    public Object visitTypeCast(LCTypeCast lcTypeCast, Object additional) {
        lcTypeCast.theType = (Type) this.visit(lcTypeCast.typeExpression, additional);
        this.visit(lcTypeCast.expression, additional);
        return null;
    }

    @Override
    public Object visitPlatform(LCPlatform lcPlatform, Object additional) {
        lcPlatform.theType = SystemTypes.String_Type;
        return null;
    }

    @Override
    public Object visitStringLiteral(LCStringLiteral lcStringLiteral, Object additional) {
        lcStringLiteral.theType = SystemTypes.String_Type;
        return null;
    }

    @Override
    public Object visitNullLiteral(LCNullLiteral lcNullLiteral, Object additional) {
        lcNullLiteral.theType = SystemTypes.Object_Type;
        return null;
    }

    @Override
    public Object visitNullptrLiteral(LCNullptrLiteral lcNullptrLiteral, Object additional) {
        lcNullptrLiteral.theType = SystemTypes.VOID_POINTER;
        return null;
    }

    @Override
    public Object visitClassof(LCClassof lcClassof, Object additional) {
        this.visit(lcClassof.typeExpression, additional);
        lcClassof.theType = SystemTypes.Class_Type;
        return null;
    }

    @Override
    public NamedType visitPredefinedTypeExpression(LCPredefinedTypeExpression lcPredefinedTypeExpression, Object additional) {
        NamedType namedType = switch (lcPredefinedTypeExpression.keyword) {
            case Byte -> SystemTypes.BYTE;
            case Short -> SystemTypes.SHORT;
            case Int -> SystemTypes.INT;
            case Long -> SystemTypes.LONG;
            case UByte -> SystemTypes.UNSIGNED_BYTE;
            case UShort -> SystemTypes.UNSIGNED_SHORT;
            case UInt -> SystemTypes.UNSIGNED_INT;
            case ULong -> SystemTypes.UNSIGNED_LONG;
            case Float -> SystemTypes.FLOAT;
            case Double -> SystemTypes.DOUBLE;
            case Char -> SystemTypes.CHAR;
            case Boolean -> SystemTypes.BOOLEAN;
            case Void -> SystemTypes.VOID;
            case Auto -> throw new IllegalStateException("Auto type is not allowed in predefined type expression.");
        };
        lcPredefinedTypeExpression.theType = namedType;
        return namedType;
    }

    @Override
    public NamedType visitTypeReferenceExpression(LCTypeReferenceExpression lcTypeReferenceExpression, Object additional) {
        super.visitTypeReferenceExpression(lcTypeReferenceExpression, additional);

        LCAst ast = this.getAST(lcTypeReferenceExpression);

        NamedType t = ast.name2Type.get(lcTypeReferenceExpression.name);
        if (t != null) {
            lcTypeReferenceExpression.theType = t;
            return t;
        }

        LCObjectDeclaration objectDeclaration = LCAstUtil.getObjectDeclarationByName(lcTypeReferenceExpression, lcTypeReferenceExpression.name);
        if (objectDeclaration == null) return null;


        t = ast.name2Type.get(objectDeclaration.getFullName());
        lcTypeReferenceExpression.theType = t;
        return t;
    }

    @Override
    public ReferenceType visitReferenceTypeExpression(LCReferenceTypeExpression lcReferenceTypeExpression, Object additional) {
        Type baseType = (Type) this.visit(lcReferenceTypeExpression.base, additional);

        ReferenceType referenceType = new ReferenceType(baseType);
        lcReferenceTypeExpression.theType = referenceType;
        return referenceType;
    }

    @Override
    public ArrayType visitArrayTypeExpression(LCArrayTypeExpression lcArrayTypeExpression, Object additional) {
        Type baseType = (Type) this.visit(lcArrayTypeExpression.base, additional);
        ArrayType arrayType = new ArrayType(baseType);
        lcArrayTypeExpression.theType = arrayType;
        return arrayType;
    }

    @Override
    public PointerType visitPointerTypeExpression(LCPointerTypeExpression lcPointerTypeExpression, Object additional) {
        Type baseType = (Type) this.visit(lcPointerTypeExpression.base, additional);
        PointerType pointerType = new PointerType(baseType);
        lcPointerTypeExpression.theType = pointerType;
        return pointerType;
    }

    @Override
    public MethodPointerType visitMethodPointerTypeExpression(LCMethodPointerTypeExpression lcMethodPointerTypeExpression, Object additional) {
        ArrayList<Type> paramTypes = new ArrayList<>();
        for (LCVariableDeclaration lcVariableDeclaration : lcMethodPointerTypeExpression.parameterList.parameters) {
            this.visitVariableDeclaration(lcVariableDeclaration, additional);
            paramTypes.add(lcVariableDeclaration.theType);
        }
        Type returnType = (Type) this.visit(lcMethodPointerTypeExpression.returnTypeExpression, additional);
        MethodPointerType methodPointerType = new MethodPointerType(paramTypes.toArray(new Type[0]), returnType);
        lcMethodPointerTypeExpression.theType = methodPointerType;
        return methodPointerType;
    }

    @Override
    public NullableType visitNullableTypeExpression(LCNullableTypeExpression lcNullableTypeExpression, Object additional) {
        Type baseType = (Type) this.visit(lcNullableTypeExpression.base, additional);
        NullableType nullableType = new NullableType(baseType);
        lcNullableTypeExpression.theType = nullableType;
        return nullableType;
    }

    @Override
    public Type visitParenthesizedTypeExpression(LCParenthesizedTypeExpression lcParenthesizedTypeExpression, Object additional) {
        Type type = (Type) this.visit(lcParenthesizedTypeExpression.base, additional);
        lcParenthesizedTypeExpression.theType = type;
        return type;
    }

    @Override
    public Object visitAutoTypeExpression(LCAutoTypeExpression lcAutoTypeExpression, Object additional) {
        lcAutoTypeExpression.theType = SystemTypes.AUTO;
        return SystemTypes.AUTO;
    }
}