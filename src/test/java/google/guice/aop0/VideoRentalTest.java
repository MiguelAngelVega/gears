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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openlogics.gears.jdbc.annotations.TransactionObservable;

import static org.junit.Assert.assertTrue;

public class VideoRentalTest {

    @Inject
    private VideoRental videoRental;

    @Before
    public void setup() {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            public void configure() {
                bindInterceptor(
                        //subclassesOf(VideoRental.class),
                        //any(),

                        Matchers.any(),
                        Matchers.annotatedWith(TransactionObservable.class),

                        new TracingInterceptor());
            }
        });
        //uses the default empty constructor to inject an instance of VideoRental into this test class
        injector.injectMembers(this);

        //another way of doing it might be the following
        /*
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            public void configure() {
                bindInterceptor(
                        subclassesOf(VideoRental.class),
                        any(),
                        new TracingInterceptor());

                bind(VideoRental.class).toInstance(new VideoRental(1));
            }
        });
        injector.injectMembers(this);
        */
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