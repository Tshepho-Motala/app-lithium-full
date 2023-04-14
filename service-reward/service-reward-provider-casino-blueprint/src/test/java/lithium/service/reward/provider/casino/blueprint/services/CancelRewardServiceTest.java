package lithium.service.reward.provider.casino.blueprint.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfig;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfigService;
import lithium.service.reward.provider.casino.blueprint.utils.TestConstants;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static lithium.service.reward.provider.casino.blueprint.utils.FixtureUtil.fixture;

@ExtendWith(MockitoExtension.class)
public class CancelRewardServiceTest {

    @Mock
    private BlueprintRewardService blueprintRewardService;

    @Mock
    private ProviderConfigService providerConfigService;

    @Mock
    private ModuleInfo moduleInfo;

    private ObjectMapper mapper;

    private CancelRewardService cancelRewardService;

    @Test
    public void must_cancel_reward_given_a_valid_request_and_response() throws Exception {

        CancelRewardRequest request = mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_CANCEL_REQUEST), CancelRewardRequest.class);

        Mockito.when(blueprintRewardService.cancelFreeSpins(Mockito.any(CancelRewardRequest.class), Mockito.any(ProviderConfig.class)))
                .thenReturn(CancelRewardResponse.builder().code("0").build());

        Mockito.when(providerConfigService.getConfig(Mockito.anyString(), Mockito.anyString())).thenReturn(ProviderConfig.builder().build());

        CancelRewardResponse response = cancelRewardService.cancelReward(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0", response.getCode());
    }

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        cancelRewardService = new CancelRewardService(blueprintRewardService, providerConfigService, moduleInfo);

        Mockito.when(moduleInfo.getModuleName()).thenReturn("service-reward-provider-casino-blueprint");
    }

}
