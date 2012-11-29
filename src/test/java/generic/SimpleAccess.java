package generic;

import com.sun.beans.TypeResolver;
import org.apache.log4j.Logger;
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
            Logger logger = Logger.getLogger(getClass());

            Type t = TypeResolver.resolveInClass(Foo.class, Foo.class);
            logger.debug("........"+t.getClass());
            Field field = null;
            try {
                field = Foo.class.getDeclaredField("var");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            logger.debug(field.getType()); // class java.lang.String.
        }
    }
}
