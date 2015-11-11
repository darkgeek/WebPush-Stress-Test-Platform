package im.darkgeek.stp.utils;

import im.darkgeek.stp.actor.Client;
import im.darkgeek.stp.task.Analytics;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

/**
 * Created by justin on 15-11-5.
 */
public class Constants {
    public final static String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static int CLIENT_COUNT = 500;

    public static int SLEEP_MILLISECOND = 10;

    public static int PUSH_SERVER_PID = 2836;

    public static String PUSH_SERVER_ADDRESS = "127.0.0.1";

    public static int PUSH_SERVER_PORT = 3000;

    public static int MONITOR_PORT = 9002;

    public static Map<String, String> endpointMap = new ConcurrentHashMap<String, String>();

    public static Map<String, Client> channelMap = new ConcurrentHashMap<String, Client>();

    public static Queue<Analytics> analyticses = new ConcurrentLinkedDeque<Analytics>();

    public static CountDownLatch clientsWorkDoneSignal, analyticsDoneSignal;
}
