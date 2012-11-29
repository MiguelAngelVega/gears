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
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.openlogics.gears.jdbc.map.BeanResultHandler;

import java.sql.*;
import java.util.List;
import java.util.Map;

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
    private int transactionIsolation = -1;

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
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public int[] update(BatchQuery query) throws SQLException {
        try {
            return query.getPreparedStatement().executeBatch();
        } finally {
            closeDBConn();
            query.clearCache();
        }
    }

    /**
     * Executes the given statement
     *
     * @param query
     */
    public int update(Query query) throws SQLException {
        List p = Lists.newLinkedList();
        String queryString = query.evaluateQueryString(this, p);
        try {
            return update(queryString, p);
        } finally {
            p.clear();
        }
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
            return query(queryString, handler, params);
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

        String queryString = query.evaluateQueryString(this, params);

        try {
            BeanResultHandler toBeanResultHandler = new BeanResultHandler(visitor, resultType);
            query(queryString, toBeanResultHandler, params);
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
            query(queryString, toBeanResultHandler, params);
            return builder.build();
        } finally {
            params.clear();
        }
    }

    /**
     * This method evaluates the given query string and replaces the parameters marked as '?'
     * in the same order parameters were provided.
     *
     * @param query
     * @param resultVisitor
     * @param parameters
     * @param <T>
     * @return what visitor decides to return
     */
    public <T> T select(String query, final ResultSetHandler<T> resultVisitor, Object... parameters) throws SQLException {

        return query(query, new ResultSetHandler<T>() {
            @Override
            public T handle(ResultSet rs) throws SQLException {
                return resultVisitor.handle(rs);
            }
        }, Lists.newLinkedList());

    }

    /**
     * Executes the given statement using the {@link org.apache.commons.dbutils.QueryRunner} class
     *
     * @param query
     * @param handler
     * @param data
     * @param <E>
     * @return
     * @throws SQLException
     */
    private <E> E query(String query, ResultSetHandler<E> handler, List data) throws SQLException {
        try {
            QueryRunner qr = new QueryRunner();
            return qr.query(getConnection(), query, handler, data.toArray());
        } finally {
            closeDBConn();
        }
    }

    /**
     * Executes the given statement using the {@link org.apache.commons.dbutils.QueryRunner} class
     *
     * @param query
     * @param data
     * @return
     * @throws SQLException
     */
    private int update(String query, List data) throws SQLException {
        try {
            QueryRunner qr = new QueryRunner();
            return qr.update(getConnection(), query, data.toArray());
        } finally {
            closeDBConn();
        }
    }

    /**
     * Creates a new {@link PreparedStatement} object
     *
     * @param preparedSql
     * @param params
     * @return
     * @throws SQLException
     */
    protected PreparedStatement newPreparedStatement(String preparedSql, List params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement preparedSt = conn.prepareStatement(preparedSql);
        return populatePreparedStatement(preparedSt, params);
    }

    protected PreparedStatement populatePreparedStatement(PreparedStatement preparedStatement, List params) throws SQLException {

        //preparedStatement.clearParameters();
        for (int i = 0; i < params.size(); i++) {
            Object object = params.get(i);
            preparedStatement.setObject(i + 1, object);
        }
        return preparedStatement;
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
    protected <T> T select(PreparedStatement preparedSt, ResultSetHandler<? extends T> handler) throws SQLException {
        logger.debug("Attempting to execute a preparedStatement QUERY: " + preparedSt + ", mapped to ");
        ResultSet rs = null;
        try {
            rs = preparedSt.executeQuery();
            return handler.handle(rs);
        } finally {
            if (rs != null) rs.close();
            DbUtils.close(preparedSt);
            if (isAutoClose()) {
                closeDBConn();
            }
        }
    }

    public synchronized void setAutoClose(boolean autoClose) {
        logger.warn("Attempting to modify the connection AUTO CLOSE type to: " + autoClose);
        this.autoClose = autoClose;
    }

    /**
     * This automatically disables the auto-commit and auto-closeable (if FALSE) features of the connection.
     * <br/><code>Warning!, Be sure to close the connection when finishing.</code>
     * @param autoCommit TRUE if commit per transaction is allowed
     */
    public void setAutoCommit(boolean autoCommit) {
        logger.warn("Attempting to modify the connection AUTO COMMIT type to: " + autoCommit+". This causes that auto close is disabled.");
        this.autoCommit = autoCommit;
        //if autocommit is false, is necessary that connection auto close becomes false
        this.autoClose = autoCommit==false?false:autoClose;
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
    public void commit() throws SQLException {
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
        if (connection != null){
            connection.rollback();
        }
    }

    /**
     *
     * @throws SQLException
     */
    public void rollBackAndClose() throws SQLException {
        DbUtils.rollbackAndClose(connection);
    }

    /**
     * This method was created for executing many transactions using a common connection, avoiding unnecessary connection openings.
     * DO NOT forget to close the connection when all processes ended.
     *
     * @return database connection
     */
    public Connection getConnection() throws SQLException {
        if(connection != null && !connection.isClosed()){
            return connection;
        }

        closeConnection();
        this.connection = acquireConnection();

        connection.setAutoCommit(autoCommit);
        if(transactionIsolation!=-1){
            connection.setTransactionIsolation(transactionIsolation);
            DatabaseMetaData dbmd = connection.getMetaData();
            logger.info("Attempting to use a TRANSACTION ISOLATION, '" + transactionIsolation + "', connection "+
                    (dbmd.supportsTransactionIsolationLevel(transactionIsolation)?"does NOT ":" does ")+ "support Isolation Level.");
        }
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

    private void closeDBConn() throws SQLException {
        if(autoClose){
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

    public void setTransactionIsolationNone() {
        this.transactionIsolation = -1;
    }
}