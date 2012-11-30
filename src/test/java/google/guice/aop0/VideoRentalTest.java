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
 * @version $Id: VideoRentalTest.java 0, 2012-11-30 6:04 PM mvega $
 */

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class VideoRentalTest {

    @Inject
    private VideoRental videoRental;

    @Before
    public void setup() {

        Injector injector = Guice.createInjector(new ExampleModule());
        injector.injectMembers(this);
    }

    @Test
    public void testRentMovie() throws Exception {

        assertTrue(videoRental.rentMovie(1));
    }

    @Test
    public void testRegisterNewMovie() throws Exception {

        assertTrue(videoRental.registerNewMovie("The Fugitive"));
    }

}