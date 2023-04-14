package lithium.service.reward.stream;

import lithium.service.reward.client.dto.GiveRewardContext;
import lithium.service.reward.client.dto.GiveRewardRequest;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.service.GiveRewardService;
import lithium.service.reward.service.RewardTypeService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding( {GiveRewardQueueSinkV1.class} )
public class GiveRewardQueueProcessor {

  @Autowired
  private GiveRewardService giveRewardService;
  @Autowired
  ModelMapper modelMapper;

  @StreamListener( GiveRewardQueueSinkV1.INPUT )
  void handleGiveRewardV1(GiveRewardRequest request) throws Exception {
    log.debug("Received a GiveRewardRequest from the v1 queue for processing: " + request);
    String locale = LocaleContextHolder.getLocale().getLanguage();

    GiveRewardContext context = GiveRewardContext.builder().modelMapper(modelMapper)
            .locale(locale)
            .giveRewardRequest(request).build();
    try {
      context.addLog("Processing give reward");
      context.setGiveRewardResponse(giveRewardService.giveReward(context));
    } catch (Exception e) {
      log.error(context.compileLog("Unknown error... "+e.getMessage()), e);
    } finally {
      log.debug(context.compileLog("Processed give reward."));
//      return context.getGiveRewardResponse();
    }
  }
}
