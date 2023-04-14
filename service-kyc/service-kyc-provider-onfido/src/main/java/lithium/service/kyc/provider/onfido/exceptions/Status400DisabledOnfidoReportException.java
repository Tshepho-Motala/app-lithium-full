package lithium.service.kyc.provider.onfido.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status400DisabledOnfidoReportException  extends NotRetryableErrorCodeException {
    public Status400DisabledOnfidoReportException(String message) {
        super(400, message, Status400DisabledOnfidoReportException.class.getCanonicalName());
    }
}
