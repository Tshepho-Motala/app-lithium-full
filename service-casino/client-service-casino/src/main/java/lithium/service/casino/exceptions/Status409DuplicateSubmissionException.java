package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class Status409DuplicateSubmissionException extends NotRetryableErrorCodeException {
    public final static int CODE = 409;
    public Status409DuplicateSubmissionException(String message) {
        super(CODE, "Duplicate submission: " + message, Status409DuplicateSubmissionException.class.getCanonicalName());
    }
}
