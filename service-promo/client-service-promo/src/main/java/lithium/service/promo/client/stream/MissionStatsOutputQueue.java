package lithium.service.promo.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MissionStatsOutputQueue {

  @Output( "missionstatsoutput" )
  MessageChannel outputQueue();
}