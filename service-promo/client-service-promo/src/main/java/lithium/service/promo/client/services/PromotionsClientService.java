package lithium.service.promo.client.services;

import java.util.List;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.promo.client.PromotionsClient;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionsClientService {

  @Setter
  @Autowired
  LithiumServiceClientFactory lithiumServiceClientFactory;

  public List<Long> promotionsLinkedToReward(Long rewardId)
  throws Exception
  {
    PromotionsClient promotionsClient = lithiumServiceClientFactory.target(PromotionsClient.class);
    return promotionsClient.promotionsLinkedToReward(rewardId);
  }
}