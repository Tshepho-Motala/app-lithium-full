package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status100InvalidInputDataException extends NotRetryableErrorCodeException {
	public Status100InvalidInputDataException() { super(100, "Invalid Input Data Provided", Status100InvalidInputDataException.class.getCanonicalName()); }
	public Status100InvalidInputDataException(String message) { super(100, message, Status100InvalidInputDataException.class.getCanonicalName()); }
}
