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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.System.nanoTime;

/**
 * @author Miguel Vega
 * @version $Id: StopWatch.java 0, 2012-11-30 4:10 PM mvega $
 */
public class StopWatch {
    private final static TimeUnit NANOS = TimeUnit.NANOSECONDS;

    private TimeUnit timeUnit;
    private long currentTime = 0;
    private List<Long> times = Lists.newLinkedList();

    StopWatch(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public static StopWatch nanoSecStopWatch(){
        return new StopWatch(TimeUnit.NANOSECONDS);
    }

    public static StopWatch milliSecStopWatch(){
        return new StopWatch(TimeUnit.MILLISECONDS);
    }

    public static StopWatch secStopWatch(){
        return new StopWatch(TimeUnit.SECONDS);
    }

    public void start() {
        this.currentTime = nanoTime();
    }

    public long lapTime() {
        //
        long now = nanoTime();
        long delay = now - currentTime;
        currentTime = now;
        try {
            return timeUnit.convert(delay, NANOS);
        } finally {
            times.add(delay);
        }
    }
}
