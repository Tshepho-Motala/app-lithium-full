package lithium.service.role.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.role.client.objects.Role;
import lithium.service.translate.client.objects.Translation;

@Service
public class RoleRegisterStream {
	
	@Autowired
	private RoleRegisterOutputQueue channel;

	public void registerRole(Role role) {
		channel.outputQueue().send(MessageBuilder.<Role>withPayload(role).build());
	}
}