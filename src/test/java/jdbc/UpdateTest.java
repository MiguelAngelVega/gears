package jdbc;

import org.junit.Test;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;
import pojo.Student;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ARNOLD
 * Date: 23-11-12
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class UpdateTest extends TestStub {
    @Test
    public void simpleUpdate() {
        //TODO: DataStore.select debe aceptar cualquier tipo de handler
        //TODO: Se debe tener la siguiente opcion: ds.update(new Query("UPDATE DIS_STUDENTS SET STD_FNAME = 'ARNOLD' WHERE STD_ID = ? AND STD_LNAME = ?", 5, 'PAYE'));
        DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            int count = ds.update(new Query("UPDATE DIS_STUDENTS SET STD_FNAME = 'ARNOLD' WHERE STD_ID = #{parameter}", 5));

            List<Map<String, Object>> listMap = ds.select(new Query("SELECT * FROM DIS_STUDENTS WHERE STD_ID = 5"));

            viewAll(ds);

            assertEquals(1, count);
            assertEquals(1, listMap.size());
            assertEquals("ARNOLD", listMap.get(0).get("STD_FNAME"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void simplePojoUpdate() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            Student student = new Student();
            student.setId(5);
            student.setFname("MAURICIO");
            student.setLname("RAMIREZ");

            int count = ds.update(new Query("UPDATE DIS_STUDENTS SET STD_FNAME = #{fname}, STD_LNAME = #{lname} WHERE STD_ID = #{id}", student));
            List<Map<String, Object>> listMap = ds.select(new Query("SELECT * FROM DIS_STUDENTS WHERE STD_ID = #{id}", student));

            viewAll(ds);

            logger.debug("count " + count);
            logger.debug("listMap.size()->" + listMap.size());
            logger.debug("listMap.get(0).get(\"STD_FNAME\")->" + listMap.get(0).get("STD_FNAME"));
            logger.debug("listMap.get(0).get(\"STD_LNAME\")->" + listMap.get(0).get("STD_LNAME"));

            assertEquals(1, count);
            assertEquals(1, listMap.size());
            assertEquals("MAURICIO", listMap.get(0).get("STD_FNAME"));
            assertEquals("RAMIREZ", listMap.get(0).get("STD_LNAME"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SimpleMapUpdate() {
         DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", 5);
            map.put("fname", "MAURICIO");
            map.put("lname", "RAMIREZ");

            int count = ds.update(new Query("UPDATE DIS_STUDENTS SET STD_FNAME = #{fname}, STD_LNAME = #{lname} WHERE STD_ID = #{id}", map));
            List<Map<String, Object>> listMap = ds.select(new Query("SELECT * FROM DIS_STUDENTS WHERE STD_ID = #{id}", map));

            viewAll(ds);

            logger.debug("count->" + count);
            logger.debug("listMap.size()->" + listMap.size());
            logger.debug("listMap.get(0).get(\"STD_FNAME\")->" + listMap.get(0).get("STD_FNAME"));
            logger.debug("listMap.get(0).get(\"STD_LNAME\")->" + listMap.get(0).get("STD_LNAME"));

            assertEquals(1, count);
            assertEquals(1, listMap.size());
            assertEquals("MAURICIO", listMap.get(0).get("STD_FNAME"));
            assertEquals("RAMIREZ", listMap.get(0).get("STD_LNAME"));
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewAll(DataStore ds) throws SQLException {
        logger.info(System.getProperty("line.separator"));
        Query query = new Query("select STD_ID, " +
                "STD_FNAME, " +
                "STD_LNAME, " +
                "STD_RATE as rate, " +
                "STD_ADD_DATE from dis_students");
        List<Student> stds = ds.select(query, Student.class);
        for (Student std : stds) {
            logger.info("Result > " + std);
        }
        logger.info(System.getProperty("line.separator"));
    }
}
