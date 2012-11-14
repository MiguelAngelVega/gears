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

import junit.framework.Assert;
import org.junit.Test;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;

import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Vega
 * @version $Id: QueryTest.java 0, 2012-11-14 5:49 PM mvega $
 */
public class QueryTest extends DefaultTest {
    @Test
    public void simpleQueryTest() {
        DataStore ds = new JdbcDataStore(basicDataSource);

        Query<String> query = new Query<String>("select * from dis_students");
        String result = query.toString();
        assertEquals("select * from dis_students", result);

        System.out.println("Result="+result);
    }
}
