package im.darkgeek.stp.actor;

import im.darkgeek.stp.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by justin on 15-11-11.
 */
public class Monitor
    implements Runnable{
    private volatile String message;

    public void run() {
        try (
            Socket monitorSocket = new Socket(Constants.PUSH_SERVER_ADDRESS, Constants.MONITOR_PORT);
            PrintWriter out = new PrintWriter(monitorSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()));
        ) {
            while (true) {
                if (message != null) {
                    out.println(message);
                    System.out.println("sent: " + message);
                    System.out.println("get reply: " + in.readLine());
                    message = null;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(String msg) {
        message = msg;
    }

    public void startMonitor(int pid) {
        sendMsg("start " + pid);
    }

    public void stopMonitor() {
        sendMsg("stop");
    }

    public void quitMonitor() {
        sendMsg("quit");
    }
}
