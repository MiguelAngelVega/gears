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

import org.apache.log4j.Logger;
import org.openlogics.gears.text.ExpressionTransformer;
import org.openlogics.gears.text.ExpressionTransformerImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Miguel Vega
 * @version $Id: SQLExpressionTransformer.java 0, 2012-11-14 3:08 PM mvega $
 */
public class SQLExpressionTransformer implements ExpressionTransformer<String, Object>{
    static final String PATTERN = "[{][a-zA-Z0-9_.$]*[}]";
    private String prefix;

    /**
     *
     * @param prefix prefix used to delimit parameters in a query string
     */
    SQLExpressionTransformer(String prefix){
        if (prefix == null) {
            throw new IllegalArgumentException("SQL");
        }
        this.prefix = prefix;
    }

    /**
     * When a query string contains ${param}, this will be simply replaced by the object provided by the context object.
     * @return a transformer to transform static parameter replacement in query string.
     */
    public static final SQLExpressionTransformer buildStaticTransformer(){
        return new SQLExpressionTransformer("$");
    }

    /**
     * When a query string contains ${param}, this will be processed by the object provided by the context object.
     * @return
     */
    public static final SQLExpressionTransformer buildDynamicTransformer(){
        return new SQLExpressionTransformer("#");
    }

    @Override
    public String transform(String source, Object context) {
        return this.transform(source, context, new ParameterParsedHandler() {
            @Override
            public void handle(Object param) {
                throw new UnsupportedOperationException("Not implemented yet");
            }
        });
    }

    private String transform(CharSequence sequence, Object context, ParameterParsedHandler visitor) {
        return this.evaluateFixedParameter(sequence, context, null, visitor);
    }

    private String evaluateFixedParameter(CharSequence sequence, Object context, String fixedValue) {
        return this.evaluateFixedParameter(sequence, context, fixedValue, new ParameterParsedHandler() {
            @Override
            public void handle(Object param) {
            }
        });
    }

    /**
     * This method is exclusively developed for the {@link org.openlogics.gears.jdbc.map.BeanResultHandler}
     * class to make the query builder dynamic. So when using prepared
     * statements, must retrieve the values from the EL in QUERY and replace
     * them by '?' symbols in query string, and then add these values to the
     * PreparedStatement object.
     *
     * @param sequence
     * @param context
     * @param fixedValue
     * @param visitor
     * @return
     */
    public String evaluateFixedParameter(CharSequence sequence, Object context, String fixedValue, ParameterParsedHandler visitor) {

        Logger logger = Logger.getLogger(getClass());

        final String regex = "[" + prefix + "]" + PATTERN;
        Matcher matcher = Pattern.compile(regex).matcher(sequence);
        //start replace of variables ${}
        StringBuffer result = new StringBuffer();
        ExpressionTransformer jbe = new ExpressionTransformerImpl();
        while (matcher.find()) {
            String key = matcher.group().replace(prefix + "{", "").replace("}", "");
            int start = matcher.start();
            int end = matcher.end();
            try {
                //Object obj = getValueFromContext(context, key);
                Object obj = jbe.transform(key, context);
                matcher.appendReplacement(result, fixedValue != null ? fixedValue : String.valueOf(obj));
                visitor.handle(obj);
            } catch (IllegalArgumentException x) {
                logger.debug("ERROR ExperssionLanguage Evaluation:" + x.getMessage());
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
    protected interface ParameterParsedHandler {
        void handle(Object param);
    }
}