package lithium.service.reward.provider.casino.blueprint.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Accessors(fluent = true)
public enum BlueprintErrorCode {
    BPPM100("BPPM100", "Invalid XML"),
    BPPM101("BPPM101", "Invalid Token"),
    BPPM102("BPPM102", "Invalid Action"),
    BPPM103("BPPM103", "Invalid Start/End Date (Start Date may be greater than End Date)"),
    BPPM104("BPPM104", "Invalid Stake (Min Stake is greater than Max Stake)"),
    BPPM200("BPPM200", "Failed to get promotions"),
    BPPM201("BPPM201", "Failed to get promotion balance"),
    BPPM202("BPPM202", "Failed to get default credit"),
    BPPM301("BPPM301", "Failed to add/remove user"),
    BPPM302("BPPM302", "Failed to enable/disable promotion"),
    BPPM303("BPPM303", "Failed to add promotion");



    private String errorCode;

    @Getter
    private String message;

    @JsonCreator
    public static BlueprintErrorCode fromErrorCode(String errorCode) {
        return Arrays.stream(values()).filter(v -> v.errorCode.equalsIgnoreCase(errorCode))
                .findFirst()
                .orElse(null);
    }
}
