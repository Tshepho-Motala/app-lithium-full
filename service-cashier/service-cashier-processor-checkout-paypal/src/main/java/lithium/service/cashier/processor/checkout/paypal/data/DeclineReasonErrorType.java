package lithium.service.cashier.processor.checkout.paypal.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeclineReasonErrorType {
    CHECKOUT_PAYPAL_DEPOSIT_STAGE_1_DECLINED(501, "ServiceCashierCheckoutPayPalDoProcessor.depositStage1: transaction declined"),
    CHECKOUT_PAYPAL_INITIATING_TRANSACTION_PAYMENT_STAGE_1_DECLINED(502, "ServiceCashierCheckoutPayPalDoProcessor.depositStage1: "),
    CHECKOUT_PAYPAL_DEPOSIT_STAGE_2_DECLINED(503, "ServiceCashierCheckoutPayPalDoProcessor.depositStage2: transaction declined"),
    CHECKOUT_PAYPAL_INITIATING_TRANSACTION_PAYMENT_STAGE_2_DECLINED(504, "ServiceCashierCheckoutPayPalDoProcessor.withdrawStage2: "),
    UNKNOWN_ERROR(404, "Validation error");

    @Getter
    @Accessors(fluent = true)
    private Integer code;
    @Getter
    @Accessors(fluent = true)
    private String description;
    private static final String PREFIX = "service-cashier-processor-checkout-paypal -> ";

    public static String getError(DeclineReasonErrorType errorType) {
        try {
            return DeclineReasonErrorType.PREFIX + errorType.code + " | " + errorType.description;
        } catch (Exception ex) {
            return DeclineReasonErrorType.PREFIX + DeclineReasonErrorType.UNKNOWN_ERROR.code + ": " + DeclineReasonErrorType.UNKNOWN_ERROR.description;
        }
    }

    public static String getInitiatingTransactionPaymentStage1DecliningMessage(long transactionId, String exMessage) {
        String message = "Unable to get payment details for the paymentToken for tran with id " + transactionId + ". " + "CheckoutException:" + exMessage;
        return getError(CHECKOUT_PAYPAL_INITIATING_TRANSACTION_PAYMENT_STAGE_1_DECLINED) + message;
    }

    public static String getInitiatingTransactionPaymentStage2DecliningMessage(long transactionId, String exMessage) {
        String message = "Unable to get payment details for the paymentToken for tran with id " + transactionId + ". " + "CheckoutException:" + exMessage;
        return getError(CHECKOUT_PAYPAL_INITIATING_TRANSACTION_PAYMENT_STAGE_2_DECLINED) + message;
    }
}
