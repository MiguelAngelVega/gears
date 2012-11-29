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

import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Miguel Vega
 * @version $Id: JdbcDataStore.java 0, 2012-11-14 12:12 PM mvega $
 */
public class JdbcDataStore extends DataStore {
    public static final String DRIVER = "driver", URL = "url";

    //while using a JDBC data store, there are two possible ways where to take connection from.
    //Using a connection from properties or from a DataSource
    private Properties properties;
    private DataSource dataSource;

    public JdbcDataStore(Properties properties) {
        super();    //To change body of overridden methods use File | Settings | File Templates.
        this.properties = properties;
    }

    public JdbcDataStore(DataSource dataSource) {
        super();    //To change body of overridden methods use File | Settings | File Templates.
        this.dataSource = dataSource;
    }

    @Override
    protected Connection acquireConnection() throws SQLException {
        //check whether is properties or datasource available
        if (dataSource != null)
            return dataSource.getConnection();
        else if (properties != null) {
            try {
                logger.debug("Properties:" + properties);
                if (!DbUtils.loadDriver(properties.getProperty(DRIVER))) {
                    throw new SQLException("Unable to load driver: " + properties.get(DRIVER));
                }
                return DriverManager.getConnection(properties.getProperty(URL), properties);
                /*
                connection.setAutoCommit(isAutoCommit());
                if (getTransactionIsolation() != -1) {
                    connection.setTransactionIsolation(getTransactionIsolation());
                    logger.debug("Attempting to use a custom TRANSACTION ISOLATION, '" + getTransactionIsolation() + "'");
                }
                */
            } catch (NullPointerException ex) {
                logger.error("An unespected error has ocurred.");
                throw new SQLException("An unespected error has ocurred.", ex);
            }
        } else {
            throw new IllegalStateException("Nor properties neither dataSource has been provided");
        }
    }
}