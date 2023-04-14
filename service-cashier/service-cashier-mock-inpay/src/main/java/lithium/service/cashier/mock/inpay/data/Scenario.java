package lithium.service.cashier.mock.inpay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Scenario {
    COMPLETED("Completed transaction", 0L),
    REJECTED("Rejected transaction", 99L),
    RETURNED("Returned transaction", 98L),
    PENDING("Pending transaction", 97L),
    NO_NOTIFICATION("No notification", 96L),
    COMPLETED_NO_DELAY("No delay notification", 95L),
    INCORRECT_FINAL_AMOUNT("Incorrect final amount",94L);

    @Getter
    private String description;

    @Getter
    private Long amount;

    public static Scenario getByAmount(Long amount) {
        for (Scenario s : Scenario.values()) {
            if (s.amount.equals(amount / 100)) {
                return s;
            }
        }
        return COMPLETED;
    }

}
