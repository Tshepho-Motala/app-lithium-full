package lithium.service.games.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GamesUpdateQueueSink {
	String INPUT = "gamesupdateinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
