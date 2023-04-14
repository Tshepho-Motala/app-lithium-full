package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status445GeoLocationException extends RoxorErrorCodeException {
    public Status445GeoLocationException() {
        super(ErrorCategory.EC_445.category(), ErrorCategory.EC_445.displayMessage(), Status445GeoLocationException.class.getCanonicalName());
    }

    public Status445GeoLocationException(Object context) {
        super(ErrorCategory.EC_445.category(), ErrorCategory.EC_445.displayMessage(), context,
                Status445GeoLocationException.class.getCanonicalName());
    }
}
