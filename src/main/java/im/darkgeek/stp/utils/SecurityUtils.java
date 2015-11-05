package im.darkgeek.stp.utils;

import java.util.UUID;

/**
 * Created by justin on 15-11-5.
 */
public class SecurityUtils {
    public static String UUIDv4() {
        return
                UUID.randomUUID().toString();
    }
}
