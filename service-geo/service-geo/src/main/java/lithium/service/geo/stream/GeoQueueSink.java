package lithium.service.geo.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GeoQueueSink {

	String INPUT = "geoinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
