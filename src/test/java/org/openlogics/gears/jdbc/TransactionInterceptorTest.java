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

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.sun.istack.internal.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.openlogics.gears.jdbc.annotations.TransactionObservable;
import pojo.Foo;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.io.Resources.getResource;

/**
 * @author Miguel Vega
 * @version $Id: TransactionInterceptorTest.java 0, 2012-11-30 5:41 PM mvega $
 */
public class TransactionInterceptorTest {
    private static Logger logger = Logger.getLogger(TransactionInterceptorTest.class);

    @Inject
    SimpleTransactor transactor = null;

    FooDAO fooDao = new FooDAO();

    @Before
    public void setup(){
        final BasicDataSource basicDataSource = new BasicDataSource();
        try {
            basicDataSource.setUrl("jdbc:h2:mem:parametrostest");
            basicDataSource.setDriverClassName("org.h2.Driver");
            Connection connection = basicDataSource.getConnection();
            URL sql = getResource(TestStub.class, "students.sql");
            try {
                connection.createStatement().execute(Resources.toString(sql, US_ASCII));
                connection.close();

                URL resource = getResource(TestStub.class, "students.xml");
                FlatXmlDataSet build = new FlatXmlDataSetBuilder().build(resource);
                DataSourceDatabaseTester databaseTester = new DataSourceDatabaseTester(basicDataSource);
                databaseTester.setDataSet(build);
                databaseTester.onSetup();
            } catch (SQLException x) {

            }
        } catch (Exception x) {
            x.printStackTrace();
        }

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                TransactionInterceptor tob = new TransactionInterceptor();
                //requestInjection(tob);

                bindInterceptor(Matchers.any(), Matchers.annotatedWith(TransactionObservable.class), tob);

                //inject datastore
                bind(DataStore.class).toInstance(new JdbcDataStore(basicDataSource));
            }
        });

        injector.injectMembers(this);
        injector.injectMembers(fooDao);
    }

    @Test(expected = SQLException.class)
    public void testInsertTransaction() throws SQLException {

        fooDao.showFoos();

        try {
            fooDao.insert(new Foo());
        } catch (SQLException e) {
            throw e;
        }finally{
            logger.info(Strings.repeat("*", 30));
            fooDao.showFoos();
        }
    }

    @Test
    public void testTransactionObserver() throws SQLException {
        //This applies on;y to all those objects which are injected by Guice
        transactor.insertFine();
    }

    public static class SimpleTransactor{
        public SimpleTransactor(){

        }

        @TransactionObservable
        public void insertFine()throws SQLException{
            logger.info("Simulating a perfect insertion");
        }

        @TransactionObservable
        public void insertFail()throws SQLException{
            throw new SQLException("Unable to perform transaction");
        }
    }
}
