package org.openlogics.gears.jdbc.annotations;

import java.lang.annotation.*;

/**
 * Used by an interceptor, and measures the time spent between start and end of method execution.
 * @author Miguel Vega
 * @version $Id: TransactionProfiled.java 0, 2012-11-20 00:37 mvega $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TransactionProfiled {
}
