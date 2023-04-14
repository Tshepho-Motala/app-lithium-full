package lithium.service.casino.provider.roxor.api.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    private String website;
    private List<String> gameKey;
    //private List<RewardGameConfig> rewardGameConfigs = null;
    private String playerId;
    private String rewardId;
    private String rewardType;
    private Integer numberOfUnits;
    private Money unitValue;
    private Duration duration;
    private Object metadata;
    private Source source;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Campaign campaign;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private InstantReward instantReward;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BonusCash bonusCash;


    @Data
    @Builder
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Campaign {
        private String campaignId;
    }

    @Data
    @Builder
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstantReward {
        private String currency;
        private Double unitValue;
        private int remainingUnits;
        private String volatility = "FIXED";
    }

    @Data
    @Builder
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BonusCash {
        private String currency;
        private Double initial;
        private Double redeemed;
        private Double balance;
    }
}
