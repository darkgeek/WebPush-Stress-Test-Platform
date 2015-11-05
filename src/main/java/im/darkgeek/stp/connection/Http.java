package im.darkgeek.stp.connection;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by justin on 15-11-5.
 */
public class Http {
    public String get(String url) throws IOException {
        return Request.Get(url)
                .execute().returnContent().asString();
    }

    public String post(String url, Map<String, String> params) throws IOException {
        if (params == null)
            return "";

        return Request.Post(url)
                .bodyForm(gatherParams(params))
                .execute().returnContent().asString();
    }

    public String put(String url, Map<String, String> params) throws IOException {
        if (params == null)
            return "";

        return Request.Put(url)
                .bodyForm(gatherParams(params))
                .execute().returnContent().asString();
    }

    private Iterable gatherParams(Map<String, String> params) {
        Form data = Form.form();
        Set<String> keys = params.keySet();

        for (String k : keys) {
            data.add(k, params.get(k));
        }

        return data.build();
    }
}
