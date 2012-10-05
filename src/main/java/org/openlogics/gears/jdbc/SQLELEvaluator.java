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

import org.openlogics.gears.text.ParamStringELvaluator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Miguel Vega
 * @version $Id: SQLELEvaluator.java 0, 2012-10-05 01:11 mvega $
 */
public class SQLELEvaluator extends ParamStringELvaluator{
    static final String PATTERN = "[{][a-zA-Z0-9_.$]*[}]";
    private String prefix;

    /**
     * Creates a new instance of SQLELEvaluator.
     */
    public SQLELEvaluator() {
        this("$");
    }

    public SQLELEvaluator(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("The Patter can not be a NULL String.");
        }
        this.prefix = pattern;
    }

    /**
     *
     * @param sequence the text to evaluate
     * @param context the Object wich holds data about the parameters to parse.
     * A Map or a simple Bean can be sent.
     * @return
     */
    public String evaluate(CharSequence sequence, Object context) {
        return this.evaluate(sequence, context, new ReadParameterVisitor<Object>() {

            @Override
            public void visit(Object grabbed) {
            }
        });
    }

    public String evaluate(CharSequence sequence, Object context, ReadParameterVisitor visitor) {
        return this.evaluateFixedParameter(sequence, context, null, visitor);
    }

    public String evaluateFixedParameter(CharSequence sequence, Object context, String fixedValue) {
        return this.evaluateFixedParameter(sequence, context, fixedValue, new ReadParameterVisitor<Object>() {
            @Override
            public void visit(Object obj) {
            }
        });
    }

    /**
     * Thi methos is exclusively developed for the {@link org.openlogics.gears.jdbc.ResultMapper}
     * class to make the quey builder dynamic. So when using prepared
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
    public String evaluateFixedParameter(CharSequence sequence, Object context, String fixedValue, ReadParameterVisitor visitor) {
        String regex = "[" + prefix + "]" + PATTERN;
        Matcher matcher = Pattern.compile(regex).matcher(sequence);
        //start replace of variables ${}
        StringBuffer result = new StringBuffer();
        ObjectResultVisitor jbe = new ObjectResultVisitor();
        while (matcher.find()) {
            String key = matcher.group().replace(prefix + "{", "").replace("}", "");
            int start = matcher.start();
            int end = matcher.end();
            try {
                //Object obj = getValueFromContext(context, key);
                Object obj = jbe.evaluate(key, context);
                matcher.appendReplacement(result, fixedValue != null ? fixedValue : String.valueOf(obj));
                visitor.visit(obj);
            } catch (IllegalArgumentException x) {
                Log.debug("ERROR ExperssionLanguage Evaluation:" + x.getMessage());
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
