package lithium.service.promo.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( name = "service-promo" )
public interface PromotionsClient {

  @RequestMapping( path = "/system/promotions-linked-to-reward", method = RequestMethod.GET )
  List<Long> promotionsLinkedToReward(@RequestParam( "rewardId" ) Long rewardId);
}