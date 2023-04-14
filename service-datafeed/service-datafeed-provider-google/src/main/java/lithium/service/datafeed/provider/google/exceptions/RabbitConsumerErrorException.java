package lithium.service.datafeed.provider.google.exceptions;

public class RabbitConsumerErrorException extends Exception {
    public RabbitConsumerErrorException(String message) {
        super(message);
    }
}
