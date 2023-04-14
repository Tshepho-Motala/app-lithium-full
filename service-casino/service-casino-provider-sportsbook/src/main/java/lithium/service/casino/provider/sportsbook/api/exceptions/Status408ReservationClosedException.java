package lithium.service.casino.provider.sportsbook.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status408ReservationClosedException extends NotRetryableErrorCodeException {
    public Status408ReservationClosedException() {
        super(408, "Reservation is closed", Status408ReservationClosedException.class.getCanonicalName());
    }
}
