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

package jdbc;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.openlogics.gears.jdbc.BatchQuery;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;

import java.sql.SQLException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Vega
 * @version $Id: CommitmentTest.java 0, 2012-11-23 7:21 PM mvega $
 */
public class CommitmentTest extends TestStub {

    @Test
    public void testCommitTransaction() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setAutoCommit(false);

        long curr = 0;

        try {
            viewAll(ds);
            curr = countAll(ds);
            assertEquals(5, curr);

            ds.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})",
                    ImmutableMap.of("a", "miguel", "b", "vega")));
            assertEquals(1 + curr, countAll(ds));

            ds.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ds.rollBack();
                assertEquals(curr + 1, countAll(ds));
                //
                viewAll(ds);
                ds.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCommitBatchTransaction() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setAutoCommit(false);

        long curr = 0;

        try {
            viewAll(ds);
            curr = countAll(ds);

            assertEquals(5, curr);


            BatchQuery q = new BatchQuery("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})", ds).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega"));
            ds.update(q);

            //batch is in memory, but it's ready to be commited
            long actual = countAll(ds);
            //todo, if remove the '4' below, JUnit must throw an exception in the following assertion,
            //todo, this error is NOT happening, JUnit throw an error in the one in the finally block. What's wrong?
            //todo, this is confused, so all the time I though the error was happening in finally, not here
            assertEquals(curr + 4, actual);

            ds.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ds.rollBack();
                //as no error has occurred while inserting, the commit has been executed, so the batch rows were added
                assertEquals(curr + 4, countAll(ds));
                //
                viewAll(ds);
                ds.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRollbackTransaction() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setAutoCommit(false);

        try {
            assertEquals(5, countAll(ds));

            ds.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})",
                    ImmutableMap.of("a", "miguel", "NEED THIS TO BE 'b'", "vega")));

            ds.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ds.rollBack();
                assertEquals(5, countAll(ds));
                //
                viewAll(ds);
                ds.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRollbackBatchTransaction() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setAutoCommit(false);

        long current = 0;
        try {
            current = countAll(ds);
            assertEquals(5, current);

            BatchQuery q = new BatchQuery("insert into FOO (FOO_FNAME, FOO_LNAME) values (#{a}, #{b})", ds).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a", "miguel", "b", "vega")).
                    addBatch(ImmutableMap.of("a-mmmm", "miguel", "b-can not be null", "vega"));

            ds.update(q);

            //commit will never happens, because last row is wrong, this causes an SQLException to be thrown
            ds.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ds.rollBack();
                assertEquals(5, countAll(ds));
                //
                viewAll(ds);
                ds.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}