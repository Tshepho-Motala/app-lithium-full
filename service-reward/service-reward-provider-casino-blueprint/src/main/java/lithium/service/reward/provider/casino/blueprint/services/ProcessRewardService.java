package lithium.service.reward.provider.casino.blueprint.services;

import lithium.modules.ModuleInfo;
import lithium.service.reward.client.exception.Status467RewardComponentNotSupported;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfig;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfigService;
import lithium.service.reward.provider.casino.blueprint.enums.RewardTypeName;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessRewardService {

  private final BlueprintRewardService blueprintRewardService;
  private final ProviderConfigService providerConfigService;

  private final ModuleInfo moduleInfo;

  public ProcessRewardResponse processReward(ProcessRewardRequest request) throws Exception {

    ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), request.domainName());
    RewardTypeName rewardTypeName = RewardTypeName.fromName(request.getRewardType().getName());
    ProcessRewardResponse response;

    if (rewardTypeName == RewardTypeName.FREESPIN) {
     response = blueprintRewardService.awardFreeSpins(request, providerConfig);
    } else {
      throw new Status467RewardComponentNotSupported(MessageFormat.format("Reward Component {0} is not supported", request.getRewardType().getName()));
    }

    return response;
  }

}
