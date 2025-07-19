package ldk.l.lc.util.symbol;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.util.symbol.object.*;

public class SymbolDumper extends SymbolVisitor {
    @Override
    public Object visitVariableSymbol(VariableSymbol variableSymbol, Object prefix) {
        System.out.println(prefix + "VariableSymbol{name: '" + variableSymbol.name + "', flags: '" + LCFlags.toFlagsString(variableSymbol.flags) + "', attributes: " + variableSymbol.attributes + "}");

        return null;
    }

    @Override
    public Object visitMethodSymbol(MethodSymbol methodSymbol, Object prefix) {
        System.out.println(prefix + methodSymbol.methodKind.name() + "{name: '" + methodSymbol.getFullName() + "', flags: '" + LCFlags.toFlagsString(methodSymbol.flags) + "', attributes: " + methodSymbol.attributes + ", local var count: " + methodSymbol.vars.size() + "}");

        if (methodSymbol.closure != null && methodSymbol.closure.vars.length > 0) {
            System.out.println(prefix + "    " + methodSymbol.closure);
        }

        return null;
    }

    @Override
    public Object visitClassSymbol(ClassSymbol classSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("ClassSymbol{name: '").append(classSymbol.getFullName()).append("'");

        if (!classSymbol.typeParameters.isEmpty()) {
            str.append(", typeParameter:[");
            for (int i = 0; i < classSymbol.typeParameters.size(); i++) {
                TypeParameterSymbol typeParameter = classSymbol.typeParameters.get(i);
                str.append("'").append(typeParameter.name).append("'");

                if (i < classSymbol.implementedInterfaces.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (classSymbol.extended != null) str.append(", extends: '").append(classSymbol.extended.name).append("'");
        if (classSymbol.implementedInterfaces != null && !classSymbol.implementedInterfaces.isEmpty()) {
            str.append(", implements:[");
            for (int i = 0; i < classSymbol.implementedInterfaces.size(); i++) {
                InterfaceSymbol implementedInterface = classSymbol.implementedInterfaces.get(i);
                str.append("'").append(implementedInterface.name).append("'");

                if (i < classSymbol.implementedInterfaces.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (classSymbol.permittedClasses != null && !classSymbol.permittedClasses.isEmpty()) {
            str.append(", permits:[");
            for (int i = 0; i < classSymbol.permittedClasses.size(); i++) {
                ClassSymbol permittedClass = classSymbol.permittedClasses.get(i);
                str.append("'").append(permittedClass.name).append("'");

                if (i < classSymbol.permittedClasses.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (!classSymbol.properties.isEmpty()) {
            str.append(", props:[");
            for (int i = 0; i < classSymbol.properties.size(); i++) {
                VariableSymbol property = classSymbol.properties.get(i);
                str.append("'").append(property.name).append("'");

                if (i < classSymbol.properties.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (!classSymbol.methods.isEmpty()) {
            str.append(", methods:[");
            for (int i = 0; i < classSymbol.methods.size(); i++) {
                MethodSymbol method = classSymbol.methods.get(i);
                str.append("'").append(method.name).append("'");
                if (i < classSymbol.methods.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);

        return null;
    }

    @Override
    public Object visitInterfaceSymbol(InterfaceSymbol interfaceSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("InterfaceSymbol{name: '").append(interfaceSymbol.getFullName()).append("'");

        if (interfaceSymbol.extendedInterfaces != null && !interfaceSymbol.extendedInterfaces.isEmpty()) {
            str.append(", extends:[");
            for (int i = 0; i < interfaceSymbol.extendedInterfaces.size(); i++) {
                InterfaceSymbol implementedInterface = interfaceSymbol.extendedInterfaces.get(i);
                str.append("'").append(implementedInterface.name).append("'");

                if (i < interfaceSymbol.extendedInterfaces.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (!interfaceSymbol.methods.isEmpty()) {
            str.append(", methods:[");
            for (int i = 0; i < interfaceSymbol.methods.size(); i++) {
                MethodSymbol method = interfaceSymbol.methods.get(i);
                str.append("'").append(method.name).append("'");
                if (i < interfaceSymbol.methods.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);
        return null;
    }

    @Override
    public Object visitAnnotationSymbol(AnnotationSymbol annotationSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("AnnotationSymbol{name: '").append(annotationSymbol.getFullName()).append("'");

        if (!annotationSymbol.fields.isEmpty()) {
            str.append(", fields:[");
            for (int i = 0; i < annotationSymbol.fields.size(); i++) {
                AnnotationSymbol.AnnotationFieldSymbol field = annotationSymbol.fields.get(i);
                str.append("'").append(field.name).append("'");
                if (i < annotationSymbol.fields.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);
        return null;
    }

    @Override
    public Object visitAnnotationFieldSymbol(AnnotationSymbol.AnnotationFieldSymbol annotationFieldSymbol, Object prefix) {
        System.out.println(prefix + "AnnotationFieldSymbol{name: '" + annotationFieldSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitEnumSymbol(EnumSymbol enumSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("EnumSymbol{name: '").append(enumSymbol.getFullName()).append("'");

        if (enumSymbol.implementedInterfaces != null && !enumSymbol.implementedInterfaces.isEmpty()) {
            str.append(", implements:[");
            for (int i = 0; i < enumSymbol.implementedInterfaces.size(); i++) {
                InterfaceSymbol implementedInterface = enumSymbol.implementedInterfaces.get(i);
                str.append("'").append(implementedInterface.name).append("'");

                if (i < enumSymbol.implementedInterfaces.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (!enumSymbol.fields.isEmpty()) {
            str.append(", fields:[");
            for (int i = 0; i < enumSymbol.fields.size(); i++) {
                EnumSymbol.EnumFieldSymbol field = enumSymbol.fields.get(i);
                str.append("'").append(field.name).append("'");

                if (i < enumSymbol.fields.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (!enumSymbol.properties.isEmpty()) {
            str.append(", props:[");
            for (int i = 0; i < enumSymbol.properties.size(); i++) {
                VariableSymbol property = enumSymbol.properties.get(i);
                str.append("'").append(property.name).append("'");

                if (i < enumSymbol.properties.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (!enumSymbol.methods.isEmpty()) {
            str.append(", methods:[");
            for (int i = 0; i < enumSymbol.methods.size(); i++) {
                MethodSymbol method = enumSymbol.methods.get(i);
                str.append("'").append(method.name).append("'");
                if (i < enumSymbol.methods.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);

        return null;
    }

    @Override
    public Object visitEnumFieldSymbol(EnumSymbol.EnumFieldSymbol enumFieldSymbol, Object prefix) {
        System.out.println(prefix + "EnumFieldSymbol{name: '" + enumFieldSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitRecordSymbol(RecordSymbol recordSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("RecordSymbol{name: '").append(recordSymbol.getFullName()).append("'");

        if (recordSymbol.implementedInterfaces != null && !recordSymbol.implementedInterfaces.isEmpty()) {
            str.append(", implements:[");
            for (int i = 0; i < recordSymbol.implementedInterfaces.size(); i++) {
                InterfaceSymbol implementedInterface = recordSymbol.implementedInterfaces.get(i);
                str.append("'").append(implementedInterface.name).append("'");

                if (i < recordSymbol.implementedInterfaces.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (!recordSymbol.fields.isEmpty()) {
            str.append(", fields:[");
            for (int i = 0; i < recordSymbol.fields.size(); i++) {
                VariableSymbol field = recordSymbol.fields.get(i);
                str.append("'").append(field.name).append("'");

                if (i < recordSymbol.fields.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (!recordSymbol.properties.isEmpty()) {
            str.append(", props:[");
            for (int i = 0; i < recordSymbol.properties.size(); i++) {
                VariableSymbol property = recordSymbol.properties.get(i);
                str.append("'").append(property.name).append("'");

                if (i < recordSymbol.properties.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (!recordSymbol.methods.isEmpty()) {
            str.append(", methods:[");
            for (int i = 0; i < recordSymbol.methods.size(); i++) {
                MethodSymbol method = recordSymbol.methods.get(i);
                str.append("'").append(method.name).append("'");
                if (i < recordSymbol.methods.size() - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);

        return null;
    }

    @Override
    public Object visitResourceForNativeSymbol(ResourceForNativeSymbol resourceForNativeSymbol, Object prefix) {
        System.out.println(prefix + "ResourceForNativeSymbol{name: '" + resourceForNativeSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitTypeParameterSymbol(TypeParameterSymbol typeParameterSymbol, Object prefix) {
        System.out.println(prefix + "TypeParameterSymbol{name: '" + typeParameterSymbol.name + "'}");
        return null;
    }
}