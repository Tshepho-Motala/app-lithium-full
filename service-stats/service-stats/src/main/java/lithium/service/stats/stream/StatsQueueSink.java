package lithium.service.stats.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface StatsQueueSink {

	String INPUT = "statsinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
