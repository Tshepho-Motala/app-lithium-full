package lithium.metrics;

import lombok.extern.slf4j.Slf4j;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Use these static methods anywhere inside either a {@link LithiumMetricsTimer} closure or a
 * method annotated with {@link TimeThisMethod} in order to mark specific steps within a timer for the purpose
 * of debugging long running functions.
 *
 * @see TimeThisMethod
 */
@Slf4j
public class SW {

    private static final ThreadLocal<Stack<StopWatch>> threadLocalStorage = ThreadLocal.withInitial(Stack::new);

    public static StopWatch storeInThread(StopWatch value) {
        threadLocalStorage.get().push(value);
        return value;
    }

    public static StopWatch getFromThreadLocal() {
        try {
            return threadLocalStorage.get().peek();
        } catch (EmptyStackException ex) {
            throw new RuntimeException("No StopWatch in thread. Is your method covered by @TimeThisMethod?");
        }
    }

    public static StopWatch getFromThreadLocalAllowNull() {
        if (threadLocalStorage.get().isEmpty()) return null;
        return threadLocalStorage.get().peek();
    }

    public static void removeFromThread() {
        threadLocalStorage.get().pop();
    }

    public static void start(String activity) {
        StopWatch sw = getFromThreadLocal();
        try {
            sw.start(activity);
        } catch (IllegalStateException ie) {
            log.warn("StopWatch already started with activity " + getFromThreadLocal().currentTaskName());
            sw.stop();
            sw.start(activity);
        }
    }

    public static void stop() {
        getFromThreadLocal().stop();
    }

    private SW() {}
}
