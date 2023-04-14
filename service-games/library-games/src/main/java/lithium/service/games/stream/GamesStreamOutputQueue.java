package lithium.service.games.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GamesStreamOutputQueue {
	@Output("gamesupdateoutput")
	public MessageChannel channel();
}