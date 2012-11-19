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

import com.google.common.base.Strings;

import java.util.List;

/**
 * Query object is delegated to transform the queryString against parameters
 * @author Miguel Vega
 * @version $Id: Query.java 0, 2012-10-05 4:03 PM mvega $
 */
public class Query{
    private String queryString;
    private Object context;
    /**
     * Default constructor to build simple queries
     * @param queryString
     */
    public Query(String queryString){
        this(queryString, null);
    }

    /**
     * Constructor which supports SQL statement plus the context of where to retrieve data from
     * @param queryString a supported SQL statement. Can contains ?, #{}, or ${} keywords
     * @param context the object to retrieve data from
     */
    public Query(String queryString, Object context){
        this.queryString = queryString;
        this.context = context;
    }

    @Override
    public String toString(){
        return queryString;
    }

    /**
     * Evaluate the given query string and replaces the parameters with a symbol for prepared statementt,
     * thus while replacing those characters, the values for each parameter is processed and retrieved for
     * placing them into the prepared statement object.
     *
     * @param dataStore
     * @param data
     * @return
     */
    protected String evaluateQueryString(DataStore dataStore, final List data) {
        if (Strings.isNullOrEmpty(queryString)) {
            throw new IllegalArgumentException("SQL Statement found is NULL");
        }
        //Before threat all the complexQuery replace the ${ds_schema}
        if (!Strings.isNullOrEmpty(dataStore.getSchema())) {
            queryString = queryString.replace("${schema}", dataStore.getSchema());
        }
        if (context == null) {
            return queryString;
        }

        SQLExpressionTransformer el = SQLExpressionTransformer.buildStaticTransformer();
        //simple access
        queryString = el.transform(queryString, context);
        //System.out.println("Evaluated........1....."+complexQuery);
        el = SQLExpressionTransformer.buildDynamicTransformer();
        //when got an input-required, visitor visits with the item found
        queryString = el.evaluateFixedParameter(queryString, context, "?", new SQLExpressionTransformer.ParameterParsedHandler() {
            @Override
            public void handle(Object obj) {
                data.add(obj);
            }
        });
        return queryString;
    }
}