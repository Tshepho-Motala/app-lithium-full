package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status411InvalidUserGuidException extends ErrorCodeException {
    public static final int CODE = 411;
    public Status411InvalidUserGuidException(String userGuid) {
        super(CODE, "An invalid user guid was supplied: " + userGuid, Status411InvalidUserGuidException.class.getCanonicalName());
    }
}
