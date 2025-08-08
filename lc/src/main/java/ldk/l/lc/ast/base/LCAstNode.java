package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
    public final LCAstNode clone() throws CloneNotSupportedException {
        LCAstNode cloned = (LCAstNode) super.clone();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof Cloneable) {
                    Method cloneMethod = value.getClass().getMethod("clone");
                    cloneMethod.setAccessible(true);
                    Object clonedValue = cloneMethod.invoke(value);
                    field.set(cloned, clonedValue);
                } else {
                    field.set(cloned, value);
                }
            } catch (Exception e) {
                throw new CloneNotSupportedException("Cloning failed for field: " + field.getName());
            }
        }
        return cloned;
    }
}