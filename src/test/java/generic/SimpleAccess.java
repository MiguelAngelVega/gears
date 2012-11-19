package generic;

import com.sun.beans.TypeResolver;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author Miguel Vega
 * @version $Id: SimpleAccess.java 0, 2012-11-18 21:11 mvega $
 */
public class SimpleAccess {
    @Test
    public void testType() {
        new Foo<String>();
    }

    class Foo<T> {
        T var;

        Foo() {
            Type t = TypeResolver.resolveInClass(Foo.class, Foo.class);
            System.out.println("........"+t.getClass());
            Field field = null;
            try {
                field = Foo.class.getDeclaredField("var");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            System.out.println(field.getType()); // class java.lang.String.
        }
    }
}
