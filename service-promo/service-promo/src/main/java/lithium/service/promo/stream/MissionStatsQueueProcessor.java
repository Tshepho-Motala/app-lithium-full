package lithium.service.promo.stream;

import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.context.PromoContext;
import lithium.service.promo.services.PromotionStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding( MissionStatsQueueSink.class )
public class MissionStatsQueueProcessor {

  @Autowired
  private PromotionStatsService promotionStatsService;

  @StreamListener( MissionStatsQueueSink.INPUT )
  void handle(PromoActivityBasic entry) throws Exception {
    log.debug("Received a PromoActivityBasic from the queue for processing: " + entry);

    promotionStatsService.register(PromoContext.builder().promoActivityBasic(entry).build());
  }
}
