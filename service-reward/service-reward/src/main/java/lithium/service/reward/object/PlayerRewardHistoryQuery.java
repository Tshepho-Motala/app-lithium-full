package lithium.service.reward.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerRewardHistoryQuery {
    private String playerGuid;
    private String rewardCode;
    private Long rewardId;
    private Date awardedDateTo;
    private Date awardedDateFrom;
    private Date redeemedDateTo;
    private Date redeemedDateFrom;
    private Date expiryDateTo;
    private Date expiryDateFrom;
    private String domainName;
    private String[] historyStatuses;
}
