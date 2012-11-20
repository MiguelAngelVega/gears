package org.openlogics.gears.jdbc.annotations;

import java.lang.annotation.*;

/**
 * @author Miguel Vega
 * @version $Id: TransactionProfiled.java 0, 2012-11-20 00:37 mvega $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TransactionProfiled {
}
