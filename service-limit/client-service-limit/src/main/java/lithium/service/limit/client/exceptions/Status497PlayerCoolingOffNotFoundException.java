package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Status497PlayerCoolingOffNotFoundException extends NotRetryableErrorCodeException {
	public Status497PlayerCoolingOffNotFoundException() {
		super(497, "Player cooling off not found",
			Status497PlayerCoolingOffNotFoundException.class.getCanonicalName());
	}
}
