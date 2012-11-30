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

import org.apache.commons.dbutils.handlers.MapHandler;
import org.junit.Test;
import pojo.Student;

import java.sql.SQLException;
import java.util.HashMap;
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
    public void testSimpleUpdate() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            int count = ds.update(new Query("UPDATE FOO SET FOO_FNAME = 'ARNOLD' WHERE FOO_ID = #{parameter}", 5));

            Map<String, Object> result = ds.select(new Query("SELECT * FROM FOO WHERE FOO_ID = 5"), new MapHandler());

            viewAll(ds);

            assertEquals(1, count);
            assertEquals("ARNOLD", result.get("FOO_FNAME"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimplePojoUpdate() {
        DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            Student student = new Student();
            student.setId(5);
            student.setFname("MAURICIO");
            student.setLname("RAMIREZ");

            int count = ds.update(new Query("UPDATE FOO SET FOO_FNAME = #{fname}, FOO_LNAME = #{lname} WHERE FOO_ID = #{id}", student));
            Map<String, Object> res = ds.select(new Query("SELECT * FROM FOO WHERE FOO_ID = #{id}", student), new MapHandler());

            viewAll(ds);

            logger.info("count " + count);
            logger.info("listMap.size()->" + res.size());
            logger.info("get(\"FOO_FNAME\")->" + res.get("FOO_FNAME"));
            logger.info("get(\"FOO_LNAME\")->" + res.get("FOO_LNAME"));

            assertEquals(1, count);
            assertEquals("MAURICIO", res.get("FOO_FNAME"));
            assertEquals("RAMIREZ", res.get("FOO_LNAME"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleMapUpdate() {
         DataStore ds = new JdbcDataStore(basicDataSource);
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", 5);
            map.put("fname", "MAURICIO");
            map.put("lname", "RAMIREZ");

            int count = ds.update(new Query("UPDATE FOO SET FOO_FNAME = #{fname}, FOO_LNAME = #{lname} WHERE FOO_ID = #{id}", map));
            Map<String, Object> res = ds.select(new Query("SELECT * FROM FOO WHERE FOO_ID = #{id}", map), new MapHandler());

            viewAll(ds);

            logger.info("count->" + count);
            logger.info("listMap.size()->" + res.size());
            logger.info("listMap.get(0).get(\"FOO_FNAME\")->" + res.get("FOO_FNAME"));
            logger.info("listMap.get(0).get(\"FOO_LNAME\")->" + res.get("FOO_LNAME"));

            assertEquals(1, count);
            assertEquals("MAURICIO", res.get("FOO_FNAME"));
            assertEquals("RAMIREZ", res.get("FOO_LNAME"));
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
