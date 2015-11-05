package im.darkgeek.stp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by justin on 15-11-5.
 */
public class JsonUtils {
    public static String toJson(Object o) {

        return createGson().toJson(o);
    }

    public static Object fromJson(String json, Class clazz) {
        return createGson().fromJson(json, clazz);
    }

    private static Gson createGson() {
        return
                new GsonBuilder().setDateFormat(Constants.FULL_DATE_FORMAT).create();
    }
}
