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
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * This class allows to instantiate a new object using reflection, based on the parameters provided.
 * Parameters will be used to describe the
 * @author Miguel Vega
 * @version $Id: Initializer.java 0, 2012-11-15 4:59 PM mvega $
 */
public class Initializer<T> {
    Class<T> type;
    List<Field> types = Lists.newLinkedList();
    Class[] params;

    Logger logger = Logger.getLogger(Initializer.class);

    public Initializer(String className) throws ClassNotFoundException {
        this((Class<T>) Class.forName(className));
    }

    public Initializer(Class<T> type) {
        this.type = type;
    }

    /**
     * Provide information about the parameters of the constructor based on the
     * constructor parameters. <b><i>It's important to provide the attributes in
     * teh same order as constructor has them</i></b> attribute names
     *
     * @param fieldName
     */
    public void addParameter(String fieldName) {
        params = null;
        try {
            Field field = type.getDeclaredField(fieldName);
            types.add(field);
        } catch (Exception ex) {
            logger.error("Unable to find attribute '" + fieldName + "', due to:" + ex.getMessage());
        }
    }

    /**
     * Avoid the wasted use of recreate once and twice...
     *
     * @return
     */
    public Class[] getParamTypes() {
        if (params == null) {
            params = new Class[types.size()];
            for (int i = 0; i < params.length; i++) {
                params[i] = types.get(i).getType();
            }
        }
        return params;
    }

    public Class getType() {
        return type;
    }

    public T newInstance(ResultSet results) throws SQLException {
        Object args[] = new Object[types.size()];
        for (int i = 0; i < args.length; i++) {
            //retreive the name of the column in the table deined as annotations fr entity
            Field field = types.get(i);
            //args[i] = ResultMapper.readObject(results, field);
            args[i] = results.getObject(field.getName());
        }
        Class[] ptypes = getParamTypes();
        Constructor<T> c;
        try {
            c = type.getConstructor(ptypes);
            return c.newInstance(args);
        } catch (Exception ex) {
            logger.error("An exception occurred while attempting to instantiate an object.");
            throw new SQLException(ex);
        }
    }
}
