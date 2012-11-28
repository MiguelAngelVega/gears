package jdbc;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;
import org.openlogics.gears.jdbc.BatchQuery;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;
import pojo.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Vega
 * @version $Id: InsertTest.java 0, 2012-11-21 19:49 mvega $
 */
public class InsertTest extends TestStub {
    @Test
    public void simplePojoInsert() {
        DataStore ds = new JdbcDataStore(basicDataSource);

        try {

            long icount = countAll(ds);

            Student std = new Student();
            std.setFname("Mr.");
            std.setLname("Bean");
            std.setRate(100);
            std.setAddDate(new Timestamp(currentTimeMillis()));

            int count = ds.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME, FOO_RATE, FOO_ADD_DATE) " +
                    "values " +
                    "(#{fname}, #{lname}, #{rate}, #{addDate})", std));

            assertEquals(1, count);
            assertEquals(1 + icount, countAll(ds));

            viewAll(ds);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void simpleMapInsert() {
        DataStore ds = new JdbcDataStore(basicDataSource);

        try {

            long icount = countAll(ds);

            Map<String, Object> map = ImmutableMap.<String, Object>of(
                    "fname", "Mr.",
                    "lname", "Bean",
                    "rate", 100f,
                    "addDate", new Timestamp(currentTimeMillis())
            );

            int count = ds.update(new Query("insert into FOO (FOO_FNAME, FOO_LNAME, FOO_RATE, FOO_ADD_DATE) " +
                    "values " +
                    "(#{fname}, #{lname}, #{rate}, #{addDate})", map));

            assertEquals(1, count);
            assertEquals(1 + icount, countAll(ds));
            logger.info("Showing MAP Insertion results...");
            viewAll(ds);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void batchInsert() throws SQLException {
        DataStore ds = new JdbcDataStore(basicDataSource);
        ds.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

        Student std = new Student();
        std.setFname("Mr.");
        std.setLname("Bean");
        std.setRate(100);
        std.setAddDate(new Timestamp(currentTimeMillis()));

        BatchQuery<Map<String, Object>> query = new BatchQuery<Map<String, Object>>("insert into FOO (FOO_FNAME, FOO_LNAME, FOO_RATE, FOO_ADD_DATE)" +
                " values " +
                "(#{fname}, #{lname}, #{rate}, #{addDate})", ds);
        query.addBatch(ImmutableMap.<String, Object>of("fname", "User 1", "lname", "Last 1", "rate", 61f, "addDate", new Timestamp(currentTimeMillis()))).
        addBatch(ImmutableMap.<String, Object>of("fname", "User 2", "lname", "Last 2", "rate", 62f, "addDate", new Timestamp(currentTimeMillis()))).
        addBatch(ImmutableMap.<String, Object>of("fname", "User 3", "lname", "Last 3", "rate", 63f, "addDate", new Timestamp(currentTimeMillis()))).
        addBatch(ImmutableMap.<String, Object>of("fname", "User 4", "lname", "Last 4", "rate", 64f, "addDate", new Timestamp(currentTimeMillis()))).
        addBatch(ImmutableMap.<String, Object>of("fname", "User 5", "lname", "Last 5", "rate", 65f, "addDate", new Timestamp(currentTimeMillis()))).
        addBatch(ImmutableMap.<String, Object>of("fname", "User 6", "lname", "Last 6", "rate", 66f, "addDate", new Timestamp(currentTimeMillis()))).
        addBatch(std);

        int[] update = ds.update(query);
        assertEquals(7, update.length);

        logger.info("Showing update batch results");
        viewAll(ds);

    }
}
