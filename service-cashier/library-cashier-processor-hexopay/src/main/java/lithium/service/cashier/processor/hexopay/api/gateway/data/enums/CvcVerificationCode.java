package lithium.service.cashier.processor.hexopay.api.gateway.data.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
//@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum CvcVerificationCode {
    CVC_MATCH("M", "Card verification code matched"),
    CVC_UNMATCH("N", "Card verification code not matched"),
    CVC_NOT_SUPPORTED_BY_BANK("U", "Card verification is not supported by the issuing bank"),
    NO_CVC_CHECK("1", "Card verification is not supported for this processor or card type"),
    NOT_VERIFIED_SYSTEM_ERROR("E", "A system error prevented any CVC verification"),
    CVC_RESULT_UNKNOWN("0", "CVC result is unknown");

    @Getter
    @Accessors(fluent=true)
    private String code;

    @Getter
    private String description;

    CvcVerificationCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CvcVerificationCode fromCode(String code) {
        for (CvcVerificationCode t: CvcVerificationCode.values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        return NO_CVC_CHECK;
    }
}
