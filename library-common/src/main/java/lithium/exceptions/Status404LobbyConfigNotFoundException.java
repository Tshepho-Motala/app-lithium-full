package lithium.exceptions;

public class Status404LobbyConfigNotFoundException extends NotRetryableErrorCodeException {
  public static final int CODE = 404;
  public Status404LobbyConfigNotFoundException(String message) {
    super(CODE, message, Status404LobbyConfigNotFoundException.class.getCanonicalName());
  }
}