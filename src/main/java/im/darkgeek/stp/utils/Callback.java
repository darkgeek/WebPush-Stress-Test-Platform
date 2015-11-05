package im.darkgeek.stp.utils;

/**
 * Created by justin on 15-11-5.
 */
public interface Callback<T, V> {
    T execute(V param);
}
