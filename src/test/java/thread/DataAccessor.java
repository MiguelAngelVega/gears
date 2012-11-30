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

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Miguel Vega
 * @version $Id: DataAccessor.java 0, 2012-11-30 2:29 PM mvega $
 */
public class DataAccessor {
    private static ThreadPoolExecutor executor;
    private int timeOut = 5000;

    static {
        executor = new ThreadPoolExecutor(10, 10, 5000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));
    }

    public static void main(String[] args) {
        List<String> requests = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            requests.add("request:" + i);
        }

        DataAccessor da = new DataAccessor();

        List<ProcessedResponse> results = da.getDataFromService(requests);

        for (ProcessedResponse result : results) {
            System.out.println("Response: " + result);
        }

        executor.shutdown();
    }

    private List<ProcessedResponse> getDataFromService(List<String> requests) {
        CountDownLatch latch = new CountDownLatch(requests.size());
        List<SubmittedJob> sjobs = Lists.newArrayListWithCapacity(requests.size());

        for (String request : requests) {
            Future<ProcessedResponse> future = executor.submit(new GetNProcessResponse(request, latch));
            sjobs.add(new SubmittedJob(future, request));
        }

        boolean isAllDone = false;

        try {
            isAllDone = latch.await(timeOut, TimeUnit.MILLISECONDS);
            if (!isAllDone) {
                System.out.println("Some jobs not done...");
            }
        } catch (InterruptedException e) {
            //take care of clean up
            for (SubmittedJob sjob : sjobs) {
                sjob.getFuture().cancel(true);
            }
        }

        List<ProcessedResponse> results = Lists.newLinkedList();
        for (SubmittedJob sjob : sjobs) {
            try {
                if (!isAllDone && !sjob.getFuture().isDone()) {
                    //cancel job and continue with others
                    sjob.getFuture().cancel(true);
                    continue;
                }
                ProcessedResponse res = sjob.getFuture().get();
                results.add(res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return results;  //To change body of created methods use File | Settings | File Templates.
    }


    @Data
    class SubmittedJob {
        final String request;
        final Future<ProcessedResponse> future;

        public SubmittedJob(final Future<ProcessedResponse> future, final String request) {
            this.request = request;
            this.future = future;
        }
    }

    @Data
    class ProcessedResponse {
        private String request, response;

        ProcessedResponse(String request, String response) {
            this.request = request;
            this.response = response;
        }
    }

    class GetNProcessResponse implements Callable<ProcessedResponse> {
        private String request;
        private CountDownLatch countDownLatch;

        GetNProcessResponse(String request, CountDownLatch countDownLatch) {
            this.request = request;
            this.countDownLatch = countDownLatch;
        }


        @Override
        public ProcessedResponse call() throws Exception {
            try {
                return getAndProcessRequest(request);
            } finally {
                countDownLatch.countDown();
            }
        }

        private ProcessedResponse getAndProcessRequest(String request) {
            //do the service call
            //...
            if ("request:3".equals(request)) {
                throw new RuntimeException("runtime");
            }
            return new ProcessedResponse(request, "Response to - >" + request);
        }
    }
}
