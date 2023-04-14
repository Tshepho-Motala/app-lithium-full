package lithium.service.user.search.stream;


import lithium.service.user.client.objects.RestrictionData;

import lithium.service.user.search.services.limit.UserRestrictionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(UserRestrictionTriggerQueueSink.class)
public class UserRestrictionTriggerQueueProcessor {
	@Autowired private UserRestrictionsService service;

	@StreamListener(UserRestrictionTriggerQueueSink.INPUT)
	public void trigger(RestrictionData data) {
		log.debug("Received an user-restriction trigger from the queue for processing: " + data);
		
		service.processUserRestrictionRuleSets(data);
	}
}
