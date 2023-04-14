package lithium.service.reward.service;

import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.ProviderClientService;

import lithium.service.reward.dto.RewardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RewardProviderService {
    private final ProviderClientService providerClientService;
    private final ModelMapper modelMapper;

    public List<RewardProvider> getRewardProviderForDomain(String domainName) {
        List<lithium.service.domain.client.objects.Provider> providerList = providerClientService.providers(domainName, ProviderConfig.ProviderType.REWARD);
        List<RewardProvider> rewardProviders = modelMapper.map(providerList, new TypeToken<List<RewardProvider>>(){}.getType());
        addDefaultProviders(rewardProviders);

        return rewardProviders.stream()
                .sorted(Comparator.comparing(RewardProvider::getName))
                .collect(Collectors.toList());
    }

    private void addDefaultProviders(List<RewardProvider> providers) {
        providers.add(RewardProvider.builder()
                        .name("Reward")
                        .url("service-reward")
                .build());
    }
}
