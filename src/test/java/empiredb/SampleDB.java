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

package empiredb;

import org.apache.empire.db.DBDatabase;
import org.apache.empire.db.DBTable;
import org.apache.empire.db.DBTableColumn;

/**
 * @author Miguel Vega
 * @version $Id: SampleDB.java 0, 2012-12-06 12:11 PM mvega $
 */
public class SampleDB extends DBDatabase{
    public static class Departments extends DBTable{
        public DBTableColumn DEPARTMENT_ID;
        public Departments(DBDatabase db) {
            super("DEPARTMENTS", db);
        }
    }
    public static class Employees extends DBTable{
        public DBTableColumn EMPLOYEE_ID;
        public DBTableColumn LASTNAME;
        public DBTableColumn GENDER;
        public DBTableColumn DEPARTMENT_ID;
        public Employees(DBDatabase db) {
            super("EMPLOYEES", db);
        }
    }

    private Departments DEPARTMENTS = new Departments(this);
    private Employees EMPLOYEES = new Employees(this);

    public SampleDB() {
        addRelation(EMPLOYEES.DEPARTMENT_ID.referenceOn(DEPARTMENTS.DEPARTMENT_ID));
    }
}