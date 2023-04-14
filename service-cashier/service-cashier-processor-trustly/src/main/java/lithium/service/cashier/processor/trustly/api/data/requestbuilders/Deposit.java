package lithium.service.cashier.processor.trustly.api.data.requestbuilders;

import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.request.RequestParameters;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.DepositData;
import lithium.service.cashier.processor.trustly.api.data.request.requestdata.RecipientInformation;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Deposit {
    private final Request request = new Request();

    private Deposit(final Build builder) {
        final RequestParameters params = new RequestParameters();
        params.setUuid(UUID.randomUUID().toString());
        params.setData(builder.data);

        request.setMethod(Method.DEPOSIT);
        request.setParams(params);
    }

    public Request getRequest() {
        return request;
    }

    public static class Build {
        private final DepositData data = new DepositData();
        private final Map<String, Object> attributes = new TreeMap<>();

        public Build(final String notificationURL, final String endUserID, final String messageID, final String currency, final String firstName, final String lastName, final String email) {
            data.setNotificationURL(notificationURL);
            data.setEndUserID(endUserID);
            data.setMessageID(messageID);

            attributes.put("Currency", currency);
            attributes.put("Firstname", firstName);
            attributes.put("Lastname", lastName);
            attributes.put("Email", email);
            data.setAttributes(attributes);
        }

        public Build locale(final String locale) {
            attributes.put("Locale", locale);
            return this;
        }

        public Build method(final String method) {
            attributes.put("Method", method);
            return this;
        }

        public Build suggestedMinAmount(final String suggestedMinAmount) {
            attributes.put("SuggestedMinAmount", suggestedMinAmount);
            return this;
        }

        public Build suggestedMaxAmount(final String suggestedMaxAmount) {
            attributes.put("SuggestedMaxAmount", suggestedMaxAmount);
            return this;
        }

        public Build amount(final String amount) {
            attributes.put("Amount", amount);
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

        public Build urlScheme(final String urlScheme) {
            attributes.put("URLScheme", urlScheme);
            return this;
        }

        public Build urlTarget(final String urlTarget) {
            attributes.put("URLTarget", urlTarget);
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

        public Build recipientInformation(final RecipientInformation recipientInformation) {
            attributes.put("RecipientInformation", recipientInformation);
            return this;
        }

        public Build shopperStatement(final String shopperStatement) {
            attributes.put("ShopperStatement", shopperStatement);
            return this;
        }

        public Build shippingAddressCountry(final String shippingAddressCountry) {
            attributes.put("ShippingAddressCountry", shippingAddressCountry);
            return this;
        }

        public Build shippingAddressPostalCode(final String shippingAddressPostalCode) {
            attributes.put("ShippingAddressPostalCode", shippingAddressPostalCode);
            return this;
        }

        public Build shippingAddressCity(final String shippingAddressCity) {
            attributes.put("ShippingAddressCity", shippingAddressCity);
            return this;
        }

        public Build shippingAddressLine1(final String shippingAddressLine1) {
            attributes.put("ShippingAddressLine1", shippingAddressLine1);
            return this;
        }

        public Build shippingAddressLine2(final String shippingAddressLine2) {
            attributes.put("ShippingAddressLine2", shippingAddressLine2);
            return this;
        }

        public Build shippingAddress(final String shippingAddress) {
            attributes.put("ShippingAddress", shippingAddress);
            return this;
        }

        public Build requestDirectDebitMandate(final String requestDirectDebitMandate) {
            attributes.put("RequestDirectDebitMandate", requestDirectDebitMandate);
            return this;
        }

        public Build chargeAccountID(final String chargeAccountId) {
            attributes.put("ChargeAccountID", chargeAccountId);
            return this;
        }

        public Build quickDeposit(final String quickDeposit) {
            attributes.put("QuickDeposit", quickDeposit);
            return this;
        }

        public Request getRequest() {
            return new Deposit(this).getRequest();
        }
    }
}
