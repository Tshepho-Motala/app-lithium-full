package lithium.service.reward.provider.casino.blueprint.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.reward.client.exception.Status467RewardComponentNotSupported;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfig;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfigService;
import lithium.service.reward.provider.casino.blueprint.utils.TestConstants;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static lithium.service.reward.provider.casino.blueprint.utils.FixtureUtil.fixture;

@ExtendWith(MockitoExtension.class)
public class ProcessRewardServiceTest {

    private ProcessRewardService processRewardService;

    @Mock
    private ModuleInfo moduleInfo;

    @Mock
    private ProviderConfigService providerConfigService;

    @Mock
    private BlueprintRewardService blueprintRewardService;

    private ObjectMapper mapper;

    @Test
    public void reward_must_be_granted_given_a_valid_request_and_response() throws Exception {
        ProcessRewardRequest request = mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST), ProcessRewardRequest.class);

        Mockito.when(blueprintRewardService.awardFreeSpins(Mockito.any(ProcessRewardRequest.class), Mockito.any(ProviderConfig.class)))
                .thenReturn(ProcessRewardResponse.builder().status(ProcessRewardStatus.SUCCESS).build());

        Mockito.when(providerConfigService.getConfig(Mockito.anyString(), Mockito.anyString())).thenReturn(ProviderConfig.builder().build());

        ProcessRewardResponse response = processRewardService.processReward(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(ProcessRewardStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void should_fail_given_an_invalid_reward_component() throws Exception {
        ProcessRewardRequest request = mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST_UNSUPPORTED), ProcessRewardRequest.class);
        Assertions.assertThrows(Status467RewardComponentNotSupported.class, () -> processRewardService.processReward(request));
    }

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        processRewardService = new ProcessRewardService(blueprintRewardService, providerConfigService, moduleInfo);

        Mockito.when(moduleInfo.getModuleName()).thenReturn("service-reward-provider-casino-blueprint");
    }
}
