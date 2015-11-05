package im.darkgeek.stp.connection;

import im.darkgeek.stp.utils.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by justin on 15-11-5.
 */
public class WebSocket {
    @org.eclipse.jetty.websocket.api.annotations.WebSocket(maxTextMessageSize = 64 * 1024)
    private static class ClientSocket {
        @SuppressWarnings("unused")
        private Session session;

        private Callback onCloseCallback;
        private Callback onConnectCallback;
        private Callback onMessageCallback;

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            if (onCloseCallback != null) {
                onCloseCallback.execute(statusCode + "-" + reason);
            }
        }

        @OnWebSocketConnect
        public void onConnect(Session session) {
            this.session = session;
            if (onConnectCallback != null) {
                onConnectCallback.execute(session);
            }
        }

        @OnWebSocketMessage
        public void onMessage(String msg) {
            if (onMessageCallback != null) {
                onMessageCallback.execute(msg);
            }
        }

        public void sendMsg(String msg) {
            if (session != null) {
                Future<Void> fut;
                fut = session.getRemote().sendStringByFuture(msg);
                try {
                    fut.get(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setOnCloseCallback(Callback onCloseCallback) {
            this.onCloseCallback = onCloseCallback;
        }

        public void setOnConnectCallback(Callback onConnectCallback) {
            this.onConnectCallback = onConnectCallback;
        }

        public void setOnMessageCallback(Callback onMessageCallback) {
            this.onMessageCallback = onMessageCallback;
        }
    }

    private WebSocketClient client;
    private ClientSocket socket;

    public void connect(String url, Callback<String, String> cb) {
        client = new WebSocketClient();
        socket = new ClientSocket();

        socket.setOnConnectCallback(cb);
        try {
            client.start();
            URI wsUri = new URI(url);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, wsUri, request);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void disconnect(Callback<String, String> cb) {
        try {
            socket.setOnCloseCallback(cb);
            client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (socket != null)
            socket.sendMsg(message);
    }

    public void onMessage(Callback<String, String> cb) {
        if (socket != null)
            socket.setOnMessageCallback(cb);
    }
}
