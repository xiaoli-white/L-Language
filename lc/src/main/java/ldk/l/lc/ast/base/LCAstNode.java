package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;
import ldk.l.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class LCAstNode implements Cloneable {
    public Position position;
    public boolean isErrorNode;
    public LCAstNode parentNode = null;

    protected LCAstNode(Position position, boolean isErrorNode) {
        this.position = position;
        this.isErrorNode = isErrorNode;
    }

    public abstract Object accept(LCAstVisitor visitor, Object additional);

    @Override
    public abstract String toString();

    @Override
    public LCAstNode clone() throws CloneNotSupportedException {
        LCAstNode cloned = (LCAstNode) super.clone();
        Class<?> clazz = cloned.getClass();
        List<Field> fields = Util.getAllFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            if ("parentNode".equals(field.getName()) || Modifier.isStatic(field.getModifiers())) continue;
            try {
                Object value = field.get(cloned);
                if (value instanceof Cloneable) {
                    field.set(cloned, cloneObject(value));
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return cloned;
    }

    public static Object cloneObject(Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method cloneMethod = o.getClass().getMethod("clone");
        cloneMethod.setAccessible(true);
        Object cloned = cloneMethod.invoke(o);
        if (o instanceof LCAstNode lcAstNode) {
            ((LCAstNode) cloned).parentNode = lcAstNode.parentNode;
        } else if (cloned instanceof List list) {
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                if (element instanceof Cloneable) {
                    Object clonedElement = cloneObject(element);
                    try {
                        list.set(i, clonedElement);
                    } catch (UnsupportedOperationException e) {
                        break;
                    }
                }
            }
        } else if (cloned instanceof Object[] array) {
            for (int i = 0; i < array.length; i++) {
                Object element = array[i];
                if (element instanceof Cloneable) {
                    array[i] = cloneObject(element);
                }
            }
        }
        return cloned;
    }
}