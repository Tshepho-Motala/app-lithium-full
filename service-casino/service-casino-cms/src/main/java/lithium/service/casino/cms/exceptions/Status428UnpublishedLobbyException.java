package lithium.service.casino.cms.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status428UnpublishedLobbyException extends NotRetryableErrorCodeException {
    private static final Integer CODE = 428;
    public Status428UnpublishedLobbyException(String message) {
        super(CODE, message, Status428UnpublishedLobbyException.class.getCanonicalName());
    }

    public Status428UnpublishedLobbyException(){
        this("Please publish lobby first before adding banners");
    }
}
