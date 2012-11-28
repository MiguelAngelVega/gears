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

import com.google.common.io.Resources;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.Query;
import pojo.Student;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.io.Resources.getResource;

/**
 * @author Miguel Vega
 * @version $Id: TestStub.java 1, 2012-10-05 10:10 AM mvega $
 */
public abstract class TestStub {
    protected IDatabaseTester databaseTester;
    protected BasicDataSource basicDataSource;

    protected Logger logger;

    @Before
    public void setup(){
        this.logger = Logger.getLogger(getClass());

        try {

            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl("jdbc:h2:mem:parametrostest");
            basicDataSource.setDriverClassName("org.h2.Driver");
            Connection connection = basicDataSource.getConnection();
            URL sql = getResource(TestStub.class, "students.sql");
            try {
                connection.createStatement().execute(Resources.toString(sql, US_ASCII));
                connection.close();

                URL resource = getResource(TestStub.class, "students.xml");
                FlatXmlDataSet build = new FlatXmlDataSetBuilder().build(resource);
                databaseTester = new DataSourceDatabaseTester(basicDataSource);
                databaseTester.setDataSet(build);
                databaseTester.onSetup();
            } catch (SQLException x) {

            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @After
    public void dispose() throws Exception {
        databaseTester.onTearDown();
        basicDataSource.close();
    }

    protected void viewAll(DataStore ds) throws SQLException {
        logger.info(System.getProperty("line.separator"));
        Query query = new Query("select FOO_ID, " +
                "FOO_FNAME, " +
                "FOO_LNAME, " +
                "FOO_RATE as rate, " +
                "FOO_ADD_DATE from FOO");
        List<Student> stds = ds.select(query, Student.class);
        logger.info("*****************************************************************************************");
        for (Student std : stds) {
            logger.info("Result > " + std);
        }
        logger.info("*****************************************************************************************");
        logger.info(System.getProperty("line.separator"));
    }

    protected long countAll(DataStore ds) throws SQLException {
        Query query = new Query("select COUNT(FOO_ID) from FOO");
        return ds.select(query, new ScalarHandler<Long>(1));
    }
}
