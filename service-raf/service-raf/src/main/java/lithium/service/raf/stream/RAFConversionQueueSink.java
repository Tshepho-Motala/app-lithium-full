package lithium.service.raf.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RAFConversionQueueSink {
	String INPUT = "rafconversioninput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}