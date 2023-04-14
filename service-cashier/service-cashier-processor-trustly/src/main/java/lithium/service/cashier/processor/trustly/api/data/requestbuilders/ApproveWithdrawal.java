package lithium.service.cashier.processor.trustly.api.data.requestbuilders;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.request.RequestParameters;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.ApproveWithdrawalData;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class ApproveWithdrawal {
    private final Request request = new Request();

    private ApproveWithdrawal(final Build builder) {
        final RequestParameters params = new RequestParameters();
        params.setUuid(UUID.randomUUID().toString());
        params.setData(builder.data);

        request.setMethod(Method.APPROVE_WITHDRAWAL);
        request.setParams(params);
    }

    public Request getRequest() {
        return request;
    }

    public static class Build {
        private final ApproveWithdrawalData data = new ApproveWithdrawalData();
        private final Map<String, Object> attributes = new TreeMap<>();

        public Build(final String userName, final String password, final String orderID) {
            data.setUsername(userName);
            data.setPassword(password);
            data.setOrderID(orderID);
            data.setAttributes(attributes);
        }

        public Request getRequest() {
            return new ApproveWithdrawal(this).getRequest();
        }
    }
}
