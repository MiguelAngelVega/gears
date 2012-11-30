/*
 * gears
 *     http://www.open-logics.com
 *     Copyright (C) 2012, OpenLogics
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package google.guice.aop0;

/**
 * @author Miguel Vega
 * @version $Id: TracingInterceptor.java 0, 2012-11-30 6:03 PM mvega $
 */

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;

public class TracingInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        long start = System.nanoTime();

        try {
            return invocation.proceed();
        } finally {
            System.out.println(String.format(
                    "Invocation of method %s() with parameters %s took %.1f ms.",
                    invocation.getMethod().getName(),
                    Arrays.toString(invocation.getArguments()),
                    (System.nanoTime() - start) / 1000000.0));
        }
    }

}