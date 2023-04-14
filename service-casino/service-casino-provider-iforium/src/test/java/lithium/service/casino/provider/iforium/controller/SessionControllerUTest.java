package lithium.service.casino.provider.iforium.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.casino.provider.iforium.config.ProviderConfigProperties;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.handler.MainExceptionHandler;
import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.request.RedeemSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.casino.provider.iforium.service.SessionService;
import lithium.service.casino.provider.iforium.service.impl.SessionServiceImpl;
import lithium.service.casino.provider.iforium.util.TestMockUtils;
import lithium.service.casino.provider.iforium.util.TestSessionUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static lithium.service.casino.provider.iforium.constant.Constants.X_FORWARDED_FOR;
import static lithium.service.casino.provider.iforium.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.iforium.constant.TestConstants.COUNTRY_CODE;
import static lithium.service.casino.provider.iforium.constant.TestConstants.CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GBP_CURRENCY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.INVALID_LONG_PARAMETER;
import static lithium.service.casino.provider.iforium.constant.TestConstants.NOT_WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_CREATE_TOKEN_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_CREATE_TOKEN_PROPERTY_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_REDEEM_TOKEN_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_REDEEM_TOKEN_PROPERTY_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_TOKEN_TTL;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_WRAP_TOKEN_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_WRAP_TOKEN_PROPERTY_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SUCCESS_REDEEM_SESSION_TOKEN_RESPONSE_FIXTURE_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.util.Fixtures.fixture;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockFailureFindByUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessFindByUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessIforiumConfig;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessPropertiesByProviderUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessProviderClient;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.setUpSystemLoginEventsClientMock;
import static lithium.service.casino.provider.iforium.util.TestSessionUtils.basicValidationForSessionTokenResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class SessionControllerUTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @Mock
    private CachingDomainClientService cachingDomainClientService;

    @BeforeEach
    void setUp() {
        SessionService sessionService = new SessionServiceImpl(lithiumServiceClientFactory, cachingDomainClientService,
                                                               SECURITY_CONFIG_UTILS,
                                                               SESSION_TOKEN_TTL);
        SessionController sessionController = new SessionController(sessionService, SECURITY_CONFIG_UTILS);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(sessionController)
                                 .setControllerAdvice(new MainExceptionHandler())
                                 .addPlaceholderValue(SESSION_WRAP_TOKEN_PROPERTY_NAME, SESSION_WRAP_TOKEN_PATH)
                                 .addPlaceholderValue(SESSION_REDEEM_TOKEN_PROPERTY_NAME, SESSION_REDEEM_TOKEN_PATH)
                                 .addPlaceholderValue(SESSION_CREATE_TOKEN_PROPERTY_NAME, SESSION_CREATE_TOKEN_PATH)
                                 .build();
    }

    @Test
    @SneakyThrows
    void redeemToken_SuccessResponse_WhenRequestIsValid() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenReturn(Domain.builder().currency(GBP_CURRENCY).build());

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(SUCCESS_REDEEM_SESSION_TOKEN_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    void redeemToken_SessionNotFoundResponse_WhenSessionTokenIsExpired() {
        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getExpiredSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.SESSION_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void redeemToken_SessionNotFoundResponse_WhenLoginEventNotFound() {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);

        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.SESSION_NOT_FOUND);
    }

    @Test
    void redeemToken_ApiAuthenticationFailedResponse_WhenLoginEventDoesNotContainUser() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser())
               .thenReturn(null);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @Test
    void redeemToken_ApiAuthenticationFailedResponse_WhenLoginEventUserDoesNotContainGuid() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(null);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @Test
    void redeemToken_ApiAuthenticationFailedResponse_WhenLoginEventDoesNotContainCountryCode() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(null);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @Test
    void redeemToken_ApiAuthenticationFailedResponse_WhenLoginEventDoesNotContainDomain() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain())
               .thenReturn(null);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @Test
    void redeemToken_ApiAuthenticationFailedResponse_WhenLoginEventDomainDoesNotContainName() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(null);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @ParameterizedTest
    @MethodSource("redeemTokenNotWellWormedRequestBodies")
    @SneakyThrows
    void redeemToken_ApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(RedeemSessionTokenRequest request) {
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, objectMapper.writeValueAsString(request), ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @Test
    void redeemToken_UnknownErrorResponse_WhenUnexpectedExceptionIsOccurred() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenThrow(RuntimeException.class);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        performFailureRequest(SESSION_REDEEM_TOKEN_PATH, requestBody, ErrorCodes.UNKNOWN_ERROR);
    }

    @SneakyThrows
    private void performFailureRequest(String path, String requestBody, ErrorCodes errorCode) {
        mockMvc.perform(post(path)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, errorCode.getCode())));
    }

    @Test
    @SneakyThrows
    void successRedeemToken_WhenXForwardedForHeaderContainsList() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenReturn(Domain.builder().currency(GBP_CURRENCY).build());

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP, NOT_WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(SUCCESS_REDEEM_SESSION_TOKEN_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void successRedeemToken_WhenXForwardedForHeaderValuesSeparatedByCommas() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenReturn(Domain.builder().currency(GBP_CURRENCY).build());

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP + ", " + NOT_WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(SUCCESS_REDEEM_SESSION_TOKEN_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenIPNotWhitelisted() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenXForwardedHeaderContainsNotWhitelistedIpOnFirstPlace() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP, WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenXForwardedHeaderIsAbsent() {
        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenAuthorizationHeaderIsAbsent() {
        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP, WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenXForwardedHeaderIsEmpty() {
        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, "")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Basic", "Basic dXNlcm5hbWUxOnBhc3N3b3Jk", "Basic dXNlcm5hbWU6cGFzc3dvcmQx",
                            "Basic dXNlcm5hbWUxOnBhc3N3b3JkMQ=="})
    void failureRedeemToken_WhenUserIsNotAuthorized(String authorization) {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenPropertyNotConfigured() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();

        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties
                .add(ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value(WHITELISTED_IP).build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName())
                                               .value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        mockSuccessPropertiesByProviderUrlAndDomainName(providerProperties);

        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenConfigurationIsNotFound() {
        mockSuccessProviderClient();
        mockFailureFindByUrlAndDomainName(Response.Status.NOT_FOUND, null);

        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        String requestBody = fixture(TestConstants.REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_REDEEM_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    @Test
    @SneakyThrows
    void createToken_SuccessResponse_WhenRequestIsValid() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getSessionKey()).thenReturn(SESSION_KEY);
        Mockito.when(loginEventMock.getLogout()).thenReturn(DateUtils.addHours(new Date(), 2));

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        String response = mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                                  .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                                  .content(requestBody))
                                 .andExpect(status().isOk())
                                 .andReturn().getResponse().getContentAsString();

        SessionTokenResponse actual = objectMapper.readValue(response, SessionTokenResponse.class);

        basicValidationForSessionTokenResponse(actual);
        assertTrue(actual.getResult().getSessionToken().startsWith(SESSION_KEY));
    }

    @ParameterizedTest
    @MethodSource("createTokenNotWellWormedRequestBodies")
    @SneakyThrows
    void createToken_ApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(CreateSessionTokenRequest request) {
        performFailureRequest(SESSION_CREATE_TOKEN_PATH, objectMapper.writeValueAsString(request), ErrorCodes.API_AUTHENTICATION_FAILED);
    }

    @Test
    @SneakyThrows
    void successCreateToken_WhenLogoutDateIsEmpty() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getSessionKey()).thenReturn(SESSION_KEY);
        Mockito.when(loginEventMock.getLogout()).thenReturn(null);

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        String response = mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                                  .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                                  .content(requestBody))
                                 .andExpect(status().isOk())
                                 .andReturn().getResponse().getContentAsString();

        SessionTokenResponse actual = objectMapper.readValue(response, SessionTokenResponse.class);

        basicValidationForSessionTokenResponse(actual);
        assertTrue(actual.getResult().getSessionToken().startsWith(SESSION_KEY));
    }

    @Test
    @SneakyThrows
    void successCreateToken_WhenXForwardedForHeaderContainsList() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getSessionKey()).thenReturn(SESSION_KEY);
        Mockito.when(loginEventMock.getLogout()).thenReturn(DateUtils.addHours(new Date(), 2));

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        String response = mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                                  .header(X_FORWARDED_FOR, WHITELISTED_IP, NOT_WHITELISTED_IP)
                                                  .content(requestBody))
                                 .andExpect(status().isOk())
                                 .andReturn().getResponse().getContentAsString();

        SessionTokenResponse actual = objectMapper.readValue(response, SessionTokenResponse.class);

        basicValidationForSessionTokenResponse(actual);
        assertTrue(actual.getResult().getSessionToken().startsWith(SESSION_KEY));
    }

    @Test
    @SneakyThrows
    void successCreateToken_WhenXForwardedForHeaderValuesSeparatedByCommas() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getSessionKey()).thenReturn(SESSION_KEY);
        Mockito.when(loginEventMock.getLogout()).thenReturn(DateUtils.addHours(new Date(), 2));

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        String response = mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                                  .header(X_FORWARDED_FOR, WHITELISTED_IP + ", " + NOT_WHITELISTED_IP)
                                                  .content(requestBody))
                                 .andExpect(status().isOk())
                                 .andReturn().getResponse().getContentAsString();

        SessionTokenResponse actual = objectMapper.readValue(response, SessionTokenResponse.class);

        basicValidationForSessionTokenResponse(actual);
        assertTrue(actual.getResult().getSessionToken().startsWith(SESSION_KEY));
    }

    @Test
    void failureCreateToken_WhenUnexpectedExceptionIsOccurred() {
        mockSuccessIforiumConfig();
        TestMockUtils.setUpFailureLoginEventMockForCreateToken(lithiumServiceClientFactory, RuntimeException.class);

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        performFailureRequest(SESSION_CREATE_TOKEN_PATH, requestBody, ErrorCodes.UNKNOWN_ERROR);
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenSessionIsExpired() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getLogout()).thenReturn(DateUtils.addHours(new Date(), -2));

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        performFailureRequest(SESSION_CREATE_TOKEN_PATH, requestBody, ErrorCodes.ACCOUNT_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenAccountIsNotExist() {
        mockSuccessIforiumConfig();
        TestMockUtils.setUpFailureLoginEventMockForCreateToken(lithiumServiceClientFactory, Status411UserNotFoundException.class);

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        performFailureRequest(SESSION_CREATE_TOKEN_PATH, requestBody, ErrorCodes.ACCOUNT_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenIPNotWhitelisted() {
        mockSuccessIforiumConfig();

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP, WHITELISTED_IP)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenXForwardedHeaderContainsNotWhitelistedIpOnFirstPlace() {
        mockSuccessIforiumConfig();

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenXForwardedHeaderIsAbsent() {
        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenAuthorizationHeaderIsAbsent() {
        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenXForwardedHeaderIsEmpty() {
        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_FORWARDED_FOR, "")
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Basic", "Basic dXNlcm5hbWUxOnBhc3N3b3Jk", "Basic dXNlcm5hbWU6cGFzc3dvcmQx",
                            "Basic dXNlcm5hbWUxOnBhc3N3b3JkMQ=="})
    void failureCreateToken_WhenUserIsNotAuthorized(String authorization) {
        mockSuccessIforiumConfig();

        String requestBody = fixture(CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH);
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_FORWARDED_FOR, WHITELISTED_IP)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(
                       fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenPropertyNotConfigured() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();

        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value(WHITELISTED_IP).build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName())
                                               .value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        mockSuccessPropertiesByProviderUrlAndDomainName(providerProperties);

        String requestBody = fixture(TestConstants.CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH, TestSessionUtils.getValidSessionToken());
        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(X_FORWARDED_FOR, NOT_WHITELISTED_IP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenConfigurationIsNotFound() {
        mockSuccessProviderClient();
        mockFailureFindByUrlAndDomainName(Response.Status.NOT_FOUND, null);

        mockMvc.perform(post(SESSION_CREATE_TOKEN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(fixture(TestConstants.CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH,
                                                 TestSessionUtils.getValidSessionToken())))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    private static Stream<Arguments> createTokenNotWellWormedRequestBodies() {
        CreateSessionTokenRequest nullPlatformKey = getAndUpdateCreateSessionTokenRequest(r -> r.setPlatformKey(null));
        CreateSessionTokenRequest nullSequence = getAndUpdateCreateSessionTokenRequest(r -> r.setSequence(null));
        CreateSessionTokenRequest nullTimestamp = getAndUpdateCreateSessionTokenRequest(r -> r.setTimestamp(null));
        CreateSessionTokenRequest nullOperatorAccountId = getAndUpdateCreateSessionTokenRequest(r -> r.setOperatorAccountId(null));
        CreateSessionTokenRequest nullGameId = getAndUpdateCreateSessionTokenRequest(r -> r.setGameId(null));

        CreateSessionTokenRequest emptyPlatformKey = getAndUpdateCreateSessionTokenRequest(r -> r.setPlatformKey(""));
        CreateSessionTokenRequest emptySequence = getAndUpdateCreateSessionTokenRequest(r -> r.setSequence(""));
        CreateSessionTokenRequest emptyOperatorAccountId = getAndUpdateCreateSessionTokenRequest(r -> r.setOperatorAccountId(""));
        CreateSessionTokenRequest emptyGameId = getAndUpdateCreateSessionTokenRequest(r -> r.setGameId(""));

        CreateSessionTokenRequest tooLongPlatformKey = getAndUpdateCreateSessionTokenRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        CreateSessionTokenRequest tooLongSequence = getAndUpdateCreateSessionTokenRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        CreateSessionTokenRequest tooLongOperatorAccountId = getAndUpdateCreateSessionTokenRequest(
                r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        CreateSessionTokenRequest tooLongGameId = getAndUpdateCreateSessionTokenRequest(r -> r.setGameId(INVALID_LONG_PARAMETER));

        CreateSessionTokenRequest notWellFormedOperatorAccountId = getAndUpdateCreateSessionTokenRequest(
                r -> r.setOperatorAccountId("qwerty"));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullGameId),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyGameId),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongGameId),

                Arguments.of(notWellFormedOperatorAccountId)
        );
    }

    private static CreateSessionTokenRequest getAndUpdateCreateSessionTokenRequest(Consumer<CreateSessionTokenRequest> consumer) {
        CreateSessionTokenRequest createSessionTokenRequest = TestSessionUtils.validCreateSessionTokenRequest();
        consumer.accept(createSessionTokenRequest);

        return createSessionTokenRequest;
    }

    private static Stream<Arguments> redeemTokenNotWellWormedRequestBodies() {
        RedeemSessionTokenRequest nullPlatformKey = getAndUpdateRedeemSessionTokenRequest(r -> r.setPlatformKey(null));
        RedeemSessionTokenRequest nullSequence = getAndUpdateRedeemSessionTokenRequest(r -> r.setSequence(null));
        RedeemSessionTokenRequest nullTimestamp = getAndUpdateRedeemSessionTokenRequest(r -> r.setTimestamp(null));
        RedeemSessionTokenRequest nullSessionToken = getAndUpdateRedeemSessionTokenRequest(r -> r.setSessionToken(null));
        RedeemSessionTokenRequest nullIpAddress = getAndUpdateRedeemSessionTokenRequest(r -> r.setIPAddress(null));

        RedeemSessionTokenRequest emptyPlatformKey = getAndUpdateRedeemSessionTokenRequest(r -> r.setPlatformKey(""));
        RedeemSessionTokenRequest emptySequence = getAndUpdateRedeemSessionTokenRequest(r -> r.setSequence(""));
        RedeemSessionTokenRequest emptySessionToken = getAndUpdateRedeemSessionTokenRequest(r -> r.setSessionToken(""));
        RedeemSessionTokenRequest emptyIpAddress = getAndUpdateRedeemSessionTokenRequest(r -> r.setIPAddress(""));

        RedeemSessionTokenRequest tooLongPlatformKey = getAndUpdateRedeemSessionTokenRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        RedeemSessionTokenRequest tooLongSequence = getAndUpdateRedeemSessionTokenRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        RedeemSessionTokenRequest tooLongSessionToken = getAndUpdateRedeemSessionTokenRequest(
                r -> r.setSessionToken(INVALID_LONG_PARAMETER));

        RedeemSessionTokenRequest notWellFormedSessionToken = getAndUpdateRedeemSessionTokenRequest(r -> r.setSessionToken("qwerty"));
        RedeemSessionTokenRequest notWellFormedLongHexSessionToken = getAndUpdateRedeemSessionTokenRequest(
                r -> r.setSessionToken("qwe-qw"));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullSessionToken),
                Arguments.of(nullIpAddress),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptySessionToken),
                Arguments.of(emptyIpAddress),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongSessionToken),

                Arguments.of(notWellFormedSessionToken),
                Arguments.of(notWellFormedLongHexSessionToken)
        );
    }

    private static RedeemSessionTokenRequest getAndUpdateRedeemSessionTokenRequest(Consumer<RedeemSessionTokenRequest> consumer) {
        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        consumer.accept(redeemSessionTokenRequest);

        return redeemSessionTokenRequest;
    }
}
