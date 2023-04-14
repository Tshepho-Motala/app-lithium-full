package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status422InvalidParameterProvidedException extends ErrorCodeException {
    public static final int CODE = 422;
    public Status422InvalidParameterProvidedException(String nameAndValueOfParameter) {
        super(CODE, "Invalid parameter provided: " + nameAndValueOfParameter, Status422InvalidParameterProvidedException.class.getCanonicalName());
    }
}
