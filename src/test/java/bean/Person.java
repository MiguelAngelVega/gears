/*
 *     JavaTools
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

package bean;

import lombok.Getter;
import lombok.Setter;
import org.openlogics.gears.jdbc.annotations.Column;

/**
 * @author Miguel Vega
 * @version $Id: Person.java 0, 2012-09-29 12:00 mvega $
 */
public class Person {
    @Getter @Setter
    @Column("STD_FNAME")
    protected String fname;
    @Getter @Setter
    @Column("STD_LNAME")
    protected String lname;
    @Getter @Setter
    @Column("STD_ID")
    protected long id;
}
