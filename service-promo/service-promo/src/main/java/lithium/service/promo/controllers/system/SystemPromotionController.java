package lithium.service.promo.controllers.system;

import java.util.List;
import lithium.service.promo.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/system" )
@RequiredArgsConstructor
public class SystemPromotionController {

  private final PromotionService promotionService;

  @GetMapping( "/promotions-linked-to-reward" )
  public List<Long> promotionsLinkedToReward(@RequestParam( "rewardId" ) Long rewardId) {
    return promotionService.promotionsLinkedToReward(rewardId);
  }
}
