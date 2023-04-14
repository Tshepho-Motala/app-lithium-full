package lithium.exceptions;

public class Status414LocaleNotFoundOrDisabledException extends NotRetryableErrorCodeException{
    public static final int CODE = 414;
    public Status414LocaleNotFoundOrDisabledException(String message) {
        super(CODE, message, Status414LocaleNotFoundOrDisabledException.class.getCanonicalName());
    }
}