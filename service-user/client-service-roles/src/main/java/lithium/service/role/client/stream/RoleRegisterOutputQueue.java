package lithium.service.role.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface RoleRegisterOutputQueue {

	@Output("rolesregisteroutput")
	public MessageChannel outputQueue();
	
}
