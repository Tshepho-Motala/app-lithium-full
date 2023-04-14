package lithium.service.user.client.stream;

import lithium.service.user.client.objects.AutoRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserEventsStream {
	@Autowired
	private UserEventsStreamOutputQueue channel;
	
	public void processUserEvent(Long id, User user, String type, String message, String data, Boolean received) {
		processUserEvent(
			UserEvent.builder()
			.id(id)
			.user(user)
			.type(type)
			.message(message)
			.data(data)
			.received(received)
			.build()
		);
	}
	
	public void processUserEvent(UserEvent userEvent) {
		try {
			channel.userEventsChannel().send(MessageBuilder.<UserEvent>withPayload(userEvent).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}

	public void processUserRegistrationSuccessEvent(AutoRegistration registrationSuccess) {
		try {
			channel.userRegistrationSuccessChannel().send(MessageBuilder.withPayload(registrationSuccess).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}