package lithium.service.reward.service;

import lithium.service.reward.enums.RewardTypeName;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessRewardService {

  private final UnlockGamesService unlockGamesService;
  private final CashAwardService cashAwardService;

  public ProcessRewardResponse process(ProcessRewardRequest request)
  throws Exception //TODO: Implement appropriate exceptions.
  {
    RewardTypeName rewardType = RewardTypeName.fromType(request.getRewardType().getName());
    ProcessRewardResponse processRewardResponse = switch (rewardType) {
      case UNLOCK_GAMES -> unlockGamesService.unlockGames(request.getPlayer(), request.getRewardRevisionTypeGames());
      case CASH -> cashAwardService.awardCash(request);
    };

    processRewardResponse.setCode(request.getReward().getCurrent().getCode());
    return processRewardResponse;
  }
}
