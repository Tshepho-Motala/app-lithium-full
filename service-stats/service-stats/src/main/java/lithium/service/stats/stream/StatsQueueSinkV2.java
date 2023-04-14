package lithium.service.stats.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface StatsQueueSinkV2 {

	String INPUT = "statsinputv2";

	@Input(INPUT)
	SubscribableChannel inputChannel();

}
