package im.darkgeek.stp.actor;

import im.darkgeek.stp.connection.Http;
import im.darkgeek.stp.task.Analytics;
import im.darkgeek.stp.utils.Constants;
import im.darkgeek.stp.utils.SecurityUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by justin on 15-11-4.
 */
public class AppServer
    implements Runnable{
    public void run() {
        Set<String> endpoints = Constants.endpointMap.keySet();

        for (String ep : endpoints) {
            String channelID = Constants.endpointMap.get(ep);
            Client client = Constants.channelMap.get(channelID);
            Analytics analytics = new Analytics();

            analytics.setType("message_latency");
            analytics.setDescription("Collect message push latency information");
            analytics.setStartTime(new Date());
            client.getAnalyticsMap().put(analytics.getType(), analytics);

            sendVersion(ep);
            SecurityUtils.sleep(Constants.SLEEP_MILLISECOND);
        }
    }

    private void sendVersion(String endPoint) {
        Http http = new Http();
        Map<String, String> params = new HashMap<String, String>(1);

        params.put("version", SecurityUtils.randInt(1, 20).toString());

        try {
            http.put(endPoint, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
