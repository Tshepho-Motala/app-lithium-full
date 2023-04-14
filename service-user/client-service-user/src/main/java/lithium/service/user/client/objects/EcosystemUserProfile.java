package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EcosystemUserProfile {
    private EcosystemUserProfileDomain domain;
    private String ecosystemRelationshipType;
    private boolean emailOptOut;
    private boolean postOptOut;
    private boolean smsOptOut;
    private boolean callOptOut;
    private boolean pushOptOut;
    private boolean leaderboardOptOut;
    private Status status;
    private StatusReason statusReason;
    private int verificationLevel;
    private boolean ageVerified;
    private boolean addressVerified;
    private boolean emailValidated;
    private boolean cellphoneValidated;
    private boolean contraAccountSet;
    private boolean commsOptInComplete;
    private boolean hasSelfExcluded;
    private boolean autoWithdrawalAllowed;
    private TermsAndConditionsVersion termsAndConditionsVersion;
    //Making use of the existing lithium.service.limit.client.objects.Access object here causes circular-reference-dependency issues
    //So this is a copy of Access object
    private Restrictions restrictions;
}
