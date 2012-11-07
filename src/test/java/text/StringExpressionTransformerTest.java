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

import bean.ClassRoom;
import bean.Student;
import bean.Teacher;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.openlogics.gears.text.StringExpressionTransformer;

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
        StringExpressionTransformer ele = new StringExpressionTransformer();
        Object o = ele.evaluate("", 1);
        Assert.assertEquals(o, 1);
    }

    @Test
    public void testDictionary(){
        StringExpressionTransformer ele = new StringExpressionTransformer();
        Hashtable<String, String> in = new Hashtable<String, String>();
        in.put("name", "mike");
        Object o = ele.evaluate("name", in);
        Assert.assertEquals(o, "mike");
    }

    @Test
    public void testMapType(){
        StringExpressionTransformer ele = new StringExpressionTransformer();
        Map<String, String> in = new ImmutableMap.Builder<String,String>().put("name", "mike").build();
        String o = ele.evaluate("name", in);
        Assert.assertEquals(o, "mike");
    }

    @Test
    public void testBeanFromMap(){
        StringExpressionTransformer ele = new StringExpressionTransformer();
        Student st = new Student();
        st.setFname("mike");
        Map<String, Student> in = new ImmutableMap.Builder<String,Student>().put("st", st).build();
        Student o = ele.evaluate("st", in);
        Assert.assertEquals(o.getFname(), "mike");
    }

    @Test
    public void testPrimitiveFromBean(){
        StringExpressionTransformer ele = new StringExpressionTransformer();
        ClassRoom cr = new ClassRoom();
        Teacher t = new Teacher();
        t.setFname("Juan");
        t.setLname("Perez");
        cr.setTeacher(t);
        Object o = ele.evaluate("teacher.fname", cr);
        Assert.assertEquals(o, "Juan");
    }

    @Test
    public void testBenaFromBean(){
        StringExpressionTransformer ele = new StringExpressionTransformer();
        ClassRoom cr = new ClassRoom();
        Teacher t = new Teacher();
        t.setFname("Juan");
        t.setLname("Perez");
        cr.setTeacher(t);
        Teacher o = (Teacher) ele.evaluate("teacher", cr);
        assertTrue(o instanceof Teacher);
        assertEquals("Juan", o.getFname());
    }
}