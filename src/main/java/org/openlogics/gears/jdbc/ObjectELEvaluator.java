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

import org.openlogics.gears.text.ELEvaluator;
import sun.rmi.runtime.Log;

import java.lang.reflect.Field;

/**
 * @author Miguel Vega
 * @version $Id: ObjectELEvaluator.java 0, 2012-10-05 01:23 mvega $
 */
public class ObjectELEvaluator implements ELEvaluator<Object, Object>{
    /**
     * Creates a new instance of MappedELEvaluator.
     */
    public MappedELEvaluator(){

    }

    @Override
    public Object evaluate(CharSequence text, Object context) {
        if (isWrapperType(context)) {
            return context;
        }

        StringTokenizer st = new StringTokenizer(text.toString(), ".");
        if(st.countTokens()==0){
            //simply return context
            return context;
        }
        else if (st.countTokens() == 1) {
            return getFinalValue(context, st.nextToken());
        } else {
            String s = st.nextToken();
            int offset = s.length() + 1;
            //make as the context the retrieved value
            Object ctxt = getFinalValue(context, s);
            return evaluate(text.subSequence(offset, text.length()), ctxt);
        }
    }
    public static boolean isWrapperType(Object context) {
        return context instanceof Number || context instanceof String ||
                context instanceof Boolean;
    }
    private Object getFinalValue(Object context, String attr) {
        if (context instanceof Map) {
            return ((Map) context).get(attr);
        }
        //use the reflection to retrieve the value
        String getter = null;
        try {
            //it's supposed to be n existing field in the given {@link bean}
            try {
                getter = getGetterName(context.getClass().getDeclaredField(attr));
            } catch (Exception x) {
                //think about some methods that don't are explicitly related to an attribute
                //there can exist many methods wich provide information but without
                //any attribute, think about "toString()" method
                //TODO, this is FORCED to be a "getXXX", not IS profixed
                getter = getGetterName(attr, String.class);
            }
            Method method = context.getClass().getMethod(getter);
            return method.invoke(context);
        } catch (Exception ex) {
            //throw new IllegalArgumentException("Object of class '" + context.getClass() + "' doesn't have the '" + getter + "' method to access data required.");
            Log.debug(ex.getMessage());
            ex.printStackTrace();
            return context;
        }

    }

    public static String getGetterName(Field field) {
        return getGetterName(field.getName(), field.getType());
    }

    public static String getGetterName(String fname, Class ftype) {
        String getterName = ftype.isAssignableFrom(Boolean.class) || ftype.isAssignableFrom(boolean.class) ? "is" : "get";
        getterName += fname.substring(0, 1).toUpperCase();
        getterName += fname.substring(1, fname.length());
        return getterName;
    }

    public static String getSetterName(String fieldName) {
        String setterName = "set";
        setterName += fieldName.substring(0, 1).toUpperCase();
        setterName += fieldName.substring(1, fieldName.length());
        return setterName;
    }
}
