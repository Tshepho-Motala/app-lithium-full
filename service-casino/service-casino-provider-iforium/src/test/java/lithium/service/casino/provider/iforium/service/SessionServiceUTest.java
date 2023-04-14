package lithium.service.casino.provider.iforium.service;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.config.ProviderConfigProperties;
import lithium.service.casino.provider.iforium.exception.PropertyNotConfiguredException;
import lithium.service.casino.provider.iforium.exception.SessionKeyExpiredException;
import lithium.service.casino.provider.iforium.exception.SessionTokenExpiredException;
import lithium.service.casino.provider.iforium.exception.UpstreamValidationFailedException;
import lithium.service.casino.provider.iforium.model.request.RedeemSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.RedeemSessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.casino.provider.iforium.service.impl.SessionServiceImpl;
import lithium.service.casino.provider.iforium.util.TestMockUtils;
import lithium.service.casino.provider.iforium.util.TestSessionUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lombok.SneakyThrows;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lithium.service.casino.provider.iforium.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.iforium.constant.TestConstants.COUNTRY_CODE;
import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GBP_CURRENCY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.NOT_WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_TOKEN_LENGTH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_TOKEN_TTL;
import static lithium.service.casino.provider.iforium.constant.TestConstants.WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockFailureFindByUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessFindByUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessIforiumConfig;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessPropertiesByProviderUrlAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.mockSuccessProviderClient;
import static lithium.service.casino.provider.iforium.util.TestSessionUtils.basicValidationForSessionTokenResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
class SessionServiceUTest {

    private SessionService sessionService;

