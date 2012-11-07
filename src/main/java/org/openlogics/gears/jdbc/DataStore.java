/*
 *     gears
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
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Miguel Vega
 * @version $Id: DataStore.java 0, 2012-10-05 01:16 mvega $
 */
public abstract class DataStore {
    private Connection connection;
    private boolean autoClose = true;
    private boolean autoCommit = true;

    public <T> T select() {
        return null;
    }

    /**
     * The very basic connection to access the whole
     *
     * @return database connection
     */
    protected abstract Connection openConnection() throws SQLException;

    /**
     * @throws SQLException
     */
    public void commitAndClose() throws SQLException {
        try {
            commit();
        } finally {
            DbUtils.close(connection);
        }
    }

    /**
     * @throws SQLException
     */
    protected void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    /**
     * rollback all changes
     * @throws SQLException
     */
    public void rollBack() throws SQLException {
        if (connection != null)
            connection.rollback();
    }

    /**
     * @throws SQLException
     */
    protected void closeConnection() throws SQLException {
        if (autoCommit) {
            DbUtils.commitAndClose(connection);
        } else {
            if (autoClose) {
                DbUtils.close(connection);
            }
        }
    }

    /**
     * This method evaluates the given query string and replaces the parameters given as '?'
     * in the same order that the provided objects.
     *
     * @param query
     * @param resultVisitor
     * @param parameters
     * @param <T>
     * @return what visitor decides to return
     */
    public <T> T select(Query query, final SimpleResultVisitor<T> resultVisitor, Object... parameters) throws SQLException {

        try {
            QueryRunner runner = new QueryRunner();
            return runner.query(connection, query.toString(), new ResultSetHandler<T>() {
                @Override
                public T handle(ResultSet rs) throws SQLException {
                    return resultVisitor.visit(rs);
                }
            });
        } finally {
            closeConnection();
        }

    }
}