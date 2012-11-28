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

import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import pojo.Student;
import com.google.common.collect.Lists;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.openlogics.gears.jdbc.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Miguel Vega
 * @version $Id: SelectTest.java 0, 2012-11-14 5:49 PM mvega $
 */
public class SelectTest extends TestStub {

    @Test
    public void plainQueryTest(){
        DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            List<Map<String, Object>> list = ds.select(new Query("select * from FOO where FOO_id between ? AND ? ", 1, 3), new MapListHandler());
            assertEquals(3, list.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void objectResultVisitorTest(){
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query query = new Query("select FOO_ID, " +
                "FOO_FNAME, " +
                "FOO_LNAME, " +
                "FOO_RATE as rate, " +
                "FOO_ADD_DATE from FOO");
        try {
            ds.select(query, Student.class, new ObjectResultSetHandler<Student>() {
                @Override
                public void handle(Student result) throws SQLException {
                    logger.info("POJO > "+result.toString());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //
        try {
            List<Map<String, Object>> results = ds.select(query, new MapListHandler());
            for (Map<String, Object> res:results){
                System.out.println("MAP > "+res+", rate="+res.get("FOO_ID").getClass());
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //
        try {
            List<Student> stds = ds.select(query, Student.class);
            for (Student std:stds){
                System.out.println("LIST POJOS > "+std);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void resultVisitorTest() {
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query query = new Query("select * from FOO");
        String result = query.toString();
        assertEquals("select * from FOO", result);
        logger.info("Result=" + result);

        try {
            String response = ds.select(query, new ResultSetHandler<String>(){
                @Override
                public String handle(ResultSet rs) throws SQLException {
                    while(rs.next()){
                        logger.debug("Record found..." + rs.getInt(1));
                    }
                    return "success";
                }
            });

            assertEquals("success", response);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testContextQuery() throws SQLException {
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query query = new Query("select * from FOO where FOO_ID = ?", 2);

        List<Object> result = ds.select(query, new ResultSetHandler<List<Object>>() {
            @Override
            public List<Object> handle(ResultSet rs) throws SQLException {
                List<Object> result = Lists.newArrayList();
                if(rs.next()){
                    result.add(rs.getObject(1));
                    result.add(rs.getObject(2));
                    result.add(rs.getObject(3));
                    result.add(rs.getObject(4));
                }
                return result;
            }
        });

        assertTrue(!result.isEmpty());

        for (Object o : result){
            logger.info("Found....."+o);
        }
    }

    @Test
    public void singleSelectionTest() throws SQLException {
        DataStore ds = new JdbcDataStore(basicDataSource);
        Map<String, Object> res = ds.select(new Query("select * from FOO where FOO_id = 1"), new MapHandler());
        logger.info("Single Result: "+res);
        assertNotNull(res);
    }
}
