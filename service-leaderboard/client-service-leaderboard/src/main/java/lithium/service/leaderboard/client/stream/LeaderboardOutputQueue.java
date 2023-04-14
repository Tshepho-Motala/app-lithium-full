package lithium.service.leaderboard.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface LeaderboardOutputQueue {
	@Output("leaderboardoutput")
	public MessageChannel outputQueue();
}