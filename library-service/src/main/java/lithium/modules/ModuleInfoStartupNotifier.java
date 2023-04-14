package lithium.modules;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ModuleInfoStartupNotifier {

	@Output("moduleinfostartupoutput")
	public MessageChannel startupSend();
	
}
