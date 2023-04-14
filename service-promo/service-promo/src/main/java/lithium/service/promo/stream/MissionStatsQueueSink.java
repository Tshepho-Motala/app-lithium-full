package lithium.service.promo.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MissionStatsQueueSink {
	String INPUT = "missionstatsinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
