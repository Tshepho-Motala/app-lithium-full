package lithium.service.limit.client.stream;

import lithium.service.user.client.objects.RestrictionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserRestrictionTriggerStream {
	@Autowired private UserRestrictionTriggerOutputQueue queue;

	public void trigger(RestrictionData data) {
		queue.outputQueue().send(MessageBuilder.<RestrictionData>withPayload(data).build());
	}
}
