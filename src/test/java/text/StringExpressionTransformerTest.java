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

package text;

import pojo.ClassRoom;
import pojo.Student;
import pojo.Teacher;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.openlogics.gears.text.ExpressionTransformerImpl;

import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Miguel Vega
 * @version $Id: StringExpressionTransformerTest.java 0, 2012-09-29 11:36 mvega $
 */
public class StringExpressionTransformerTest {
    @Test
    public void testPrimitiveType(){
        ExpressionTransformerImpl ele = new ExpressionTransformerImpl();
        Object o = ele.transform("", 1);
        Assert.assertEquals(o, 1);
    }

    @Test
    public void testDictionary(){
        ExpressionTransformerImpl ele = new ExpressionTransformerImpl();
        Hashtable<String, String> in = new Hashtable<String, String>();
        in.put("name", "mike");
        Object o = ele.transform("name", in);
        Assert.assertEquals(o, "mike");
    }

    @Test
    public void testMapType(){
        ExpressionTransformerImpl ele = new ExpressionTransformerImpl();
        Map<String, String> in = new ImmutableMap.Builder<String,String>().put("name", "mike").build();
        String o = ele.transform("name", in);
        Assert.assertEquals(o, "mike");
    }

    @Test
    public void testBeanFromMap(){
        ExpressionTransformerImpl ele = new ExpressionTransformerImpl();
        Student st = new Student();
        st.setFname("mike");
        Map<String, Student> in = new ImmutableMap.Builder<String,Student>().put("st", st).build();
        Student o = ele.transform("st", in);
        Assert.assertEquals(o.getFname(), "mike");
    }

    @Test
    public void testPrimitiveFromBean(){
        ExpressionTransformerImpl ele = new ExpressionTransformerImpl();
        ClassRoom cr = new ClassRoom();
        Teacher t = new Teacher();
        t.setFname("Juan");
        t.setLname("Perez");
        cr.setTeacher(t);
        Object o = ele.transform("teacher.fname", cr);
        Assert.assertEquals(o, "Juan");
    }

    @Test
    public void testBenaFromBean(){
        ExpressionTransformerImpl ele = new ExpressionTransformerImpl();
        ClassRoom cr = new ClassRoom();
        Teacher t = new Teacher();
        t.setFname("Juan");
        t.setLname("Perez");
        cr.setTeacher(t);
        Teacher o = (Teacher) ele.transform("teacher", cr);
        assertTrue(o instanceof Teacher);
        assertEquals("Juan", o.getFname());
    }
}