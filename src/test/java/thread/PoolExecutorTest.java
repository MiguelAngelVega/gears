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

package thread;

import com.google.common.io.ByteStreams;
import com.sun.istack.internal.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.openlogics.gears.util.StopWatch;

import java.net.URL;
import java.util.concurrent.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Miguel Vega
 * @version $Id: PoolExecutorTest.java 0, 2012-11-30 3:15 PM mvega $
 */
public class PoolExecutorTest {
    private Logger logger = Logger.getLogger(PoolExecutorTest.class);

    /**
     * The test will attempts to download a page in lte 5000 ms, as this wont happen, a TimoutException will be thrown
     * even when task inside callable is completed
     */
    @Test(expected = TimeoutException.class)
    public void testCallableTimeout() throws TimeoutException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 2, 3000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        CountDownLatch latch = new CountDownLatch(1);

        final String surl = "http://www.netbeans.org";

        final StopWatch timer = StopWatch.milliSecStopWatch();

        //this can be a single connection to a web site, where this returns the wbe page contents
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                timer.start();
                try {
                    URL url = new URL(surl);
                    return new String(ByteStreams.toByteArray(url.openStream()));
                } finally {
                    System.out.println("Elapsed time..."+(timer.lapTime()+" milliseconds"));
                }
            }
        });

        if (!future.isCancelled()) {
            System.out.println("The page has been downloaded successfully!!");
            try {
                //we'll give only 1000 milliseconds to process the call, if call is not achieved, a {@link TimeoutException} is thrown
                System.out.println(future.get(500, TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                throw new IllegalStateException("This should never happens");
            } catch (ExecutionException e) {
                throw new IllegalStateException("This should never happens");
            } catch (TimeoutException e) {
                throw e;
            }
        }else{
            throw new IllegalStateException("This should never happens");
        }
    }

    /**
     * Test of a Callable which will terminate its given task
     */
    @Test
    public void testCallableCompleted(){
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 2, 3000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        CountDownLatch latch = new CountDownLatch(1);

        final String surl = "http://www.netbeans.org";

        final StopWatch timer = StopWatch.milliSecStopWatch();

        //this can be a single connection to a web site, where this returns the wbe page contents
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                timer.start();
                try {
                    URL url = new URL(surl);
                    return new String(ByteStreams.toByteArray(url.openStream()));
                } finally {
                    logger.info("Elapsed time..."+(timer.lapTime()+" milliseconds"));
                }
            }
        });


        if (!future.isCancelled()) {
            logger.info("The page has been downloaded successfully!!");
            try {
                //will wait 'till the process finishes, then just do not pass Time parameter, so Timeout will never occurs
                assertNotNull(future.get());
                assertTrue(future.isDone());
                assertFalse(future.isCancelled());
            } catch (InterruptedException e) {
                throw new IllegalStateException("This should never happens");
            } catch (ExecutionException e) {
                throw new IllegalStateException("This should never happens");
            }
        }else{
            throw new IllegalStateException("This should never happens");
        }
    }
}
