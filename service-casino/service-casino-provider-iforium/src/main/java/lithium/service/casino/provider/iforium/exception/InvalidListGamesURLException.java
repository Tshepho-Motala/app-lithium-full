package lithium.service.casino.provider.iforium.exception;

public class InvalidListGamesURLException extends RuntimeException {

    public InvalidListGamesURLException(String url, Throwable cause) {
        super(String.format("listGamesUrl=%s is invalid", url), cause);
    }
}
