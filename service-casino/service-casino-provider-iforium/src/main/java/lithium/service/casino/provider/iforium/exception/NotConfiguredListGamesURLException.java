package lithium.service.casino.provider.iforium.exception;

public class NotConfiguredListGamesURLException extends RuntimeException {

    public NotConfiguredListGamesURLException() {
        super("listGamesUrl not configured");
    }
}
