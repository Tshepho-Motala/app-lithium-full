package lithium.service.geo.neustar.exception;

public class IPV4AddressNotFoundException extends RuntimeException {
    public IPV4AddressNotFoundException(String ipv4) {
        super("The URI doesn’t correspond to any known resource, or the referenced IP address is not mapped in the " +
                "current data set : [" + ipv4 + "]");
    }
}
