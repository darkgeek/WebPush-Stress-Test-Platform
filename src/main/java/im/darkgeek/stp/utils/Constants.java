package im.darkgeek.stp.utils;

import im.darkgeek.stp.actor.Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by justin on 15-11-5.
 */
public class Constants {
    public final static String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Map<String, String> endpointMap = new ConcurrentHashMap<String, String>();

    public static Map<String, Client> channelMap = new ConcurrentHashMap<String, Client>();

    public static CountDownLatch clientsWorkDoneSignal;
}
