package lithium.service.cashier.processor.trustly.api.data.requestbuilders;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.request.RequestParameters;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.AccountPayoutData;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.SenderInformation;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class AccountPayout {
    private final Request request = new Request();

    private AccountPayout(final Build builder) {
        final RequestParameters params = new RequestParameters();
        params.setUuid(UUID.randomUUID().toString());
        params.setData(builder.data);

        request.setMethod(Method.ACCOUNT_PAYOUT);
        request.setParams(params);
    }

    public Request getRequest() {
        return request;
    }

    public static class Build {
        private final AccountPayoutData data = new AccountPayoutData();
        private final Map<String, Object> attributes = new TreeMap<>();

        public Build(final String notificationURL, final String endUserId, final String messageId, final String currency,
                      final String amount, final String accountId) {
            data.setNotificationUrl(notificationURL);
            data.setEnduserId(endUserId);
            data.setMessageId(messageId);
            data.setCurrency(currency);
            data.setAmount(amount);
            data.setAccountId(accountId);
            data.setAttributes(attributes);
        }

        public Build shopperStatement(final String shopperStatement) {
            attributes.put("ShopperStatement", shopperStatement);
            return this;
        }

        public Build senderInformation(final SenderInformation senderInformation) {
            attributes.put("SenderInformation", senderInformation);
            return this;
        }

        public Build country(final String countryISOCode) {
            attributes.put("Country", countryISOCode);
            return this;
        }

        public Request getRequest() {
            return new AccountPayout(this).getRequest();
        }
    }
}
