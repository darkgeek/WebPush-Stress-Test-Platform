package im.darkgeek.stp.actor;

import im.darkgeek.stp.task.Analytics;
import im.darkgeek.stp.utils.AlgorithmUtils;
import im.darkgeek.stp.utils.Constants;

import java.util.concurrent.CountDownLatch;

/**
 * Created by justin on 15-11-5.
 */
public class Container {
    private static int CLIENT_COUNT = 160;
    public static void main(String[] args) throws InterruptedException {
        Constants.clientsWorkDoneSignal = new CountDownLatch(CLIENT_COUNT);
        Constants.analyticsDoneSignal = new CountDownLatch(CLIENT_COUNT);

        for (int i = 0; i < CLIENT_COUNT; i++) {
            Client client = new Client("ws://127.0.0.1:3000/webpush");
            Thread thread = new Thread(client);

            thread.start();
        }
        Constants.clientsWorkDoneSignal.await();

        Thread asThread = new Thread(new AppServer());
        asThread.start();
        Constants.analyticsDoneSignal.await();

        // Calculate the medium latency
        AlgorithmUtils.MedianFinder medianFinder = new AlgorithmUtils.MedianFinder();
        for (Analytics analytics : Constants.analyticses) {
            long latency = analytics.getEndTime().getTime() - analytics.getStartTime().getTime();

            medianFinder.addNum((int) latency);
        }
        System.out.println("Medium: " + medianFinder.findMedian());
    }
}
