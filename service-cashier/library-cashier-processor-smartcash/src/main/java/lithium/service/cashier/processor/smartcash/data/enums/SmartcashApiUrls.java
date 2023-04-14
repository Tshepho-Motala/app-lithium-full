package lithium.service.cashier.processor.smartcash.data.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum SmartcashApiUrls {
    AUTH_URL("/auth/oauth2/token"),
    DEPOSIT_CUSTOMER_SEARCH_URL("/merchant/v1/payments/user/instruments?msisdn={msisdn}"),
    DEPOSIT_GET_TRANSACTION_URL("/merchant/v1/payments/{id}?authentication_medium=USSD_PUSH"),
    DEPOSIT_URL("/merchant/v1/payments/"),

    WITHDRAW_CUSTOMER_SEARCH_URL("/standard/v1/disbursements/user/instruments?msisdn={msisdn}"),
    WITHDRAW_GET_TRANSACTION_URL("/standard/v1/disbursements/{id}"),
    WITHDRAW_URL("/standard/v1/disbursements/");

    private String url;

    public String getUrl() {
        return url;
    }
}
