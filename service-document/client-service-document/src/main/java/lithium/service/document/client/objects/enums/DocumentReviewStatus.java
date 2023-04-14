package lithium.service.document.client.objects.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DocumentReviewStatus {
    HISTORIC("Historic"),
    WAITING("Waiting"),
    VALID("Valid"),
    INVALID("Invalid");

    @Getter
    private String name;

    public static DocumentReviewStatus fromName(String name) {
        for (DocumentReviewStatus status : DocumentReviewStatus.values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
