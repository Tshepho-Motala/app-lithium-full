package lithium.service.reward.provider.casino.blueprint.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfig;
import lithium.service.reward.provider.casino.blueprint.dto.BlueprintRewardRequest;
import lithium.service.reward.provider.casino.blueprint.enums.RewardTypeFieldName;
import lithium.service.reward.provider.casino.blueprint.services.impl.BlueprintRewardServiceImpl;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static lithium.service.reward.provider.casino.blueprint.utils.FixtureUtil.fixture;

@ExtendWith(MockitoExtension.class)
public class BlueprintRewardServiceTest {

    private XmlMapper xmlMapper = new XmlMapper();
    private ObjectMapper mapper;

    private BlueprintRewardServiceImpl blueprintRewardService;

    ProviderConfig providerConfig;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        blueprintRewardService = new BlueprintRewardServiceImpl(restTemplate);

        providerConfig = ProviderConfig.builder()
                .brandId("LVS-P009-142")
                .countryCode("ENG")
                .iforiumPlatformKey("P009")
                .countryCode("GBP")
                .jurisdiction("UK")
                .playerOffset("1000")
                .rewardApiToken("devtoken")
                .rewardsBaseUrl("")
                .playerGuidPrefix("LVS")
                .build();

        mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void must_be_an_instance_of_BlueprintRewardService() {
        Assertions.assertInstanceOf(BlueprintRewardService.class, blueprintRewardService);
    }

    @Test
    public void should_return_a_successful_response_given_a_successful_blueprint_grant_response() throws JsonProcessingException {
        String blueprintResponse = fixture(TestConstants.FIXTURE_BLUEPRINT_SUCCESSFUL_GRANT_RESPONSE);
        String processRewardRequestJson = fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST);
        ProcessRewardRequest processRewardRequest= mapper.readValue(processRewardRequestJson, ProcessRewardRequest.class);

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.ok().body(blueprintResponse));

        ProcessRewardResponse response = blueprintRewardService.awardFreeSpins(processRewardRequest, providerConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getStatus(), ProcessRewardStatus.SUCCESS);
    }


    @Test
    public void should_return_a_failed_response_given_a_failed_blueprint_grant_response() throws JsonProcessingException {
        String blueprintResponse = fixture(TestConstants.FIXTURE_BLUEPRINT_FAILED_GRANT_RESPONSE);
        String processRewardRequestJson = fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST);
        ProcessRewardRequest processRewardRequest= mapper.readValue(processRewardRequestJson, ProcessRewardRequest.class);

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.ok().body(blueprintResponse));

        ProcessRewardResponse response = blueprintRewardService.awardFreeSpins(processRewardRequest, providerConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getStatus(), ProcessRewardStatus.FAILED);
    }


    @Test
    public void should_return_a_successful_response_given_a_successful_blueprint_reward_cancel_response() throws JsonProcessingException {
        String blueprintResponse = fixture(TestConstants.FIXTURE_BLUEPRINT_SUCCESSFUL_GRANT_RESPONSE);
        CancelRewardRequest processRewardRequest= mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_CANCEL_REQUEST), CancelRewardRequest.class);

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.ok().body(blueprintResponse));

        CancelRewardResponse response = blueprintRewardService.cancelFreeSpins(processRewardRequest, providerConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0", response.getCode());
    }

    @Test
    public void should_return_a_failed_response_given_a_failed_blueprint_cancel_response() throws JsonProcessingException {
        String blueprintResponse = fixture(TestConstants.FIXTURE_BLUEPRINT_FAILED_GRANT_RESPONSE);
        CancelRewardRequest processRewardRequest= mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_CANCEL_REQUEST), CancelRewardRequest.class);

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.ok().body(blueprintResponse));

        CancelRewardResponse response = blueprintRewardService.cancelFreeSpins(processRewardRequest, providerConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("-1", response.getCode());
    }

    @Test
    public void should_handle_unexpected_errors() throws JsonProcessingException {
        String blueprintResponse = fixture(TestConstants.FIXTURE_BLUEPRINT_FAILED_GRANT_RESPONSE);
        String processRewardRequestJson = fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST);
        ProcessRewardRequest processRewardRequest= mapper.readValue(processRewardRequestJson, ProcessRewardRequest.class);

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenThrow(new RuntimeException());

        ProcessRewardResponse response = blueprintRewardService.awardFreeSpins(processRewardRequest, providerConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getStatus(), ProcessRewardStatus.FAILED);
    }


    @Test
    public void should_generate_the_correct_operator_id() {
        String results = blueprintRewardService.generateExternalPlayerIdentifier(providerConfig, "livescore_uk", 200L);
        Assertions.assertEquals("LVS-livescore_uk/1200", results);
    }

    @Test
    public void should_build_a_complete_blueprint_reward_request() throws JsonProcessingException {
        ProcessRewardRequest processRewardRequest = mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST), ProcessRewardRequest.class);
        BlueprintRewardRequest blueprintRewardRequest = blueprintRewardService.buildAwardFreeSpinRewardRequest(processRewardRequest, providerConfig);

        long amountInCents = processRewardRequest.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        int numberOfSpins = processRewardRequest.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class);
        String identifier = blueprintRewardService.generateExternalPlayerIdentifier(providerConfig, processRewardRequest.domainName(),
                Long.parseLong(processRewardRequest.getPlayer().getOriginalId()));

        Assertions.assertNotNull(blueprintRewardRequest);
        Assertions.assertEquals(providerConfig.getRewardApiToken(), blueprintRewardRequest.getApiToken());
        Assertions.assertEquals(providerConfig.getJurisdiction(), blueprintRewardRequest.getJurisdiction());
        Assertions.assertEquals(providerConfig.getBrandId(), blueprintRewardRequest.getBrandId());
        Assertions.assertEquals(providerConfig.getCountryCode(), blueprintRewardRequest.getCountryCode());
        Assertions.assertEquals(processRewardRequest.domainCurrency(), blueprintRewardRequest.getCurrencyCode());
        Assertions.assertEquals(numberOfSpins, blueprintRewardRequest.getNumberOfFreeSpins());
        Assertions.assertEquals(amountInCents, blueprintRewardRequest.getCoinValue());
        Assertions.assertEquals(processRewardRequest.getRewardRevisionTypeGames().size(), blueprintRewardRequest.getGames().size());
        Assertions.assertEquals(identifier, blueprintRewardRequest.getPlayerId());
        Assertions.assertEquals(processRewardRequest.getPlayer().isTestAccount(), blueprintRewardRequest.isTestPlayer());

    }

}
