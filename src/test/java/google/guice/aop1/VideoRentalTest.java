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

package google.guice.aop1;

/**
 * @author Miguel Vega
 * @version $Id: VideoRentalTest.java 0, 2012-11-30 6:10 PM mvega $
 */
import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.HashSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class VideoRentalTest {

    @Inject
    private VideoRental videoRental;

    @Inject
    private UserManager userManager;

    private static User customer;

    private static User employee;

    @BeforeClass
    public static void setupUsers() {

        customer = new User("Peter", EnumSet.of(Role.CUSTOMER));
        employee = new User("Bob", EnumSet.of(Role.EMPLOYEE));
    }

    @Before
    public void setup() {

        ExampleModule module = new ExampleModule();

        Injector injector = Guice.createInjector(module);
        for (Object oneInjectee : module.getInjectees()) {
            injector.injectMembers(oneInjectee);
        }

        injector.injectMembers(this);
    }

    @Test
    public void testRentMovieSuccessfully() throws Exception {

        userManager.setCurrentUser(customer);
        assertTrue(videoRental.rentMovie(1));
    }

    @Test(expected = IllegalStateException.class)
    public void testRentMovieFailing() throws Exception {

        userManager.setCurrentUser(employee);
        videoRental.rentMovie(1);
    }

    @Test
    public void testRegisterNewMovieSuccessfully() throws Exception {

        userManager.setCurrentUser(employee);
        assertTrue(videoRental.registerNewMovie("The Fugitive"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterNewMovieFailing() throws Exception {

        userManager.setCurrentUser(customer);
        assertTrue(videoRental.registerNewMovie("The Fugitive"));
    }
}