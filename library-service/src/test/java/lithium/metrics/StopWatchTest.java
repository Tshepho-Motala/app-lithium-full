package lithium.metrics;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class StopWatchTest {

    @Test
    public void testStopWatch() {
        StopWatch sw = new StopWatch("1");
        sw.start("1");
        sleep(5);
        sw.stop();
        log.info(sw.prettyPrint());
    }

    @Test
    public void testNested() {
        StopWatch sw = new StopWatch("1");
        sw.start("1");
        StopWatch sw2 = new StopWatch("2", sw);
        sw2.start("2");
        sleep(5);
        sw2.stop();
        sw.stop();
        log.info(sw.prettyPrint());
    }

    @Test
    public void testAutomaticNesting() {
        StopWatch sw = new StopWatch("1");
        sw.start("1");
        sw.start("2");
        sleep(5);
        sw.stop();
        sw.stop();
        log.info(sw.prettyPrint());
    }

    @Test(expected = IllegalStateException.class)
    public void testAutomaticNestingTooManyStops() {
        StopWatch sw = new StopWatch("1");
        sw.start("1");
        sw.start("2");
        sleep(5);
        sw.stop();
        sw.stop();
        sw.stop();
    }

    private void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException i) {}
    }

}