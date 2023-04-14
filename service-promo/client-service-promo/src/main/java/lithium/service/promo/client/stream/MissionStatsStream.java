package lithium.service.promo.client.stream;

import lithium.service.promo.client.objects.PromoActivityBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MissionStatsStream {

  @Autowired
  private MissionStatsOutputQueue missionStatsOutputQueue;

  //  public void register(MissionStatBasic entry) {
  //    missionStatsOutputQueue.outputQueue().send(MessageBuilder.withPayload(entry).build());
  //  }

  public void registerActivity(PromoActivityBasic entry) {
    missionStatsOutputQueue.outputQueue().send(MessageBuilder.withPayload(entry).build());
  }
}