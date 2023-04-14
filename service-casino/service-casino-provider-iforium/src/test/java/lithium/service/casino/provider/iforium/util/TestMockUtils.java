package lithium.service.casino.provider.iforium.util;

import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.provider.iforium.IforiumModuleInfo;
import lithium.service.casino.provider.iforium.config.ProviderConfigProperties;
import lithium.service.casino.provider.iforium.config.ProviderConfigService;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static lithium.service.casino.provider.iforium.constant.TestConstants.JASYPT_ENCRYPTOR_PASSWORD;
import static lithium.service.casino.provider.iforium.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.WHITELISTED_IP;
import static org.mockito.ArgumentMatchers.any;

@UtilityClass
public final class TestMockUtils {

    private static final ProviderClient PROVIDER_CLIENT = Mockito.mock(ProviderClient.class);
    private static final LithiumServiceClientFactory LITHIUM_SERVICE_CLIENT_FACTORY = Mockito.mock(LithiumServiceClientFactory.class);
    private static final ProviderConfigService PROVIDER_CONFIG_SERVICE = new ProviderConfigService(LITHIUM_SERVICE_CLIENT_FACTORY,
                                                                                                   new DecryptUtils(
                                                                                                           JASYPT_ENCRYPTOR_PASSWORD),
                                                                                                   Mockito.mock(
                                                                                                           IforiumModuleInfo.class));
    public static final SecurityConfigUtils SECURITY_CONFIG_UTILS = new SecurityConfigUtils(PROVIDER_CONFIG_SERVICE,
                                                                                            new DecryptUtils(JASYPT_ENCRYPTOR_PASSWORD));

    @SneakyThrows
    public static SystemLoginEventsClient setUpSystemLoginEventsClientMock(LithiumServiceClientFactory lithiumServiceClientFactory) {
        SystemLoginEventsClient systemLoginEventsClientMock = Mockito.mock(SystemLoginEventsClient.class);
        Mockito.when(lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true))
               .thenReturn(systemLoginEventsClientMock);