    @Mock
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @Mock
    private CachingDomainClientService cachingDomainClientService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl(lithiumServiceClientFactory, cachingDomainClientService, SECURITY_CONFIG_UTILS, SESSION_TOKEN_TTL);
    }

    @Test
    @SneakyThrows
    void wrapToken_ReturnsSuccessResponse_WhenRequestIsValid() {
        String actual = sessionService.wrapSessionToken(SESSION_KEY);

        assertThat(actual).contains(SESSION_KEY)
                          .hasSize(SESSION_TOKEN_LENGTH);
    }

    @Test
    @SneakyThrows
    void redeemToken_ReturnsSuccessResponse_WhenRequestIsValid() {
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

        RedeemSessionTokenResponse actual = sessionService.redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION, WHITELISTED_IP);

        assertEquals(TestSessionUtils.getSuccessRedeemSessionTokenResponse(), actual);
    }

    @Test
    void redeemToken_ThrowsSessionTokenExpiredException_WhenSessionTokenIsExpired() {
        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        redeemSessionTokenRequest.setSessionToken(TestSessionUtils.getExpiredSessionToken());

        Assertions.assertThrows(SessionTokenExpiredException.class, () -> sessionService.redeemToken(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
    }

    @Test
    void redeemToken_ThrowsUpstreamValidationFailedException_WhenLoginEventDoesNotContainUser() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser())
               .thenReturn(null);

        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        Assertions.assertThrows(UpstreamValidationFailedException.class, () -> sessionService.redeemToken(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
    }

    @Test
    void redeemToken_ThrowsUpstreamValidationFailedException_WhenLoginEventUserDoesNotContainGuid() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(null);

        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        Assertions.assertThrows(UpstreamValidationFailedException.class, () -> sessionService.redeemToken(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
    }

    @Test
    void redeemToken_ThrowsUpstreamValidationFailedException_WhenLoginEventDoesNotContainCountryCode() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(null);

        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        Assertions.assertThrows(UpstreamValidationFailedException.class, () -> sessionService.redeemToken(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
    }

    @Test
    void redeemToken_ThrowsUpstreamValidationFailedException_WhenLoginEventDoesNotContainDomain() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain())
               .thenReturn(null);

        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        Assertions.assertThrows(UpstreamValidationFailedException.class, () -> sessionService.redeemToken(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
    }

    @Test
    void redeemToken_ThrowsUpstreamValidationFailedException_WhenLoginEventDomainDoesNotContainName() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(null);

        RedeemSessionTokenRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
        Assertions.assertThrows(UpstreamValidationFailedException.class, () -> sessionService.redeemToken(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
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

        RedeemSessionTokenResponse actual = sessionService.redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION,
                                                                       WHITELISTED_IP + ", " + NOT_WHITELISTED_IP);

        assertEquals(TestSessionUtils.getSuccessRedeemSessionTokenResponse(), actual);
    }

    @Test
    @SneakyThrows
    void successRedeemToken_WhenLoginEventReturnCountryCodeInLowerCase() {
        mockSuccessIforiumConfig();
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn("gb");
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenReturn(Domain.builder().currency(GBP_CURRENCY).build());

        RedeemSessionTokenResponse actual = sessionService.redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION,
                                                                       WHITELISTED_IP + ", " + NOT_WHITELISTED_IP);

        assertEquals(TestSessionUtils.getSuccessRedeemSessionTokenResponse(), actual);
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

        AuthorizationServiceException exception = assertThrows(AuthorizationServiceException.class, () -> sessionService
                .redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION, NOT_WHITELISTED_IP));

        assertThat(exception.getMessage()).contains("RemoteAddress=8.8.8.8 is not whitelisted.");
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

        AuthorizationServiceException exception = assertThrows(AuthorizationServiceException.class, () -> sessionService
                .redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION, NOT_WHITELISTED_IP + ", " + WHITELISTED_IP));

        assertThat(exception.getMessage()).contains("RemoteAddress=8.8.8.8 is not whitelisted.");
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

        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, () -> sessionService
                .redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), authorization, WHITELISTED_IP));

        assertThat(exception.getMessage()).contains("Bad credentials. User=username is not authorized");
    }

    @Test
    @SneakyThrows
    void failureRedeemToken_WhenPropertyNotConfigured() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();

        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value(WHITELISTED_IP).build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName()).value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        mockSuccessPropertiesByProviderUrlAndDomainName(providerProperties);

        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getUser().getGuid())
               .thenReturn(OPERATOR_ACCOUNT_ID);
        Mockito.when(loginEventMock.getCountryCode())
               .thenReturn(COUNTRY_CODE);
        Mockito.when(loginEventMock.getDomain().getName())
               .thenReturn(DOMAIN_NAME);

        PropertyNotConfiguredException exception = assertThrows(PropertyNotConfiguredException.class, () -> sessionService
                .redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION, WHITELISTED_IP));

        assertThat(exception.getMessage()).contains("Property=secureUserPassword is not configured in BO");
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

        Status512ProviderNotConfiguredException exception = assertThrows(Status512ProviderNotConfiguredException.class, () -> sessionService
                .redeemToken(TestSessionUtils.validRedeemSessionTokenRequest(), AUTHORIZATION, WHITELISTED_IP));

        assertThat(exception.getMessage()).contains("The provider is not configured for this domain: domain");
    }

    @Test
    @SneakyThrows
    void createToken_ReturnsSuccessResponse_WhenRequestIsValid() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);

        Mockito.when(loginEventMock.getSessionKey()).thenReturn(SESSION_KEY);
        Mockito.when(loginEventMock.getLogout()).thenReturn(DateUtils.addHours(new Date(), 2));

        SessionTokenResponse actual = sessionService.createToken(TestSessionUtils.validCreateSessionTokenRequest());

        basicValidationForSessionTokenResponse(actual);
        assertTrue(actual.getResult().getSessionToken().startsWith(SESSION_KEY));
    }

    @Test
    void failureCreateToken_WhenUnexpectedExceptionIsOccurred() {
        TestMockUtils.setUpFailureLoginEventMockForCreateToken(lithiumServiceClientFactory, RuntimeException.class);
        Assertions.assertThrows(RuntimeException.class, () -> sessionService.createToken(TestSessionUtils.validCreateSessionTokenRequest()));
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenSessionIsExpired() {
        LoginEvent loginEventMock = TestMockUtils.setUpLoginEventMockByOperatorAccountId(lithiumServiceClientFactory);
        Mockito.when(loginEventMock.getLogout()).thenReturn(DateUtils.addHours(new Date(), -2));
        SessionKeyExpiredException exception = Assertions.assertThrows(SessionKeyExpiredException.class,
                                                                       () -> sessionService.createToken(TestSessionUtils.validCreateSessionTokenRequest()));
        assertThat(exception.getMessage()).isEqualTo("SessionKey=null for OperatorAccountId=domain/accountId is expired.");
    }

    @Test
    @SneakyThrows
    void failureCreateToken_WhenAccountIsNotExist() {
        TestMockUtils.setUpFailureLoginEventMockForCreateToken(lithiumServiceClientFactory, Status411UserNotFoundException.class);
        Assertions.assertThrows(Status411UserNotFoundException.class, () -> sessionService.createToken(TestSessionUtils.validCreateSessionTokenRequest()));
    }
}