package im.darkgeek.stp.actor;

import im.darkgeek.stp.task.Analytics;
import im.darkgeek.stp.utils.AlgorithmUtils;
import im.darkgeek.stp.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.CountDownLatch;

/**
 * Created by justin on 15-11-5.
 */
public class Container {
    public static void main(String[] args) throws InterruptedException {
        processCommandLine(args);
        Constants.clientsWorkDoneSignal = new CountDownLatch(Constants.CLIENT_COUNT);
        Constants.analyticsDoneSignal = new CountDownLatch(Constants.CLIENT_COUNT);

        for (int i = 0; i < Constants.CLIENT_COUNT; i++) {
            Client client = new Client("ws://127.0.0.1:3000/webpush");
            Thread thread = new Thread(client);

            thread.start();
        }
        Constants.clientsWorkDoneSignal.await();

        Thread asThread = new Thread(new AppServer());
        asThread.start();

        Monitor monitor = new Monitor();
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();
        monitor.startMonitor(Constants.PUSH_SERVER_PID);
        Constants.analyticsDoneSignal.await();
        monitor.stopMonitor();
//        monitor.quitMonitor();

        // Calculate the medium latency
        AlgorithmUtils.MedianFinder medianFinder = new AlgorithmUtils.MedianFinder();
        int count = 0;
        for (Analytics analytics : Constants.analyticses) {
            long latency = analytics.getEndTime().getTime() - analytics.getStartTime().getTime();

            medianFinder.addNum((int) latency);
            count++;
        }
        System.out.println("Medium: " + medianFinder.findMedian());
        System.out.println("Count: " + count);
        System.exit(0);
    }

    private static void processCommandLine(String[] args) {
        if (args.length == 0)
            return;

        if (args.length < 6) {
            System.out.println("Usage: java -jar " + getJarFilePath() + "" +
                    " [client_count] [sleep_millisecond] [push_server_pid] [push_server_address]" +
                    " [push_server_port] [monitor_server_port]");
            System.exit(1);
        }

        try {
            Constants.CLIENT_COUNT = Integer.parseInt(args[0]);
            Constants.SLEEP_MILLISECOND = Integer.parseInt(args[1]);
            Constants.PUSH_SERVER_PID = Integer.parseInt(args[2]);
            Constants.PUSH_SERVER_ADDRESS = args[3];
            Constants.PUSH_SERVER_PORT = Integer.parseInt(args[4]);
            Constants.MONITOR_PORT = Integer.parseInt(args[5]);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid parameter value type: expect int.");
            System.exit(2);
        }
    }

    private static String getJarFilePath() {
        String path = Container.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = "";
        try {
            decodedPath = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decodedPath;
    }
}
