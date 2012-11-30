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

package org.openlogics.gears.jdbc;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openlogics.gears.jdbc.annotations.TransactionObservable;

import java.sql.SQLException;

/**
 * This class aids to intercept any transaction exception, thus preventing from an invalid commit on
 * truncated data.
 *
 * @author Miguel Vega
 * @version $Id: TransactionInterceptor.java 0, 2012-11-30 5:33 PM mvega $
 */
public class TransactionInterceptor implements MethodInterceptor {
    @Inject(optional = false)
    private DataStore dataStore;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        //check if method is annotated with the {@link TransactionInterceptor} class
        TransactionObservable to = methodInvocation.getMethod().getAnnotation(TransactionObservable.class);
        if (to != null) {
            //need to set the data store to autocommit mode FALSE
            boolean autoCommit = dataStore.isAutoCommit();
            boolean autoClose = dataStore.isAutoClose();

            try {
                dataStore.setAutoCommit(false);

                Object proceed = methodInvocation.proceed();

                dataStore.commit();

                return proceed;
            } catch (SQLException x) {
                //if an exception is thrown, then rollback, else commit
                dataStore.rollBack();
                throw x;
            }finally{
                dataStore.setAutoCommit(autoCommit);
                dataStore.setAutoClose(autoClose);
            }
        }
        return methodInvocation.proceed();
    }
}
