package lithium.service.cashier.services.autowithdrawal;

import lombok.Getter;

public class ValidatedResult {
    @Getter
    private boolean validated;
    @Getter
    private String checkingValue;

    private ValidatedResult() {

    }

    public static ValidatedResult failed() {
        ValidatedResult result =  new ValidatedResult();
        result.validated = false;
        return result;
    }

    public static ValidatedResult of(boolean validated, String value) {
        ValidatedResult result =  new ValidatedResult();
        result.validated = validated;
        result.checkingValue = value;
        return result;
    }
}
