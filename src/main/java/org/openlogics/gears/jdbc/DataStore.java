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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.openlogics.gears.jdbc.map.BeanResultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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

    /**
     * Executes a simple callable statement
     *
     * @param query
     * @param <T>
     */
    public <T> void call(Query query) {

    }

    /**
     * Executes the given statement
     *
     * @param query
     * @param context
     * @param <T>
     */
    public <T> void update(Query query, T context) {

    }

    /**
     * Simple execution of the given statement
     *
     * @param query
     */
    public void update(Query query) {

    }

    /**
     * @param query
     * @param handler
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> T select(Query query, ResultSetHandler<? extends T> handler) throws SQLException {
        //a simple list to hold data about query
        List params = Lists.newLinkedList();
        try {
            String queryString = query.evaluateQueryString(this, params);
            return queryRunner(queryString, handler, params);
        } finally {
            params.clear();
        }
    }

    /**
     * @param query
     * @param resultType
     * @param visitor
     */
    public <T> void select(Query query, Class<?> resultType, ObjectResultSetHandler<? extends T> visitor) throws SQLException {
        List params = Lists.newLinkedList();
        BeanResultHandler toBeanResultHandler = new BeanResultHandler(visitor, resultType);
        String queryString = query.evaluateQueryString(this, params);
        try {
            queryRunner(queryString, toBeanResultHandler, params);
        } finally {
            params.clear();
        }
    }

    public <T> List<T> select(Query query, Class<T> type) throws SQLException {
        List params = Lists.newLinkedList();
        final ImmutableList.Builder<T> builder = new ImmutableList.Builder<T>();
        ObjectResultSetHandler<T> handler = new ObjectResultSetHandler<T>() {
            @Override
            public void handle(T result) throws SQLException {
                builder.add(result);
            }
        };
        BeanResultHandler<T> toBeanResultHandler = new BeanResultHandler<T>(handler, type);
        String queryString = query.evaluateQueryString(this, params);
        try {
            queryRunner(queryString, toBeanResultHandler, params);
        } finally {
            params.clear();
        }
        return builder.build();
    }

    /**
     * @param query
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> select(Query query) throws SQLException {
        final ImmutableList.Builder<Map<String, Object>> builder = new ImmutableList.Builder<Map<String, Object>>();
        ObjectResultSetHandler<Map<String, Object>> handler = new ObjectResultSetHandler<Map<String, Object>>() {
            @Override
            public void handle(Map<String, Object> result) throws SQLException {
                builder.add(result);
            }
        };
        BeanResultHandler toBeanResultHandler = new BeanResultHandler(handler, Map.class);
        List params = Lists.newLinkedList();
        String queryString = query.evaluateQueryString(this, params);

        try {
            queryRunner(queryString, toBeanResultHandler, params);
        } finally {
            params.clear();
            params = null;
        }
        return builder.build();
    }

    /**
     * This method evaluates the given query string and replaces the parameters marked as '?'
     * in the same order that the provided objects.
     *
     * @param query
     * @param resultVisitor
     * @param parameters
     * @param <T>
     * @return what visitor decides to return
     */
    public <T> T select(String query, final ResultSetHandler<T> resultVisitor, Object... parameters) throws SQLException {

        try {
            return queryRunner(query, new ResultSetHandler<T>() {
                @Override
                public T handle(ResultSet rs) throws SQLException {
                    return resultVisitor.handle(rs);
                }
            }, Lists.newLinkedList());
        } finally {
            closeConnection();
        }
    }

    private <E> E queryRunner(String query, ResultSetHandler<E> handler, List data)throws SQLException{
        QueryRunner qr = new QueryRunner();
        return qr.query(getConnection(), query, handler, data.toArray());
    }

    /**
     * Creates a new {@link PreparedStatement} object
     *
     * @param preparedSql
     * @param params
     * @return
     * @throws SQLException
     * @deprecated used in the older version of the CoreJavaBeans
     */
    @Deprecated
    private PreparedStatement prepareStatement(String preparedSql, List params) throws SQLException {
        Connection conn = acquireConnection();
        PreparedStatement preparedSt = conn.prepareStatement(preparedSql);
        for (int i = 0; i < params.size(); i++) {
            Object object = params.get(i);
            preparedSt.setObject(i + 1, object);
        }
        return preparedSt;
    }

    /**
     * @param preparedSt
     * @param handler
     * @param <T>
     * @return
     * @throws SQLException
     * @deprecated Used in an older version of teh CoreJavaBeans, but maybe useful yet, unitil find more features
     */
    @Deprecated
    public <T> T select(PreparedStatement preparedSt, ResultSetHandler<? extends T> handler) throws SQLException {
        logger.debug("Attempting to execute a preparedStatement QUERY: " + preparedSt + ", mapped to ");
        ResultSet rs = null;
        try {
            rs = preparedSt.executeQuery();
            return handler.handle(rs);
        } finally {
            if (rs != null) rs.close();
            DbUtils.close(preparedSt);
            if (isAutoClose()) {
                closeConnection();
            }
        }
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
     * This method was created for executing many transactions using a common connection, avoiding unnecessary connection openings.
     * DO NOT forget to close the connection when all processes ended.
     * @return database connection
     */
    public Connection getConnection() throws SQLException {
        this.connection = (connection != null && !connection.isClosed()) ? connection : acquireConnection();
        connection.setAutoCommit(autoCommit);
        return connection;
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