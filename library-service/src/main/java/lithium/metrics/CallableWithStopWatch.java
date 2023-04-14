package lithium.metrics;

import org.springframework.util.StopWatch;

public interface CallableWithStopWatch<T> {

	public T call(StopWatch sw) throws Throwable;
	
}
