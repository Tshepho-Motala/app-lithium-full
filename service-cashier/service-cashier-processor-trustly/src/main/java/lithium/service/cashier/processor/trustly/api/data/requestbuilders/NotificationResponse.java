package lithium.service.cashier.processor.trustly.api.data.requestbuilders;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.ResponseStatus;
import lithium.service.cashier.processor.trustly.api.data.response.Result;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;

import java.util.HashMap;
import java.util.Map;

public class NotificationResponse {
    private final TrustlyResponse response = new TrustlyResponse();

    private NotificationResponse(final Build builder) {
        final Result result = new Result();
        result.setUuid(builder.uuid);
        result.setData(builder.data);
        result.setMethod(builder.method);

        response.setResult(result);
        response.setVersion("1.1");
    }

    public TrustlyResponse getResponse() {
        return response;
    }

    public static class Build {
        private final Map<String, Object> data = new HashMap<>();
        final String uuid;
        final Method method;

        public Build(final Method method, final String uuid, final ResponseStatus status) {
            this.uuid = uuid;
            this.method = method;
            data.put("status", status);
        }

        public TrustlyResponse getResponse() {
            return new NotificationResponse(this).getResponse();
        }
    }
}
