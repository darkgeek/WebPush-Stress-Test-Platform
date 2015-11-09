package im.darkgeek.stp.actor;

import im.darkgeek.stp.connection.WebSocket;
import im.darkgeek.stp.task.Analytics;
import im.darkgeek.stp.utils.Callback;
import im.darkgeek.stp.utils.Constants;
import im.darkgeek.stp.utils.JsonUtils;
import im.darkgeek.stp.utils.SecurityUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by justin on 15-11-4.
 */
public class Client
    implements Runnable{
    private CountDownLatch doneSignal = new CountDownLatch(1);
    private WebSocket webSocket;
    private String wsUrl;
    private Client thisClient;
    private Map<String, Analytics> analyticsMap = new HashMap<String, Analytics>(1);

    public static class MessageBody {
        public String messageType;
        public String uaid;
        public List<String> channelIDs;
        public String channelID;
        public Integer status;
        public String pushEndpoint;
        public List<Update> updates;
        public static class Update {
            public String channelID;
            public Integer version;
        }
    }

    public Client(String url) {
        wsUrl = url;
        thisClient = this;
        webSocket = new WebSocket();
        Callback<String, String> onMessage = new Callback<String, String>() {
            public String execute(String param) {
                System.out.println("Get message: " + param);
                MessageBody messageBody = (MessageBody) JsonUtils.fromJson(param, MessageBody.class);
                String msgType = messageBody.messageType;

                if ("hello".equals(msgType)) {
                    // Ends the handshake
                    doneSignal.countDown();
                } else if ("register".equals(msgType)) {
                    Constants.endpointMap.put(messageBody.pushEndpoint, messageBody.channelID);
                    Constants.channelMap.put(messageBody.channelID, thisClient);
                    // Ends the register
                    doneSignal.countDown();
                    Constants.clientsWorkDoneSignal.countDown();
                } else if ("notification".equals(msgType)) {
                    Analytics latencyAnalytics = analyticsMap.get("message_latency");
                    latencyAnalytics.setEndTime(new Date());
                    Constants.analyticses.add(latencyAnalytics);
                    Constants.analyticsDoneSignal.countDown();
                    ack(messageBody.updates);
                }
                return null;
            }
        };
        webSocket.onMessage(onMessage);
    }

    public void run() {
        connect();
        await();
        handshake();
        await();
        register();
    }

    private void connect() {
        Callback<String, String> onConnected = new Callback<String, String>() {
            public String execute(String param) {
                doneSignal.countDown();
                return null;
            }
        };
        Callback<String, String> onDisconnected = new Callback<String, String>() {
            public String execute(String param) {
                System.out.println("Disconnected: " + param);
                return null;
            }
        };
        webSocket.onDisconnect(onDisconnected);
        webSocket.connect(wsUrl, onConnected);
    }

    private void handshake() {
        MessageBody messageBody = new MessageBody();
        messageBody.messageType = "hello";
        messageBody.uaid = "empty";

        String message = JsonUtils.toJson(messageBody);
        webSocket.sendMessage(message);
    }

    private void register() {
        MessageBody messageBody = new MessageBody();
        messageBody.messageType = "register";
        messageBody.channelID = SecurityUtils.UUIDv4();

        String message = JsonUtils.toJson(messageBody);
        webSocket.sendMessage(message);
    }

    private void ack(List<MessageBody.Update> updates) {
        MessageBody messageBody = new MessageBody();
        messageBody.messageType = "ack";
        messageBody.updates = updates;

        String message = JsonUtils.toJson(messageBody);
        webSocket.sendMessage(message);
    }

    private void await() {
        try {
            doneSignal = new CountDownLatch(1);
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Analytics> getAnalyticsMap() {
        return analyticsMap;
    }
}
