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

package google.guice.aop0;

/**
 * @author Miguel Vega
 * @version $Id: VideoRental.java 0, 2012-11-30 6:02 PM mvega $
 */
public class VideoRental {

    public boolean rentMovie(long movieId) {

        System.out.println(
                String.format("Movie %s rented.", movieId));

        return true;
    }

    public boolean registerNewMovie(String name) {

        System.out.println(
                String.format("New movie \"%s\" registered.", name));

        return true;
    }
}