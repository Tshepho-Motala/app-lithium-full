package lithium.service.limit.client.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum VerificationStatus {
    UNVERIFIED(1, 0),
    MANUALLY_VERIFIED(2, 1),
    EXTERNALLY_VERIFIED(3, 1),
    SOF_VERIFIED(4, 2),
    UNDERAGED(5, -1),
    AGE_ONLY_VERIFIED(6, 1);

    @Getter
    private long id;
    @Getter
    private int level;

    public static boolean isVerified(Long verificationStatusId) {
        return verificationStatusId == MANUALLY_VERIFIED.id || verificationStatusId == EXTERNALLY_VERIFIED.id
                || verificationStatusId == SOF_VERIFIED.id;
    }

    public static boolean isAgeOnlyVerified(Long verificationStatusId) {
        return verificationStatusId == AGE_ONLY_VERIFIED.id;
    }
}
