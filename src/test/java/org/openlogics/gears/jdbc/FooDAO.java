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
import com.sun.istack.internal.logging.Logger;
import org.openlogics.gears.jdbc.annotations.TransactionObservable;
import pojo.Foo;

import java.sql.SQLException;

/**
 * @author Miguel Vega
 * @version $Id: FooDAO.java 0, 2012-11-30 7:16 PM mvega $
 */
public class FooDAO {

    @Inject
    private DataStore dataStore;

    private Logger logger = Logger.getLogger(FooDAO.class);

    @TransactionObservable
    public void insert(Foo foo) throws SQLException {
        dataStore.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME, FOO_RATE, FOO_ADD_DATE) " +
                "values " +
                "(#{fname}, #{lname}, #{rate}, #{addDate})", foo));
    }

    @TransactionObservable
    public void update(Foo foo) throws SQLException {
        dataStore.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME, FOO_RATE, FOO_ADD_DATE) " +
                "values " +
                "(#{fname}, #{lname}, #{rate}, #{addDate})", foo));
    }

    public void showFoos() throws SQLException {
        Query query = new Query("select FOO_ID, " +
                "FOO_FNAME, " +
                "FOO_LNAME, " +
                "FOO_RATE as rate, " +
                "FOO_ADD_DATE from FOO");

        dataStore.select(query, Foo.class, new ObjectResultSetHandler<Foo>() {
            @Override
            public void handle(Foo result) throws SQLException {
                logger.info("POJO > " + result.toString());
            }
        });

    }
}
