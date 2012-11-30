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

package org.openlogics.gears.util;

import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Miguel Vega
 * @version $Id: StopWatchTest.java 0, 2012-11-30 4:29 PM mvega $
 */
public class StopWatchTest {
    @Test
    public void testNanos() throws InterruptedException {
        StopWatch t = StopWatch.milliSecStopWatch();
        t.start();
        Thread.sleep(1000);
        assertTrue(1000 <= t.lapTime());
        Thread.sleep(1000);
        assertTrue(1000 <= t.lapTime());
        Thread.sleep(2000);
        assertTrue(2000 <= t.lapTime());
    }
}
