package lithium.service.cashier.mock.smartcash.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Scenario {
    SUCCESS("Success flow", "", "DP01100001001"),
    FAILED("Transaction Failed", "9999",""),
    AMBIGUOUS("Transaction Ambiguous", "9998",""),
    NO_NOTIFICATION("Notification will not be sent", "9997",""),
    NO_NOTIFICATION_DELAY("Notification will be sent with no delay","9996",""),
    NO_NOTIFICATION_SIGNATURE("Notification will be sent without signature", "9995",""),
    FAILED_NOTIFICATION_SIGNATURE("Notification will be sent with failed signature", "9994",""),
    ERROR_RESPONSE("Error response on initial request", "9993",""),
    INVALID_AMOUNT("User enters invalid amount.", "9992","DP01100001004"),
    INCORRECT_PIN("User enters incorrect pin.", "9991","DP01100001002"),
    LIMIT_EXCEEDED("User Exceeds withdrawal transaction limit.", "9990","DP01100001003"),
    INSUFFICIENT_FUNDS("User has insufficient funds to complete the transaction.", "9989", "DP01100001007"),
    INVALID_MOBILE_NUMBER("Invalid mobile number", "9988","DP01100001012"),
    TRANSACTION_ALREADY_EXIST("This transaction already exists", "9987", "DP01100001016"),
    NO_PLAYER_CONFIRMATION("User does not confirm payment", "9986", "");


    @Getter
    private String description;

    @Getter
    private String amount;

    @Getter
    private String code;

    public static Scenario getByAmount(String amount) {
        for (Scenario s : Scenario.values()) {
            if (s.amount.equals(amount)) {
                return s;
            }
        }
        return SUCCESS;
    }
}
