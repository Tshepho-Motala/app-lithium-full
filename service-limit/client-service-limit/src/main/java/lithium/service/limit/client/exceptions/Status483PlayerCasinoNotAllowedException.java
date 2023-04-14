package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status483PlayerCasinoNotAllowedException extends NotRetryableErrorCodeException {
    public static final int CODE = 483;
    public Status483PlayerCasinoNotAllowedException() {
        super(CODE, "Player casino not allowed", null, Status483PlayerCasinoNotAllowedException.class.getCanonicalName());
    }

    public Status483PlayerCasinoNotAllowedException(String message) {
        super(CODE, message, Status483PlayerCasinoNotAllowedException.class.getCanonicalName());
    }
}
