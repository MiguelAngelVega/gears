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

import bean.Student;
import com.google.common.collect.Lists;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.openlogics.gears.jdbc.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Miguel Vega
 * @version $Id: QueryTest.java 0, 2012-11-14 5:49 PM mvega $
 */
public class QueryTest extends DefaultTest {
    Logger logger = Logger.getLogger(QueryTest.class);

    @Test
    public void objectResultVisitorTest(){
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query query = new Query("select STD_ID, " +
                "STD_FNAME, " +
                "STD_LNAME, " +
                "STD_RATE as rate, " +
                "STD_ADD_DATE from dis_students");
        try {
            ds.select(query, Student.class, new ObjectResultSetHandler<Student>() {
                @Override
                public void handle(Student result) throws SQLException {
                    logger.info(">>>>>>>>>id="+result.getId()+", rate="+result.getRate()+", addDate="+result.getAddDate());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //
        try {
            List<Map<String, Object>> results = ds.select(query);
            for (Map<String, Object> res:results){
                System.out.println("*****************************"+res+", rate="+res.get("STD_ID").getClass());
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //
        try {
            List<Student> stds = ds.select(query, Student.class);
            for (Student std:stds){
                System.out.println("--------------------------"+std);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void resultVisitorTest() {
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query query = new Query("select * from dis_students");
        String result = query.toString();
        assertEquals("select * from dis_students", result);
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

        Query query = new Query("select * from dis_students where STD_ID = #{id}", 2);

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
}
