package lithium.service.cashier.processor.trustly.api.data.requestbuilders;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.request.RequestParameters;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.WithdrawData;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Withdraw {
    private final Request request = new Request();

    private Withdraw(final Build builder) {
        final RequestParameters params = new RequestParameters();
        params.setUuid(UUID.randomUUID().toString());
        params.setData(builder.data);

        request.setMethod(Method.WITHDRAW);
        request.setParams(params);
    }

    public Request getRequest() {
        return request;
    }

    public static class Build {
        private final WithdrawData data = new WithdrawData();
        private final Map<String, Object> attributes = new TreeMap<>();

        public Build(final String notificationURL, final String endUserID, final String messageID, final String currency, final String firstName, final String lastName, final String email, final String dateOfBirth) {
            data.setNotificationUrl(notificationURL);
            data.setEndUserId(endUserID);
            data.setMessageId(messageID);
            data.setCurrency(currency);

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

        public Build suggestedMinAmount(final String suggestedMinAmount) {
            attributes.put("SuggestedMinAmount", suggestedMinAmount);
            return this;
        }

        public Build suggestedAmount(final String suggestedAmount) {
            attributes.put("SuggestedAmount", suggestedAmount);
            return this;
        }

        public Build suggestedMaxAmount(final String suggestedMaxAmount) {
            attributes.put("SuggestedMaxAmount", suggestedMaxAmount);
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

        public Build bankNumber(final String bankNumber) {
            attributes.put("BankNumber", bankNumber);
            return this;
        }

        public Build accountNumber(final String accountNumber) {
            attributes.put("AccountNumber", accountNumber);
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

        public Build addressCountry(final String addressCountry) {
            attributes.put("AddressCountry", addressCountry);
            return this;
        }

        public Build addressPostalCode(final String addressPostalCode) {
            attributes.put("AddressPostalcode", addressPostalCode);
            return this;
        }

        public Build addressCity(final String addressCity) {
            attributes.put("AddressCity", addressCity);
            return this;
        }

        public Build addressLine1(final String addressLine1) {
            attributes.put("AddressLine1", addressLine1);
            return this;
        }

        public Build addressLine2(final String addressLine2) {
            attributes.put("AddressLine2", addressLine2);
            return this;
        }

        public Build address(final String address) {
            attributes.put("Address", address);
            return this;
        }

        public Request getRequest() {
            return new Withdraw(this).getRequest();
        }
    }
}
