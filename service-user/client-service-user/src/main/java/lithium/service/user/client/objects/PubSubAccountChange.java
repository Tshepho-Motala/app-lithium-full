package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubAccountChange implements PubSubObj {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String domain;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PubSubEventType eventType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long accountId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String DOB;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String addressLine1;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String city;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String country;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countryCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String postalCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String registrationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long lossLimitDaily;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long lossLimitWeekly;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long lossLimitMonthly;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long dailyDepositLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long weeklyDepositLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long monthlyDepositLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long balanceLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean depositBlock;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String verificationStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String biometricsStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String closureReason;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cellphoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String affiliateGuid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String affiliateSecondaryGuid1;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String affiliateSecondaryGuid2;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String affiliateSecondaryGuid3;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean selfExcluded;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean selfExclusionPermanent;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String selfExclusionCreated;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String selfExclusionExpiry;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean coolingOff;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String coolingOffCreated;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String coolingOffExpiry;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long playTimeLimitGranularity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long playTimeLimitTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String author;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String origin;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isEmailValidated;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean contraAccountSet;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isTestAccount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String externalUserId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kycVerificationName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long linkedUserId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCasinoBonusAllowed;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCasinoBlocked;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isSportsBookBlocked;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLoginBlocked;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCompedBlocked;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isWithdrawalBlocked;

    private Device signupDevice;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String referrerGuid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String referrer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String referralCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean converted;
}
