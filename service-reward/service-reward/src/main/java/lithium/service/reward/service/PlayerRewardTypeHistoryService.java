package lithium.service.reward.service;

import lithium.service.reward.client.dto.RewardRevisionTypeValueOverride;
import lithium.service.reward.data.entities.PlayerRewardTypeHistoryValue;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import lithium.service.reward.data.entities.RewardRevisionTypeValue;
import lithium.service.reward.data.repositories.PlayerRewardTypeHistoryValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PlayerRewardTypeHistoryService {
    private final PlayerRewardTypeHistoryValueRepository playerRewardHistoryTypeValueRepository;
    private final RewardService rewardService;

    public void saveHistoryValuesFromOverrides(PlayerRewardTypeHistory playerRewardTypeHistory, List<RewardRevisionTypeValueOverride>  overrides) {
        List<RewardRevisionTypeValue> rewardRevisionTypeValues = rewardService.findByRewardRevisionType(playerRewardTypeHistory.getRewardRevisionType().getId());

        for (RewardRevisionTypeValueOverride override: overrides) {
            for (RewardRevisionTypeValue rewardRevisionTypeValue: rewardRevisionTypeValues) {
                if ((override.getRewardRevisionTypeId().equals(rewardRevisionTypeValue.getRewardRevisionType().getId()) && (override.getRewardTypeFieldId().equals(rewardRevisionTypeValue.getRewardTypeField().getId())))) {
                    playerRewardHistoryTypeValueRepository.save(PlayerRewardTypeHistoryValue.builder()
                                    .playerRewardTypeHistory(playerRewardTypeHistory)
                                    .rewardTypeField(rewardRevisionTypeValue.getRewardTypeField())
                                    .value(override.getValue())
                            .build());

                    log.debug("Saved override value for field {} with value {}", rewardRevisionTypeValue.getRewardTypeField().getName(), override.getValue());
                }
            }
        }
    }

    public List<PlayerRewardTypeHistoryValue> getPlayerRewardHistoryValues(PlayerRewardTypeHistory playerRewardTypeHistory) {
        return playerRewardHistoryTypeValueRepository.findAllByPlayerRewardTypeHistory(playerRewardTypeHistory);
    }
}
