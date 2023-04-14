package lithium.service.reward.controller.backoffice;

import lithium.service.reward.data.entities.RewardType;
import lithium.service.reward.service.RewardTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice/reward-types")
@RequiredArgsConstructor
public class BackofficeRewardTypeController {
    private final RewardTypeService rewardTypeService;
    private final ModelMapper modelMapper;

    @PostMapping("/providers")
    public List<lithium.service.reward.client.dto.RewardType> getRewardTypesForProvider(@RequestBody List<String> providerGuids) {

        List<RewardType> types = rewardTypeService.getTypesForProviders(providerGuids);
        return modelMapper.map(types, new TypeToken<List<lithium.service.reward.client.dto.RewardType>>(){}.getType());
    }
}
