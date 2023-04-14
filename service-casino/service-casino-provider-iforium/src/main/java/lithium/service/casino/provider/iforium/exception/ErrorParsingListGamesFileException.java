package lithium.service.casino.provider.iforium.exception;

public class ErrorParsingListGamesFileException extends RuntimeException {

    public ErrorParsingListGamesFileException(Throwable cause) {
        super("Error parsing listGames url", cause);
    }
}
