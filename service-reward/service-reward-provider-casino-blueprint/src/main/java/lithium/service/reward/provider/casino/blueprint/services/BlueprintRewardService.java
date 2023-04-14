package lithium.service.reward.provider.casino.blueprint.services;

import lithium.service.reward.provider.casino.blueprint.config.ProviderConfig;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;

public interface BlueprintRewardService {
     ProcessRewardResponse awardFreeSpins(ProcessRewardRequest awardRequest, ProviderConfig providerConfig);
     CancelRewardResponse cancelFreeSpins(CancelRewardRequest cancelRequest, ProviderConfig providerConfig);

}
