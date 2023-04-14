package lithium.service.reward.provider.casino.blueprint.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.CustomHttpErrorCodeControllerAdvice;
import lithium.service.reward.client.exception.Status467RewardComponentNotSupported;
import lithium.service.reward.provider.casino.blueprint.controller.system.ProcessRewardController;
import lithium.service.reward.provider.casino.blueprint.services.CancelRewardService;
import lithium.service.reward.provider.casino.blueprint.services.ProcessRewardService;
import lithium.service.reward.provider.casino.blueprint.utils.TestConstants;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static lithium.service.reward.provider.casino.blueprint.utils.FixtureUtil.fixture;


@ExtendWith(MockitoExtension.class)
public class ProcessRewardControllerTest {

    @Mock
    private ProcessRewardService processRewardService;

    @Mock
    private CancelRewardService cancelRewardService;


    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Test
    public void should_process_reward_given_and_valid_request_and_response() throws Exception {
        ProcessRewardRequest request = mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST), ProcessRewardRequest.class);

        Mockito.when(processRewardService.processReward(request)).thenReturn(ProcessRewardResponse.builder()
                        .status(ProcessRewardStatus.SUCCESS)
                        .build()
        );

        mockMvc.perform(post(TestConstants.PROCESS_REWARD_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status" ).value(ProcessRewardStatus.SUCCESS.name()));
    }

    @Test
    public void should_cancel_reward_given_and_valid_request_and_response() throws Exception {
        Mockito.when(cancelRewardService.cancelReward(Mockito.any(CancelRewardRequest.class))).thenReturn(CancelRewardResponse.builder()
                .code("0")
                .build());

        mockMvc.perform(post(TestConstants.CANCEL_REWARD_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code" ).value("0"));
    }

    @Test
    public void should_handler_unsupported_reward_component_exception() throws Exception {
        ProcessRewardRequest request = mapper.readValue(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST), ProcessRewardRequest.class);

        Mockito.when(processRewardService.processReward(request)).thenThrow(new Status467RewardComponentNotSupported("unsupported"));

        mockMvc.perform(post(TestConstants.PROCESS_REWARD_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(fixture(TestConstants.FIXTURE_PROCESS_REWARD_REQUEST)))
                .andExpect(status().is(467))
                .andExpect(jsonPath("$.message" ).value("unsupported"));
    }

    @BeforeEach
    void setUp() {

        mapper = new ObjectMapper();

        ProcessRewardController processRewardController = new ProcessRewardController(processRewardService, cancelRewardService);

        mockMvc = MockMvcBuilders.standaloneSetup(processRewardController)
                .setControllerAdvice(new CustomHttpErrorCodeControllerAdvice())
                .build();

    }
}
