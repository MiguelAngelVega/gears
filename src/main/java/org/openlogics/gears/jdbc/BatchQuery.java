package org.openlogics.gears.jdbc;

import com.google.common.collect.Lists;
import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Used for executing batch statements.
 * @author Miguel Vega
 * @version $Id: BatchQuery.java 0, 2012-11-21 20:36 mvega $
 */
public class BatchQuery <E> extends Query{
    private PreparedStatement preparedStatement;
    private DataStore dataStore;

    public BatchQuery(String queryString, DataStore dataStore) {
        super(queryString);
        this.dataStore = dataStore;
    }

    public <E> BatchQuery addBatch(E ctx) throws SQLException {
        assert ctx!=null;
        List list = Lists.newLinkedList();
        this.context = ctx;
        String plainQuery = super.evaluateQueryString(dataStore, list);
        if(preparedStatement==null){
            preparedStatement= dataStore.newPreparedStatement(plainQuery, list);
        }
        else {
            //preparedSt.clearParameters();
            preparedStatement = dataStore.populatePreparedStatement(preparedStatement, list);
        }
        preparedStatement.addBatch();
        return this;
    }

    protected PreparedStatement getPreparedStatement(){
        return preparedStatement;
    }

    /**
     * Executes the batch, and clears cache to prevent from memory leaks
     */
    public void clearCache() throws SQLException {
        DbUtils.close(preparedStatement);
        this.preparedStatement = null;
        this.dataStore = null;
    }
}