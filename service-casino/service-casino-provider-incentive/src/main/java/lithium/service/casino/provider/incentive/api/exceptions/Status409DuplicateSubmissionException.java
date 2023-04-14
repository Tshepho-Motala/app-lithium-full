package lithium.service.casino.provider.incentive.api.exceptions;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class Status409DuplicateSubmissionException extends NotRetryableErrorCodeException {
    public Status409DuplicateSubmissionException(String message) {
        super(HttpStatus.CONFLICT.value(), "Duplicate submission: " + message, Status409DuplicateSubmissionException.class.getCanonicalName());
    }
}
