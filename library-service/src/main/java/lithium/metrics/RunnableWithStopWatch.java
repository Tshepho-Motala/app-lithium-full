package lithium.metrics;

import org.springframework.util.StopWatch;

public interface RunnableWithStopWatch {

	public void run(StopWatch sw) throws Throwable;

}
