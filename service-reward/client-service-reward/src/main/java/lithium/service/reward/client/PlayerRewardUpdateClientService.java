package lithium.service.reward.client;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlayerRewardUpdateClientService {

  @Autowired
  @Setter
  LithiumServiceClientFactory services;

  private PlayerRewardUpdateClient rewardTransactionClient()
  throws Exception
  { //TODO: Fix exceptions
    try {
      return services.target(PlayerRewardUpdateClient.class, "service-reward", true);
    } catch (Exception e) {
      log.error("Problem getting reward service", e);
      throw new Exception("Unable to retrieve reward client proxy: " + e.getMessage());
    }
  }

  public PlayerRewardTypeHistory updatePlayerRewardCounter(Long playerRewardTypeHistoryId)
  throws Exception
  { //TODO: add specific error code exceptions
    try {
      return rewardTransactionClient().updatePlayerRewardCounter(playerRewardTypeHistoryId);
    } catch (Exception e) {
      throw e;
    }
  }
}
