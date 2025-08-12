package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.error.ErrorStream;

import java.util.Map;

public class TypeParametersProcessor extends LCAstVisitor {
    private final SemanticAnalyzer semanticAnalyzer;
    private final Map<String, Type> argName2Type;
    private final ErrorStream errorStream;

    public TypeParametersProcessor(SemanticAnalyzer semanticAnalyzer, Map<String, Type> argName2Type, ErrorStream errorStream) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.argName2Type = argName2Type;
        this.errorStream = errorStream;
    }

    @Override
    public Object visitTypeReferenceExpression(LCTypeReferenceExpression lcTypeReferenceExpression, Object additional) {
        if (argName2Type.containsKey(lcTypeReferenceExpression.name)) {
            lcTypeReferenceExpression.theType = argName2Type.get(lcTypeReferenceExpression.name);
            semanticAnalyzer.typeResolver.visit(lcTypeReferenceExpression.parentNode, additional);
        }
        return null;
    }
}
