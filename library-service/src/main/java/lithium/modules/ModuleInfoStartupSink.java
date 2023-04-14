package lithium.modules;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ModuleInfoStartupSink {

	String INPUT = "moduleinfostartupinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
