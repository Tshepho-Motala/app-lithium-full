package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Status489PlayerExclusionNotFoundException extends NotRetryableErrorCodeException {
	public Status489PlayerExclusionNotFoundException() {
		super(489, "Player exclusion not found",
				Status489PlayerExclusionNotFoundException.class.getCanonicalName());
	}
}
