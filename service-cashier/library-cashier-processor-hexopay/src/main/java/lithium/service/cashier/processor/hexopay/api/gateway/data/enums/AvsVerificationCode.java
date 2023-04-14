package lithium.service.cashier.processor.hexopay.api.gateway.data.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum AvsVerificationCode {
    POSTAL_CODE_UNMATCH("B", "Street address matches, but postal code is not verified"),
    ADDRESS_AND_POSTAL_CODE_UNMATCH("N", "Street address and postal code do not match"),
    ADDRESS_AND_POSTAL_CODE_MATCH("M", "Street address and postal code match"),
    ADDRESS_UNMATCH("P", "Postal code matches, but street address is not verified"),
    ADDRESS_UNAVAILABLE("U", "Address information unavailable"),
    AVS_NOT_SUPPORTED("1", "AVS is not supported for this processor or card type"),
    NOT_VERIFIED_SYSTEM_ERROR("E", "A system error prevented any verification of street address or postal code"),
    AVS_RESULT_UNKNOWN("0", "AVS result is unknown");

    @Getter
    @Accessors(fluent=true)
    private String code;

    @Getter
    private String description;

    public static AvsVerificationCode fromCode(String code) {
        for (AvsVerificationCode t: AvsVerificationCode.values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        return AVS_NOT_SUPPORTED;
    }
}
