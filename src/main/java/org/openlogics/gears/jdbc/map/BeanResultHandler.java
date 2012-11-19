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

package org.openlogics.gears.jdbc.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.openlogics.gears.jdbc.ObjectResultVisitor;
import org.openlogics.gears.jdbc.ResultVisitor;
import org.openlogics.gears.jdbc.annotations.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Miguel Vega
 * @version $Id: BeanResultHandler.java 0, 2012-11-14 2:55 PM mvega $
 */
public class BeanResultHandler<T> implements ResultVisitor{

    Logger logger = Logger.getLogger(BeanResultHandler.class);

    private ObjectResultVisitor<T> visitor;
    private Class<T> requiredType;

    public BeanResultHandler(ObjectResultVisitor<T> visitor, Class<T> requiredType){
        this.visitor = visitor;
        this.requiredType = requiredType;
    }

    public static final BeanResultHandler buildMapListHandler(ObjectResultVisitor<Map<String, Object>> visitor){
        return new BeanResultHandler(visitor, Map.class);
    }

    @Override
    public Object visit(ResultSet rs) throws SQLException {
        while (rs.next()) {
            T obj = mapResultSet(rs, requiredType);
            visitor.visit(obj);
        }
        return null;
    }

    /**
     *
     * @param resultSet
     * @param requiredType the class to be used to map result
     * @return
     * @throws java.sql.SQLException
     */
    public T mapResultSet(ResultSet resultSet, Class<T> requiredType) throws SQLException {
        return this.mapResultSet(resultSet, false, requiredType);
    }

    /**
     *
     * @param resultSet
     * @param instantiate
     * @return
     * @throws SQLException
     */
    public T mapResultSet(ResultSet resultSet, Initializer<T> instantiate) throws SQLException {
        return this.mapResultSet(resultSet, false, instantiate);
    }

    /**
     *
     * @param resultSet
     * @param useColumnLabel this means that the column label will be used
     * instead of the columnName, some drivers support this feature
     * @param requiredType the class to be used to map result
     * @return
     * @throws SQLException
     */
    public T mapResultSet(ResultSet resultSet, boolean useColumnLabel, Class<T> requiredType) throws SQLException {
        return this.mapResultSet(resultSet, useColumnLabel, new Initializer<T>(requiredType));
    }

    /**
     *
     * @param resultSet
     * @param useColumnLabel
     * @param instantiate
     * @return
     * @throws SQLException
     */
    private T mapResultSet(ResultSet resultSet, boolean useColumnLabel, Initializer<T> instantiate) throws SQLException {
        try {
            //T obj = requiredType.newInstance();
            if (instantiate == null || instantiate.getType() == null) {
                throw new IllegalArgumentException("Initializer can not be null neither the type to instantiate.");
            }
            ResultSetMetaData rsmd = resultSet.getMetaData();
            Class requiredType = instantiate.getType();
            if (!Map.class.isAssignableFrom(requiredType)) {
                T obj = instantiate.newInstance(resultSet);
                //Adecuate RESULTS to BEAN struct
                List<Field> fields = getInheritedFields(requiredType);//requiredType.getDeclaredFields();
                for (Field field : fields) {
                    String metName = getSetterName(field.getName());
                    Method method = null;
                    String columnName = "";
                    try {
                        method = requiredType.getMethod(metName, field.getType());
                    } catch (NoSuchMethodException ex) {
                        //LOGGER.warn("Can't bind a result to method " + metName + " of class " + requiredType.getName());
                        continue;
                    } catch (SecurityException ex) {
                        //LOGGER.warn("Can't bind a result to method " + metName + " of class " + requiredType.getName());
                        continue;
                    }
                    Object value = null;
                    try {
                        Column c = field.getAnnotation(Column.class);
                        if (c != null) {
                            columnName = c.value().trim();
                        }
                        columnName = columnName.length() > 0 ? columnName : field.getName();

                        value = resultSet.getObject(columnName);
                        method.invoke(obj, value);
                    } catch (IllegalArgumentException ex) {
                        logger.debug("Type found in database is '"+value.getClass().getName()+"', but target object requires '"+field.getType().getName()+"': "+ex.getLocalizedMessage());
                        //if this is thrown the try to fix this error using the following:
                        //If is a big decimal, maybe bean has double or float attributes
                        try {
                            if (value instanceof BigDecimal || value instanceof Number) {
                                if (Double.class.isAssignableFrom(field.getType())
                                        || double.class.isAssignableFrom(field.getType())) {
                                    method.invoke(obj, ((BigDecimal) value).doubleValue());
                                    continue;
                                } else if (Float.class.isAssignableFrom(field.getType())
                                        || float.class.isAssignableFrom(field.getType())) {
                                    method.invoke(obj, ((BigDecimal) value).floatValue());
                                    continue;
                                } else if (Long.class.isAssignableFrom(field.getType())
                                        || long.class.isAssignableFrom(field.getType())) {
                                    method.invoke(obj, ((BigDecimal) value).longValue());
                                    continue;
                                } else {
                                    logger.warn("Tried to fix the mismatch problem, but couldn't: " + "Trying to inject an object of class " + value.getClass().getName() + " to an object of class " + field.getType());
                                }
                            } else if (value instanceof Date) {
                                Date dd = (Date) value;
                                if (java.sql.Date.class.isAssignableFrom(field.getType())) {
                                    method.invoke(obj, new java.sql.Date(dd.getTime()));
                                    continue;
                                } else if (Timestamp.class.isAssignableFrom(field.getType())) {
                                    method.invoke(obj, new Timestamp(dd.getTime()));
                                    continue;
                                } else if (Time.class.isAssignableFrom(field.getType())) {
                                    method.invoke(obj, new Time(dd.getTime()));
                                    continue;
                                }
                            }
                        } catch (IllegalArgumentException x) {
                            printIllegalArgumentException(x, field, value);
                        } catch (InvocationTargetException x) {
                            x.printStackTrace();
                        }
                        //throw new DataSourceException("Can't execute method " + method.getName() + " due to "+ex.getMessage(), ex);
                        logger.warn("Can't execute method " + method.getName() + " due to: " + ex.getMessage() + ".");
                    } catch (InvocationTargetException ex) {
                        //throw new DataSourceException("Can't inject an object into method " + method.getName(), ex);
                        logger.warn("Can't inject an object into method " + method.getName() + " due to: " + ex.getMessage());
                    } catch (SQLException ex) {
                        logger.warn("Target object has a field '" + columnName + "', this was not found in query results, "
                                + "this cause that attribute remains NULL or with default value.");
                    }
                }
                return obj;
            } else {
                ImmutableMap.Builder<String, Object> obj = new ImmutableMap.Builder<String, Object>();
                //Adecuate results to BEAN
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String column = useColumnLabel ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    obj.put(column, value);
                }
                return (T) obj.build();
            }
        } catch (IllegalAccessException ex) {
            throw new SQLException("Object of class " + instantiate.getType().getName() + " doesn't provide a valid access. It's possible be private or protected access only.", ex);
        }
    }

    void printIllegalArgumentException(IllegalArgumentException x, Field field, Object value) {
        x.printStackTrace();
        if (x.getMessage().trim().equals("argument type mismatch")) {
            logger.warn("Tried to fix the mismatch problem, but couldn't: "
                    + "Trying to inject an object of class " + value.getClass() + " to an object of class "
                    + field.getType());
        }
    }

    /**
     * This method retrieves all declared fields including those which belong to
     * the super classes.
     *
     * @param type
     * @return
     */
    public List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = Lists.newArrayList();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
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