package lithium.exceptions;

public class Status434AccountBlockedResponsibleGamingException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 434;

    public Status434AccountBlockedResponsibleGamingException(String message) {
        super(ERROR_CODE, message, Status434AccountBlockedResponsibleGamingException.class.getCanonicalName());
    }
}
