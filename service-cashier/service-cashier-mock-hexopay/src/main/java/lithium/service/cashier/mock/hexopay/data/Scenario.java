package lithium.service.cashier.mock.hexopay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Scenario {
    SUCCESS("Success flow", 0L, ""),
    FAILED("Failed flow", 9999L,""),
    NO_NOTIFICATION("Notification will not be sent", 9998L,""),
    NO_NOTIFICATION_DELAY("Notification will be sent with no delay",9997L,""),
    NO_NOTIFICATION_SIGNATURE("Notification will be sent without signature", 9996L,""),
    FAILED_NOTIFICATION_SIGNATURE("Notification will be sent with failed signature", 9995L,""),
    INCORRECT_AMOUNT("Different initial and final transaction amount", 9994L,""),
    ERROR_RESPONSE("Error response on initial request", 9993L,""),
    AVS_POSTAL_CODE_UNMATCH("AVS B: Street address matches, but postal code is not verified", -1L,"B"),
    AVS_ADDRESS_AND_POSTAL_CODE_UNMATCH( "AVS N: Street address and postal code do not match",-1L,"N"),
    AVS_ADDRESS_AND_POSTAL_CODE_MATCH( "AVS M: Street address and postal code match",-1L,"M"),
    AVS_ADDRESS_UNMATCH("AVS P: Postal code matches, but street address is not verified", -1L,"P"),
    AVS_ADDRESS_UNAVAILABLE("AVS U: Address information unavailable", -1L,"U"),
    AVS_NOT_VERIFIED_SYSTEM_ERROR("AVS E: A system error prevented any verification of street address or postal code", -1L,"E"),
    AVS_RESULT_UNKNOWN("AVS 0: AVS result is unknown", -1L,"0"),
    CVC_MATCH("CVC M: Card verification code matched",-1L, "M"),
    CVC_UNMATCH("CVC N: Card verification code not matched",-1L, "N"),
    CVC_NOT_SUPPORTED_BY_BANK("CVC U: Card verification is not supported by the issuing bank", -1L,"U"),
    CVC_NOT_VERIFIED_SYSTEM_ERROR("CVC E: A system error prevented any CVC verification", -1L,"E"),
    CVC_RESULT_UNKNOWN("CVC 0: CVC result is unknown", -1L,"0"),
    BE_PROTECTED_EMAIL("Email is black listed", 9992L, ""),
    BE_PROTECTED_IP("IP is black listed", 9991L, ""),
    BE_PROTECTED_CARD("Card is black listed", 9990L, ""),
    BE_PROTECTED_DUPLICATE_ACCOUNT("Duplicate card check failed", 9989L, "");

    @Getter
    private String description;

    @Getter
    private Long amount;

    @Getter
    private String data;

    public static Scenario getByAmount(Long amount) {
        for (Scenario s : Scenario.values()) {
            if (s.amount.equals(amount)) {
                return s;
            }
        }
        return SUCCESS;
    }

    public static Scenario getBySelector(String selector) {
        for (Scenario s : Scenario.values()) {
            if (s.amount.equals(selector)) {
                return s;
            }
        }
        return SUCCESS;
    }
}
