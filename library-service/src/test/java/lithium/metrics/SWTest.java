package lithium.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class SWTest {
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Test
    void testNoTimer() {
        Assertions.assertThrows(RuntimeException.class, () -> SW.start("noTimer"),
                "No StopWatch in thread. Is your method covered by @TimeThisMethod?");
    }

    @Test
    void testSingleTimer() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("singleTimer", sw -> {
            SW.start("singleTimer");
            sleep(2);
            SW.stop();
        });
    }

    @Test
    void testTimerWithOldAndNewAPI() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("testTimerWithManyStopwatches", sw -> {
            SW.start("1");
            sleep(2);
            SW.stop();
            SW.start("2");
            sleep(2);
            SW.stop();
            sw.start("3");
            sleep(2);
            sw.stop();
        });
    }

    void testTimerWithOutOfOrderSW() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("testTimerWithManyStopwatches", sw -> {
            SW.start("1");
            SW.stop();
            SW.start("2");
            sleep(2);
            sw.start("3");
            SW.stop();
            sw.stop();
        });
    }

    @Test
    void testNestedStopwatches() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("testNestedStopwatches", sw -> {
            SW.start("1");

            LithiumMetricsTimer timer2 = createTimer();
            timer2.time("testNestedStopwatches2", sw2 -> {
                SW.start("2");
                sleep(2);
                SW.stop();
            });

            SW.stop();
        });
    }

    @Test
    void testNestedStopwatchesNewAndOldAndNew() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("testNestedStopwatchesNewAndOldAndNew", sw -> {
            SW.start("1");

            LithiumMetricsTimer timer2 = createTimer();
            timer2.time("testNestedStopwatchesNewAndOldAndNew2", sw2 -> {
                sw2.start("2");
                LithiumMetricsTimer timer3 = createTimer();
                timer3.time("testNestedStopwatchesNewAndOldAndNew3", sw3 -> {
                    SW.start("3");
                    sleep(2);
                    SW.stop();
                });
                sw2.stop();
            });

            SW.stop();
        });
    }

    @Test
    void testNestedStopwatchesOldAndNewAndOld() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("testNestedStopwatchesOldAndNewAndOld", sw -> {
            sw.start("1");

            LithiumMetricsTimer timer2 = createTimer();
            timer2.time("testNestedStopwatchesOldAndNewAndOld2", sw2 -> {
                SW.start("2");
                LithiumMetricsTimer timer3 = createTimer();
                timer3.time("testNestedStopwatchesOldAndNewAndOld3", sw3 -> {
                    SW.start("3");
                    sleep(2);
                    SW.stop();
                });
                SW.stop();
            });

            sw.stop();
        });
    }

    @Test
    void testNestedCallsToSWWithoutTimers() throws Exception {
        LithiumMetricsTimer timer = createTimer();
        timer.time("testNestedCallsToSWWithoutTimers", sw -> {
            SW.start("1");
            SW.start("1.1");
            sleep(2);
            SW.stop();
            SW.stop();
        });
    }

    private LithiumMetricsTimer createTimer() {
        return new LithiumMetricsTimer(meterRegistry, log, 100, 40, 10);
    }

    private void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException i) {}
    }
}
