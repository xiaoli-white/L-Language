package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.expression.LCBinary;
import ldk.l.lc.ast.expression.LCMethodCall;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.token.Token;
import ldk.l.lc.util.Context;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.symbol.MethodKind;

public final class ModifierChecker extends LCAstVisitor {
    private final ErrorStream errorStream;

    public ModifierChecker(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    // TODO 处理其它访问修饰符 && 对于不可变变量的初次赋值不报错
//    @Override
//    public Object visitDot(LCDot lcDot, Object additional) {
//        this.visit(lcDot.base, additional);
//        boolean canAccessNonStatic = !(lcDot.base instanceof LCTypeReferenceExpression);
//        Context context = new Context();
//        context.add("canAccessNonStatic", canAccessNonStatic);
//        this.visit(lcDot.property, context);
//        return null;
//    }

//    @Override
//    public Object visitMemberAccess(LCMemberAccess lcMemberAccess, Object additional) {
//        this.visit(lcMemberAccess.base, additional);
//        boolean canAccessNonStatic = !(lcMemberAccess.base instanceof LCTypeReferenceExpression);
//        Context context = new Context();
//        context.add("canAccessNonStatic", canAccessNonStatic);
//        this.visit(lcMemberAccess.member, context);
//        return null;
//    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object context_object) {
        super.visitMethodCall(lcMethodCall, null);

        Context context = context_object instanceof Context ? (Context) context_object : new Context();
        boolean canAccessNonStatic = context.contains("canAccessNonStatic") && (boolean) context.get("canAccessNonStatic");
        if (!canAccessNonStatic) {
            LCMethodDeclaration lcMethodDeclaration = this.getEnclosingMethodDeclaration(lcMethodCall);
            canAccessNonStatic = lcMethodDeclaration != null && !LCFlags.hasStatic(lcMethodDeclaration.modifier.flags);
        }

        if (lcMethodCall.expression == null && lcMethodCall.symbol != null) {
            if (lcMethodCall.symbol.declaration.methodKind != MethodKind.Constructor) {
                if (!LCFlags.hasStatic(lcMethodCall.symbol.flags) && !canAccessNonStatic) {
                    System.err.println("无法从静态上下文中引用非静态方法'" + lcMethodCall.symbol.getFullName() + "'");
                }
            }

        }

        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object context_object) {
        Context context = context_object instanceof Context ? (Context) context_object : new Context();
        boolean canAccessNonStatic = context.contains("canAccessNonStatic") && (boolean) context.get("canAccessNonStatic");
        if (!canAccessNonStatic) {
            LCMethodDeclaration lcMethodDeclaration = this.getEnclosingMethodDeclaration(lcVariable);
            canAccessNonStatic = lcMethodDeclaration != null && !LCFlags.hasStatic(lcMethodDeclaration.modifier.flags);
        }
        if (lcVariable.symbol != null && lcVariable.symbol.objectSymbol != null) {
            if (!LCFlags.hasStatic(lcVariable.symbol.flags) && !canAccessNonStatic) {
                System.err.println("无法从静态上下文中引用非静态变量'" + lcVariable.name + "'");
            }
        }
        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object additional) {
        if (Token.isAssignOperator(lcBinary._operator)) {
            if (lcBinary.expression1 instanceof LCVariable lcVariable) {
                if (lcVariable.symbol != null) {
                    if (LCFlags.hasConst(lcVariable.symbol.flags) || LCFlags.hasFinal(lcVariable.symbol.flags)) {
                        System.err.println("无法对不可变变量'" + lcVariable.name + "'赋值");
                    }
                }
            }
        }
        return null;
    }
}
