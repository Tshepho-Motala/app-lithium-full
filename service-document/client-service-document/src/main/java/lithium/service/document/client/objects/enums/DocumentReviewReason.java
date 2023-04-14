package lithium.service.document.client.objects.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DocumentReviewReason {
    CROPPED("Cropped"),
    DOCTORED("Doctored"),
    FALSIFIED_DOCUMENT("Falsified Document"),
    PERSONAL_DETAILS_MISMATCH("Personal Details Mismatch"),
    UNREADABLE_BLURRED("Unreadable - blurred"),
    UNREADABLE_LOW_RESOLUTION("Unreadable - low resolution"),
    UNREADABLE_OBSCURED_DATA("Unreadable - obscured data"),
    OUT_OF_DATE("Out of date"),
    INCORRECT_DOCUMENT("Incorrect Document"),
    AWAITING_BACK("Awaiting Back"),
    ID_VERIFIED_NEEDS_ADDRESS("ID verified - Needs Address"),
    ID_AND_ADDRESS_VERIFIED("ID & Address Verified"),
    OTHER("Other");

    @Getter
    private String name;

    public static DocumentReviewReason fromName(String name) {
        for (DocumentReviewReason reason : DocumentReviewReason.values()) {
            if (reason.name.equalsIgnoreCase(name)) {
                return reason;
            }
        }
        return null;
    }
}
