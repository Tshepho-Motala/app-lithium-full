package lithium.service.cashier.processor.trustly.api.data.requestbuilders;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.request.RequestParameters;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.SelectAccountData;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class SelectAccount {
    private final Request request = new Request();

    private SelectAccount(final Build builder) {
        final RequestParameters params = new RequestParameters();
        params.setUuid(UUID.randomUUID().toString());
        params.setData(builder.data);

        request.setMethod(Method.SELECT_ACCOUNT);
        request.setParams(params);
    }

    public Request getRequest() {
        return request;
    }

    public static class Build {
        private final SelectAccountData data = new SelectAccountData();
        private final Map<String, Object> attributes = new TreeMap<>();

        public Build(final String notificationURL, final String endUserID, final String messageID, final String firstName, final String lastName, final String email, final String dateOfBirth) {
            data.setNotificationUrl(notificationURL);
            data.setEndUserId(endUserID);
            data.setMessageId(messageID);

            attributes.put("Firstname", firstName);
            attributes.put("Lastname", lastName);
            attributes.put("Email", email);
            attributes.put("DateOfBirth", dateOfBirth);
            data.setAttributes(attributes);
        }

        public Build locale(final String locale) {
            attributes.put("Locale", locale);
            return this;
        }


        public Build country(final String countryISOCode) {
            attributes.put("Country", countryISOCode);
            return this;
        }

        public Build ip(final String IP) {
            attributes.put("IP", IP);
            return this;
        }

        public Build successURL(final String successURL) {
            attributes.put("SuccessURL", successURL);
            return this;
        }

        public Build failURL(final String failURL) {
            attributes.put("FailURL", failURL);
            return this;
        }

        public Build templateURL(final String templateURL) {
            attributes.put("TemplateURL", templateURL);
            return this;
        }

        public Build urlTarget(final String urlTarget) {
            attributes.put("URLTarget", urlTarget);
            return this;
        }

        public Build clearingHouse(final String clearingHouse) {
            attributes.put("ClearingHouse", clearingHouse);
            return this;
        }

        public Build mobilePhone(final String mobilePhone) {
            attributes.put("MobilePhone", mobilePhone);
            return this;
        }

        public Build nationalIdentificationNumber(final String nin) {
            attributes.put("NationalIdentificationNumber", nin);
            return this;
        }

        public Build urlScheme(final String urlScheme) {
            attributes.put("URLScheme", urlScheme);
            return this;
        }
        public Request getRequest() {
            return new SelectAccount(this).getRequest();
        }
    }
}
