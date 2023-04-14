package lithium.service.cashier.mock.paynl.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Scenario {
    SUCCESS("Success flow", 0L),
    FAILURE("Failure", 9999L),
    CANCELED("Canceled", 9998L),
    DENIED("Denied", 9997L),
    DENIED_V2("Denied v2", 9996L),
    EXPIRED("Expired", 9995L),
    NO_NOTIFICATION("No notification", 9994L),
    COMPLETED_NO_DELAY("No delay notification", 9993L),
    INCORRECT_FINAL_AMOUNT("Incorrect final amount", 9992L),
    GENERAL_ERROR("General error", 9991L),
    MULTIPLE_ERRORS("Multiple Errors", 9990L),
    EMPTY_ERROR("Empty error", 9989L),
    EMPTY_BODY("Empty body", 9988L);
    
    @Getter
    private String description;
    
    @Getter
    private Long amount;

    public static Scenario getByAmount(Long amount) {
        for (Scenario s : Scenario.values()) {
            if (s.amount.equals(amount)) {
                return s;
            }
        }
        return SUCCESS;
    }
}
