package lithium.service.report.client.players.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.util.ExceptionMessageUtil;

public class Status551ServiceReportClientException extends NotRetryableErrorCodeException {
    public Status551ServiceReportClientException(String message) {
        super(551, message, Status551ServiceReportClientException.class.getCanonicalName());
    }

    public Status551ServiceReportClientException(LithiumServiceClientFactoryException e) {
        super(551, ExceptionMessageUtil.allMessages(e), e, Status551ServiceReportClientException.class.getCanonicalName());
    }
}
