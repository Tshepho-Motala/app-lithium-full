package lithium.service.cashier.mock.flutterwave.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Scenario {

    BAD_REQUEST("Bad request", "cancelled", 10000l),
    INSUFFICIENT_FUND("Insufficient Fund", "failed", 10100l),
    NOT_COMPLETED("Transaction not completed by user", "failed", 10200l),
    TRANSACTION_FAILED("Transaction Failed", "failed", 10300l),
    SUCCESFUL("Transaction Successful", "successful", 0l);

    @Getter
    private String description;

    @Getter
    private String state;

    @Getter
    private Long amount;

    public static Scenario getScenarioByAmount(Long tranAmount) {
        for (Scenario sc : Scenario.values()) {
            if (sc.getAmount().equals(tranAmount)) {
                return sc;
            }
        }
        return Scenario.SUCCESFUL;
    }

}
