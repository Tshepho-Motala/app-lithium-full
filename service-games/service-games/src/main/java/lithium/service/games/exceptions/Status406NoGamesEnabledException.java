package lithium.service.games.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

    public class Status406NoGamesEnabledException extends NotRetryableErrorCodeException {
        public Status406NoGamesEnabledException(String message) {
            super(406, message, Status406NoGamesEnabledException.class.getCanonicalName());
        }
    }

