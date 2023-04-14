package lithium.cache;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CacheClearMessageSink {

	String INPUT = "clearcacheinput";
	
	@Input(INPUT)
	SubscribableChannel input();
	
}
