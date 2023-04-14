package lithium.service.geo.neustar.exception;

public class InvalidIPV4AddressException extends RuntimeException {
    public InvalidIPV4AddressException(String ipv4) {
        super("Invalid IP address received in request : [" + ipv4 + "]");
    }
}