        return systemLoginEventsClientMock;
    }

    @SneakyThrows
    public static LoginEvent setUpLoginEventMockBySessionKey(LithiumServiceClientFactory lithiumServiceClientFactory) {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);

        LoginEvent loginEventMock = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenReturn(loginEventMock);

        return loginEventMock;
    }

    @SneakyThrows
    public static LoginEvent setUpLoginEventMockByOperatorAccountId(LithiumServiceClientFactory lithiumServiceClientFactory) {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);

        LoginEvent loginEventMock = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(systemLoginEventsClientMock.getLastLoginEventForUser(OPERATOR_ACCOUNT_ID))
               .thenReturn(loginEventMock);

        return loginEventMock;
    }

    @SneakyThrows
    public static void setUpFailureLoginEventMockForCreateToken(LithiumServiceClientFactory lithiumServiceClientFactory, Class e) {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);

        Mockito.when(systemLoginEventsClientMock.getLastLoginEventForUser(OPERATOR_ACCOUNT_ID))
               .thenThrow(e);
    }

    public static void mockSuccessIforiumConfig() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();
        mockSuccessPropertiesByProviderUrlAndDomainName(validProviderProperties());
    }

    @SneakyThrows
    public static void mockSuccessProviderClient() {
        Mockito.when(LITHIUM_SERVICE_CLIENT_FACTORY.target(ProviderClient.class, "service-domain", true)).thenReturn(PROVIDER_CLIENT);
    }

    @SneakyThrows
    public static void mockSuccessFindByUrlAndDomainName() {
        Response<Object> response = Response.builder().status(Response.Status.OK).data(Provider.builder().enabled(true).build()).build();
        Mockito.doReturn(response).when(PROVIDER_CLIENT).findByUrlAndDomainName(any(), any());
    }

    @SneakyThrows
    public static void mockFailureFindByUrlAndDomainName(Response.Status status, Provider provider) {
        Response<Object> response = Response.builder().status(status).data(provider).build();
        Mockito.doReturn(response).when(PROVIDER_CLIENT).findByUrlAndDomainName(any(), any());
    }

    @SneakyThrows
    public static void mockSuccessPropertiesByProviderUrlAndDomainName(List<ProviderProperty> providerProperties) {
        Response<Object> response = Response.builder().status(Response.Status.OK).data(providerProperties).build();
        Mockito.doReturn(response).when(PROVIDER_CLIENT).propertiesByProviderUrlAndDomainName(any(), any());
    }

    private static List<ProviderProperty> validProviderProperties() {
        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(
                ProviderProperty.builder().name(ProviderConfigProperties.WHITELIST_IP.getName()).value(WHITELISTED_IP).build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_NAME.getName())
                                               .value("pX65ilUmZW4kR1iVL9EymxHyoVivW66M").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_PASSWORD.getName())
                                               .value("9I+zKGlx3CHQ4DC+TkSR3eeRJBtwrLM0").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.SECURITY_USER_PASSWORD.getName())
                                               .value("9I+zKGlx3CHQ4DC+TkSR3eeRJBtwrLM0").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.CASINO_ID.getName())
                                               .value("S0009").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.LIST_GAME_URL.getName())
                                               .value("URL").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.STARTGAME_BASE_URL.getName())
                                               .value("google.com").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.LOBBY_URL.getName())
                                               .value("https://www.operator.com/lobby").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATIONS_ENABLED.getName())
                                               .value("true").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_OVERRIDE_RTS_13_MODE.getName())
                                               .value("disabled").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_OVERRIDE_CMA_MODE.getName())
                                               .value("disabled").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_SESSION_DURATION.getName())
                                               .value("0").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_INTERVAL.getName())
                                               .value("86400").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_GAME_HISTORY_URL.getName())
                                               .value("https://www.operator.com/history").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.REGULATION_BONUS_URL.getName())
                                               .value("https://www.operator.com/bonus").build());
        providerProperties.add(ProviderProperty.builder().name(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL.getName())
                .value("https://sapirgsuat.blueprintgaming.com/iforium/SAPI.asmx/Progressive").build());
        return providerProperties;
    }

    public static Domain setUpDomainMockByDomainName(CachingDomainClientService cachingDomainClientService)
            throws Status550ServiceDomainClientException {
        Domain domain = Mockito.mock(Domain.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(TestConstants.DOMAIN_NAME))
               .thenReturn(domain);

        return domain;
    }

    public static Game setUpGameMockByGuidAndDomainName(LithiumServiceClientFactory lithiumServiceClientFactory)
            throws Exception {
        Response<Game> gameResponse = setUpGameResponseMockByGuidAndDomainName(lithiumServiceClientFactory);

        Game game = Mockito.mock(Game.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(gameResponse.isSuccessful()).thenReturn(true);
        Mockito.when(gameResponse.getData()).thenReturn(game);

        return game;
    }

    public static Response<Game> setUpGameResponseMockByGuidAndDomainName(
            LithiumServiceClientFactory lithiumServiceClientFactory) throws Exception {
        GamesClient gamesClient = Mockito.mock(GamesClient.class);
        Mockito.doReturn(gamesClient).when(lithiumServiceClientFactory).target(GamesClient.class, "service-games", true);

        Response<Game> gameResponse = Mockito.mock(Response.class);
        Mockito.when(gamesClient.findByGuidAndDomainNameNoLabels(TestConstants.DOMAIN_NAME,
                                                                 TestConstants.MODULE_NAME + "_" + TestConstants.GAME_ID))
               .thenReturn(gameResponse);
        return gameResponse;
    }

    public void setUpGameResponseMockByGuidAndDomainNameReturnNull(
            LithiumServiceClientFactory lithiumServiceClientFactory) throws Exception {
        GamesClient gamesClient = Mockito.mock(GamesClient.class);
        Mockito.doReturn(gamesClient).when(lithiumServiceClientFactory).target(GamesClient.class, "service-games", true);

        Mockito.when(gamesClient.findByGuidAndDomainNameNoLabels(TestConstants.DOMAIN_NAME,
                                                                 TestConstants.MODULE_NAME + "_" + TestConstants.GAME_ID))
               .thenReturn(null);
    }

    public void setUpGameResponseMockByGuidAndDomainNameReturnException(
            LithiumServiceClientFactory lithiumServiceClientFactory) throws Exception {
        GamesClient gamesClient = Mockito.mock(GamesClient.class);
        Mockito.doReturn(gamesClient).when(lithiumServiceClientFactory).target(GamesClient.class, "service-games", true);

        Mockito.when(gamesClient.findByGuidAndDomainNameNoLabels(TestConstants.DOMAIN_NAME,
                                                                 TestConstants.MODULE_NAME + "_" + TestConstants.GAME_ID))
               .thenThrow(Exception.class);
    }

    public static BalanceAdjustmentResponse setUpBalanceAdjustmentResponseMockForMultiBetV1(CasinoClientService casinoClientService)
            throws Exception {
        BalanceAdjustmentResponse balanceAdjustmentResponse = Mockito.mock(BalanceAdjustmentResponse.class);
        Mockito.doReturn(balanceAdjustmentResponse).when(casinoClientService).multiBetV1(any(), any());

        return balanceAdjustmentResponse;
    }
}
