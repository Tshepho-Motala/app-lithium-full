package lithium.service.reward.controller.backoffice;

import lithium.service.reward.dto.RewardProvider;
import lithium.service.reward.service.RewardProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice/{domainName}/providers")
public class BackofficeRewardProvider {

    private final RewardProviderService rewardProviderService;

    @GetMapping
    public List<RewardProvider> getProviders(@PathVariable String domainName) {
        return rewardProviderService.getRewardProviderForDomain(domainName);
    }
}
