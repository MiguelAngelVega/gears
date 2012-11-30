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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.sun.istack.internal.logging.Logger;
import org.junit.Test;
import org.openlogics.gears.jdbc.annotations.TransactionObserver;

import java.sql.SQLException;

/**
 * @author Miguel Vega
 * @version $Id: TransactionInterceptorTest.java 0, 2012-11-30 5:41 PM mvega $
 */
public class TransactionInterceptorTest {
    private Logger logger = Logger.getLogger(TransactionInterceptorTest.class);

    @Test
    public void testTransactionObserver() throws SQLException {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                TransactionInterceptor tob = new TransactionInterceptor();
                //requestInjection(tob);

                bindInterceptor(Matchers.any(), Matchers.annotatedWith(TransactionObserver.class), tob);
            }
        });

        SimpleTransactor st = new SimpleTransactor();

        injector.injectMembers(st);

        st.insertFine();
    }

    class SimpleTransactor{
        @TransactionObserver
        public void insertFine()throws SQLException{
            logger.info("Simulating a perfect insertion");
        }

        @TransactionObserver
        public void insertFail()throws SQLException{
            throw new SQLException("Unable to perform transaction");
        }
    }
}
