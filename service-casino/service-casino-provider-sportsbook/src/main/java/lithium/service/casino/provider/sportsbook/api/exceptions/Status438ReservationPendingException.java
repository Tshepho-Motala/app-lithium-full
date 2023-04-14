package lithium.service.casino.provider.sportsbook.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status438ReservationPendingException extends NotRetryableErrorCodeException {
    public Status438ReservationPendingException() {
        super(438, "Reservation pending. Please retry after a while.", Status438ReservationPendingException.class.getCanonicalName());
    }
}
