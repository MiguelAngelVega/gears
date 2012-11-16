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

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;
import org.openlogics.gears.jdbc.ResultVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Miguel Vega
 * @version $Id: QueryTest.java 0, 2012-11-14 5:49 PM mvega $
 */
public class QueryTest extends DefaultTest {
    Logger logger = Logger.getLogger(QueryTest.class);
    @Test
    public void simpleQueryTest() {
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query<String> query = new Query<String>("select * from dis_students");
        String result = query.toString();
        assertEquals("select * from dis_students", result);
        logger.info("Result=" + result);

        try {
            String response = ds.select(query, new ResultVisitor<String>() {
                @Override
                public String visit(ResultSet rs) throws SQLException {
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

        Query<Integer> query = new Query<Integer>("select * from dis_students where STD_ID = #{id}", 2);

        List<Object> result = ds.select(query, new ResultVisitor<List<Object>>() {
            @Override
            public List<Object> visit(ResultSet rs) throws SQLException {
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
