package im.darkgeek.stp.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by justin on 15-11-5.
 */
public class SecurityUtils {
    public static String UUIDv4() {
        return
                UUID.randomUUID().toString();
    }

    public static Integer randInt(int min, int max) {
        return
                ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
