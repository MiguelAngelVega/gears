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

import com.google.common.collect.Lists;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Miguel Vega
 * @version $Id: DataStore.java 0, 2012-10-05 01:16 mvega $
 */
public abstract class DataStore {
    private Connection connection;
    private boolean autoClose = true;
    private boolean autoCommit = true;
    /*
     * transactionIsolation, allows to define the isolation level for the transaction,
     * useful when need to lock rows from database.
     */
    private int transactionIsolation;

    protected Logger logger;

    private String schema;

    DataStore() {
        logger = Logger.getLogger(getClass());
    }

    public <T> T select(Query query, SimpleResultVisitor<? extends T> visitor) throws SQLException {
        //a simple list to hold data about query
        List params = Lists.newLinkedList();
        try {
            String queryString = query.evaluateQueryString(this, params);
            PreparedStatement ps = prepareStatement(queryString.toString(), params);
            return this.select(ps, visitor);
        } finally {
            params.clear();
        }
    }

    /**
     * @param preparedSt
     * @param handler
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> T select(PreparedStatement preparedSt, SimpleResultVisitor<? extends T> handler) throws SQLException {
        logger.debug("Attempting to execute a preparedStatement QUERY: " + preparedSt + ", mapped to ");
        ResultSet rs = null;
        try {
            rs = preparedSt.executeQuery();
            return handler.visit(rs);
        } finally {
            if (rs != null) rs.close();
            DbUtils.close(preparedSt);
            if (isAutoClose()) {
                closeConnection();
            }
        }
    }

    /**
     * @param query
     * @param resultType
     * @param visitor
     */
    public void select(Query query, Class<?> resultType, ObjectResultVisitor<?> visitor) {

    }

    public <T> T select() {
        return null;
    }

    /**
     * Creates a new {@link PreparedStatement} object
     *
     * @param preparedSql
     * @param params
     * @return
     * @throws SQLException
     */
    public PreparedStatement prepareStatement(String preparedSql, List params) throws SQLException {
        Connection conn = acquireConnection();
        PreparedStatement preparedSt = conn.prepareStatement(preparedSql);
        for (int i = 0; i < params.size(); i++) {
            Object object = params.get(i);
            preparedSt.setObject(i + 1, object);
        }
        return preparedSt;
    }

    public synchronized void setAutoClose(boolean autoClose) {
        logger.warn("Attempting to modify the connection AUTO CLOSE type to: " + autoClose);
        this.autoClose = autoClose;
    }

    public synchronized void setAutoCommit(boolean autoCommit) {
        logger.warn("Attempting to modify the connection AUTO COMMIT type to: " + autoCommit);
        this.autoCommit = autoCommit;
    }

    /**
     * The very basic connection to access the whole
     *
     * @return database connection
     */
    protected abstract Connection acquireConnection() throws SQLException;

    /**
     * @throws SQLException
     */
    public void commitAndClose() throws SQLException {
        DbUtils.commitAndClose(connection);
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
     *
     * @throws SQLException
     */
    public void rollBack() throws SQLException {
        if (connection != null)
            connection.rollback();
    }

    /**
     * This method is always called after any transaction is executed.
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        DbUtils.close(connection);
        this.connection = null;
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
    public <T> T select(String query, final SimpleResultVisitor<T> resultVisitor, Object... parameters) throws SQLException {

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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isAutoClose() {
        return autoClose;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
}