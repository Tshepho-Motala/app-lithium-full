package lithium.service.changelog.stream;

import lithium.service.changelog.services.UserService;
import lithium.service.user.client.objects.UserAttributesData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(UserAttributesTriggerQueueSink.class)
public class UserAttributesTriggerQueueProcessor {
	@Autowired
	private UserService service;

	@StreamListener(UserAttributesTriggerQueueSink.INPUT)
	public void trigger(UserAttributesData data) {
		log.debug("Received an user attributes trigger from the queue for processing: " + data);

		service.processUserAttributesData(data);
	}
}
