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
 * @version $Id: ExampleModule.java 0, 2012-11-30 6:04 PM mvega $
 */

import com.google.inject.AbstractModule;

import static com.google.inject.matcher.Matchers.any;
import static com.google.inject.matcher.Matchers.subclassesOf;

public class ExampleModule extends AbstractModule {

    public void configure() {

        bindInterceptor(
                subclassesOf(VideoRental.class),
                any(),
                new TracingInterceptor());
    }
}