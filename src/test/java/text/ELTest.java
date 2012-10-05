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
import bean.Teacher;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.openlogics.gears.text.ParamStringELvaluator;

import java.util.Map;

/**
 * @author Miguel Vega
 * @version $Id: ELTest.java 0, 2012-09-29 11:36 mvega $
 */
public class ELTest {
    @Test
    public void testSimpleType(){
        ParamStringELvaluator ele = new ParamStringELvaluator();
        Object o = ele.evaluate("$1", 1);
        Assert.assertEquals(o, 1);
    }
    @Test
    public void testMapType(){
        ParamStringELvaluator ele = new ParamStringELvaluator();
        Map<String, String> in = new ImmutableMap.Builder<String,String>().put("name", "mike").build();
        Object o = ele.evaluate("name", in);
        Assert.assertEquals(o, "mike");
    }
    @Test
    public void testBeanType(){
        ParamStringELvaluator ele = new ParamStringELvaluator();
        ClassRoom cr = new ClassRoom();
        Teacher t = new Teacher();
        t.setFname("Juan");
        t.setLname("Perez");
        cr.setTeacher(t);
        Object o = ele.evaluate("teacher.fname", cr);
        Assert.assertEquals(o, "Juan");
    }
}