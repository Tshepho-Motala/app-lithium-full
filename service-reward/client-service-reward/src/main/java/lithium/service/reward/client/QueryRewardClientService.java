package lithium.service.reward.client;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.exception.Status511UpstreamServiceUnavailableException;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueryRewardClientService {

  @Autowired
  @Setter
  LithiumServiceClientFactory services;

  private QueryRewardClient queryRewardClient()
  throws Exception
  { //TODO: Fix exceptions
    try {
      return services.target(QueryRewardClient.class, "service-reward", true);
    } catch (Exception e) {
      log.error("Problem getting reward service", e);
      throw new Exception("Unable to retrieve reward client proxy: " + e.getMessage());
    }
  }

  public PlayerRewardTypeHistory findByRewardTypeReference(String rewardTypeReference)
  throws Status511UpstreamServiceUnavailableException
  {
    try {
      PlayerRewardTypeHistory findByRewardTypeReference = queryRewardClient().findByRewardTypeReference(rewardTypeReference);
      log.debug("PlayerRewardTypeHistory:: "+findByRewardTypeReference);
      return findByRewardTypeReference;
    } catch (Exception e) {
      log.error("Failed to retrieve reward component by reference::"+ rewardTypeReference, e);
      throw new Status511UpstreamServiceUnavailableException("Reward component retrieval failed:"+ e);
    }
  }

  public PlayerRewardTypeHistory findById(Long playerRewardTypeHistoryId)
  throws Status511UpstreamServiceUnavailableException
  {
    try {
      PlayerRewardTypeHistory findByRewardTypeReference = queryRewardClient().findById(playerRewardTypeHistoryId);
      log.debug("PlayerRewardTypeHistory:: "+findByRewardTypeReference);
      return findByRewardTypeReference;
    } catch (Exception e) {
      log.error("Failed to retrieve reward component by id::"+ playerRewardTypeHistoryId, e);
      throw new Status511UpstreamServiceUnavailableException("Reward component retrieval failed:"+ e);
    }
  }
}
