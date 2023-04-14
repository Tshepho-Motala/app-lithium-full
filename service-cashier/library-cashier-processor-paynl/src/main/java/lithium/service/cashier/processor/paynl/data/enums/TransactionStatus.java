package lithium.service.cashier.processor.paynl.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum TransactionStatus {

    CANCEL("-90", "CANCEL", "The payment has been cancelled."),
    EXPIRED("-80", "EXPIRED","The payment has expired."),
    REFUNDING("-72", "REFUNDING", "The payment will be refunded (you can still cancel this)."),
    REFUND("-81", "REFUND","The payment has been refunded."),
    PENDING("20", "PENDING", "The final status of the payment is not yet known."),
    PENDING_V2("20", "PENDING", "The final status of the payment is not yet known."),
    PENDING_V3("25", "PENDING", "The final status of the payment is not yet known."),
    PENDING_V4("50", "PENDING", "The final status of the payment is not yet known."),
    VERIFY("85", "VERIFY", "The payment is treated as suspicious. The status needs to be determined by means of an additional check."),
    AUTHORIZE("95", "AUTHORIZE", "The payment has been reserved and can be captured. This status is used for credit card payments and some post-payment methods."),
    PARTLY_CAPTURED("97", "PARTLY CAPTURED", "The payment has been partly captured. You can void or capture the autorised part."),
    PAID("100", "PAID", "The payment was successful."),
    PAID_CHECKAMOUNT("-51", "PAID CHECKAMOUNT","Payment cancelled because the amount paid by the customer does not match the original order amount."),
    FAILURE("-60", "FAILURE", "The payment was cancelled due to an unexptected failure at the payment processor."),
    DENIED("-63", "DENIED", "The payment was rejected by the payment processor. In case of post-payment methods this means that the customer did not pass the credit check."),
    DENIED_V2("-64", "DENIED", "The payment was cancelled by an employee or external system."),
    CHARGEBACK("-71", "CHARGEBACK", "Chargeback of a credit card payment."),
    PARTIAL_REFUND("-82", "PARTIAL REFUND","The payment has been partially refunded."),
    PARTIAL_PAYMENT("80", "PARTIAL PAYMENT", "Partial payments are used with gift cards that do not cover the full amount of the order.");

    @Getter
    private String code;
    @Getter
    private String action;
    @Getter
    private String description;

    public static TransactionStatus getTransactionStatusByCode(String incomingStatusCode) {
        for (TransactionStatus st : TransactionStatus.values()) {
            if (st.getCode().equals(incomingStatusCode)) return st;
        }
        return null;
    }
    
}
