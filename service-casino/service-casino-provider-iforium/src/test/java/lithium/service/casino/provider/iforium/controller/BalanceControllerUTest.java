package lithium.service.casino.provider.iforium.controller;

import lithium.service.Response;
import lithium.service.casino.provider.iforium.AbstractBalance;
import lithium.service.casino.provider.iforium.config.ProviderConfigProperties;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.handler.MainExceptionHandler;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.service.BalanceService;
import lithium.service.casino.provider.iforium.service.impl.BalanceServiceImpl;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static lithium.service.casino.provider.iforium.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.iforium.constant.TestConstants.BALANCE_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.BALANCE_PROPERTY_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.NOT_WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.constant.TestConstants.WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.util.Fixtures.fixture;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockFailureFindByUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessFindByUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessIforiumConfig;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessPropertiesByProviderUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessProviderClient;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class BalanceControllerUTest extends AbstractBalance {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockExternalServices();

        BalanceService balanceService = new BalanceServiceImpl(cachingDomainClientService, casinoClientService, lithiumClientUtils);
        BalanceController balanceController = new BalanceController(balanceService, SECURITY_CONFIG_UTILS);

        mockMvc = MockMvcBuilders.standaloneSetup(balanceController)
                                 .setControllerAdvice(new MainExceptionHandler())
                                 .addPlaceholderValue(BALANCE_PROPERTY_NAME, BALANCE_PATH)
                                 .build();
    }

    @Test
    @SneakyThrows
    void successBalance() {
        mockSuccessIforiumConfig();
        mockSuccessGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    @SneakyThrows
    void successBalance_WhenXForwardedForHeaderContainsList() {
        mockSuccessIforiumConfig();
        mockSuccessGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP, NOT_WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    @SneakyThrows
    void successBalance_WhenXForwardedForHeaderValuesSeparatedByCommas() {
        mockSuccessIforiumConfig();
        mockSuccessGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP + " , " + NOT_WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    @SneakyThrows
    void successBalanceWithOptionalParameters() {
        mockSuccessIforiumConfig();
        mockSuccessGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH,
                                                 TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, TestConstants.TIMESTAMP,
                                                 TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @ParameterizedTest
    @MethodSource("notWellWormedRequest")
    @SneakyThrows
    void failureBalance_WhenRequestNotWellFormed(String body) {
        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(body))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenDomainDoesNotExist() {
        mockSuccessIforiumConfig();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString()))
               .thenThrow(new Status550ServiceDomainClientException("Unable to retrieve domain from domain service: domain"));

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenPlayerIdDoesNotExist() {
        mockSuccessIforiumConfig();
        mockFailureGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenIPNotWhitelisted() {
        mockSuccessIforiumConfig();

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, NOT_WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenXForwardedHeaderContainsNotWhitelistedIpOnFirstPlace() {
        mockSuccessIforiumConfig();

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, NOT_WHITELISTED_IP, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenXForwardedHeaderIsAbsent() {
        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenAuthorizationHeaderIsAbsent() {
        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(Constants.X_FORWARDED_FOR, NOT_WHITELISTED_IP, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenXForwardedHeaderIsEmpty() {
        mockSuccessIforiumConfig();

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, "")
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Basic", "Basic dXNlcm5hbWUxOnBhc3N3b3Jk", "Basic dXNlcm5hbWU6cGFzc3dvcmQx",
                            "Basic dXNlcm5hbWUxOnBhc3N3b3JkMQ=="})
    void failureBalance_WhenUserIsNotAuthorized(String authorization) {
        mockSuccessIforiumConfig();

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenPropertyNotConfigured() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();

        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value(WHITELISTED_IP).build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName()).value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        mockSuccessPropertiesByProviderUrlAndDomainName(providerProperties);

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureBalance_WhenConfigurationIsNotFound() {
        mockSuccessProviderClient();
        mockFailureFindByUrlAndDomainName(Response.Status.NOT_FOUND, null);

        mockMvc.perform(post(BALANCE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY,
                                                 TestConstants.SEQUENCE, TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    private static Stream<Arguments> notWellWormedRequest() {
        return Stream.of(
                Arguments.of(buildBalanceRequest(null, TestConstants.SEQUENCE, new Date(), TestConstants.OPERATOR_ACCOUNT_ID,
                                                 TestConstants.GAME_ID, TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments.of(buildBalanceRequest(TestConstants.PLATFORM_KEY, null, new Date(), TestConstants.OPERATOR_ACCOUNT_ID,
                                                 TestConstants.GAME_ID, TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments
                        .of(buildBalanceRequest(TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, null, TestConstants.OPERATOR_ACCOUNT_ID,
                                                TestConstants.GAME_ID, TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments
                        .of(buildBalanceRequest(TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, new Date(), null, TestConstants.GAME_ID,
                                                TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments.of(buildBalanceRequest(TestConstants.INVALID_LONG_PARAMETER, TestConstants.SEQUENCE, new Date(),
                                                 TestConstants.OPERATOR_ACCOUNT_ID, TestConstants.GAME_ID,
                                                 TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments.of(buildBalanceRequest(TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, new Date(),
                                                 TestConstants.INVALID_LONG_PARAMETER, TestConstants.GAME_ID,
                                                 TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments.of(buildBalanceRequest(TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, new Date(),
                                                 TestConstants.OPERATOR_ACCOUNT_ID, TestConstants.INVALID_LONG_PARAMETER,
                                                 TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments.of(buildBalanceRequest(TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, new Date(),
                                                 TestConstants.OPERATOR_ACCOUNT_ID, TestConstants.GAME_ID,
                                                 TestConstants.INVALID_LONG_PARAMETER)),
                Arguments.of(buildBalanceRequest(TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE, new Date(), "invalidOperatorAccountId",
                                                 TestConstants.GAME_ID, TestConstants.CONTENT_GAME_PROVIDER_ID)),
                Arguments
                        .of("{\"PlatformKey\":\"L100\",\"Sequence\":\"f82f441f-a20f-4244-b760-35d2d05705d7\",\"Timestamp\":invalidTimeStamp,\"OperatorAccountID\":\"domain/accountId\",\"GameID\":\"gameId\",\"ContentGameProviderID\":\"contentGameProviderId\"}")
        );
    }


}
