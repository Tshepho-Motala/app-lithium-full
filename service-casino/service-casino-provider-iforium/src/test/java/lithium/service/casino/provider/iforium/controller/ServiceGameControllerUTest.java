package lithium.service.casino.provider.iforium.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.IforiumModuleInfo;
import lithium.service.casino.provider.iforium.config.IforiumProviderConfig;
import lithium.service.casino.provider.iforium.config.ProviderConfigService;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.exception.SessionTokenExpiredException;
import lithium.service.casino.provider.iforium.handler.InternalServiceExceptionHandler;
import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResult;
import lithium.service.casino.provider.iforium.service.ListGameService;
import lithium.service.casino.provider.iforium.service.SessionService;
import lithium.service.casino.provider.iforium.service.StartGameService;
import lithium.service.casino.provider.iforium.service.impl.ListGameServiceImpl;
import lithium.service.casino.provider.iforium.service.impl.ServiceGameServiceImpl;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.games.client.objects.Game;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GAME_GUID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GAME_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GBP_CURRENCY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.LANG;
import static lithium.service.casino.provider.iforium.constant.TestConstants.LIST_GAME_PATH;
import static lithium.service.casino.provider.iforium.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_CURRENCY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_GAME_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_LANG;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_MACHINE_GUID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_OS;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_PLATFORM;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_TOKEN;
import static lithium.service.casino.provider.iforium.constant.TestConstants.QUERY_PARAM_TUTORIAL;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.TOGGLE_LOCKED_PATH;
import static lithium.service.casino.provider.iforium.util.Fixtures.fixture;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
public class ServiceGameControllerUTest {
    public static final String STRING_QUERY_PARAM = "anyString";
    public static final String NUMBER_QUERY_PARAM = "1";
    public static final String PARAM_NOT_PRESENT_TEMPLATE = "Required request parameter '%s' for method parameter type String is not present";
    public static ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    protected TokenStore tokenStore;
    protected CachingDomainClientService cachingDomainClientService;
    protected LithiumConfigurationProperties lithiumConfigurationProperties;
    protected ProviderConfigService providerConfigService;
    protected IforiumModuleInfo iforiumModuleInfo;
    protected SessionService sessionService;
    protected OAuth2AccessToken oAuth2AccessToken;
    protected UserApiInternalClientService userService;
    protected ModuleInfo moduleInfo;
    protected LocaleContextProcessor localeContextProcessor;
    protected LimitInternalSystemService limitInternalSystemService;

    @BeforeEach
    void setUp() {
        mockExternalServices();
        StartGameService startGameService = new ServiceGameServiceImpl(tokenStore, cachingDomainClientService,
                                                                       lithiumConfigurationProperties,
                                                                       providerConfigService, iforiumModuleInfo, sessionService,
                                                                       userService,
                limitInternalSystemService);

        ListGameService listGameService = new ListGameServiceImpl(providerConfigService, moduleInfo);
        ServiceGameController startGameController = new ServiceGameController(startGameService, listGameService);
        startGameController.setLocaleContextProcessor(localeContextProcessor);
        this.mockMvc = MockMvcBuilders.standaloneSetup(startGameController)
                                      .setControllerAdvice(new InternalServiceExceptionHandler())
                                      .build();
    }

    @SneakyThrows
    protected void mockExternalServices() {
        tokenStore = Mockito.mock(TokenStore.class);
        cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
        lithiumConfigurationProperties = Mockito.mock(LithiumConfigurationProperties.class);
        providerConfigService = Mockito.mock(ProviderConfigService.class);
        iforiumModuleInfo = Mockito.mock(IforiumModuleInfo.class);
        sessionService = Mockito.mock(SessionService.class);
        oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        userService = Mockito.mock(UserApiInternalClientService.class);
        moduleInfo = Mockito.mock(ModuleInfo.class);
        localeContextProcessor = Mockito.mock(LocaleContextProcessor.class);
        limitInternalSystemService = Mockito.mock(LimitInternalSystemService.class);
    }

    @Test
    @SneakyThrows
    void startGame_Return200_WhenRequestIsValid() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(lithiumConfigurationProperties.getGatewayPublicUrl()).thenReturn(TestConstants.LOCAL_GATEWAY_URL);
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME)).thenReturn(validProviderConfig());
        Mockito.when(iforiumModuleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());

        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(false)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_START_GAME_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void startGame_Return200_WhenRegulationsDisabled() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(lithiumConfigurationProperties.getGatewayPublicUrl()).thenReturn(TestConstants.LOCAL_GATEWAY_URL);
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenReturn(validProviderConfigWithDisableRegulations());
        Mockito.when(iforiumModuleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(false)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_START_GAME_RESPONSE_WITHOUT_REGULATIONS_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void startGame_Return200_WithOptionalParameters() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(lithiumConfigurationProperties.getGatewayPublicUrl()).thenReturn(TestConstants.LOCAL_GATEWAY_URL);
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME)).thenReturn(validProviderConfig());
        Mockito.when(iforiumModuleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(true)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_START_GAME_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void listGame_Return200AndList_WhenURLIsNotEmpty() {
        String filename = "testdata/csv_gamelist-sample.csv";
        ClassLoader classLoader = ServiceGameControllerUTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(filename).getFile());
        assertTrue("Mock HTML File " + filename + " not found", file.exists());

        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenReturn(listGameUrlProviderConfig(file.toURI().toURL().toString()));
        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        mockMvc.perform(post(LIST_GAME_PATH, DOMAIN_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_LIST_GAME_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void listGame_Return500_WhenListGamesUrlLIsEmpty() {
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME)).thenReturn(listGameUrlProviderConfig(""));

        mockMvc.perform(post(LIST_GAME_PATH, DOMAIN_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("500"))
               .andExpect(jsonPath("$.message").value("listGamesUrl not configured"));
    }

    @Test
    @SneakyThrows
    void listGame_Return500_WhenGameListFileWithoutHeader() {
        String filename = "testdata/csv_gamelist-sample-without-header.csv";
        ClassLoader classLoader = ServiceGameControllerUTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(filename).getFile());
        assertTrue("Mock HTML File " + filename + " not found", file.exists());

        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenReturn(listGameUrlProviderConfig(file.toURI().toURL().toString()));

        mockMvc.perform(post(LIST_GAME_PATH, DOMAIN_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("500"))
               .andExpect(jsonPath("$.message", Matchers.containsString("Error parsing listGames url")));
    }

    @Test
    @SneakyThrows
    void listGame_Return500_WhenDomainNameIsNotValid() {
        Mockito.when(providerConfigService.getIforiumConfig(DOMAIN_NAME))
               .thenThrow(new Status512ProviderNotConfiguredException(DOMAIN_NAME));

        mockMvc.perform(post(LIST_GAME_PATH, DOMAIN_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("512"))
               .andExpect(jsonPath("$.message").value("The provider is not configured for this domain: domain"));
    }

    @Test
    @SneakyThrows
    void listGame_Return500_WhenGameListURLIsWrong() {
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME)).thenReturn(listGameUrlProviderConfig("http://invalid_url"));

        mockMvc.perform(post(LIST_GAME_PATH, DOMAIN_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("500"))
               .andExpect(jsonPath("$.message").value("Error parsing listGames url"));
    }

    @SneakyThrows
    @Test
    void listGame_Return500_WhenGameListURLIsInvalid() {
        String url = "invalid_url";
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenReturn(listGameUrlProviderConfig(url));

        mockMvc.perform(post(LIST_GAME_PATH, DOMAIN_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("500"))
               .andExpect(jsonPath("$.message").value(format("listGamesUrl=%s is invalid", url)));

    }

    @Test
    @SneakyThrows
    void startGame_Return500AndLithiumStatusCode550_WhenDomainNameIsNotValid() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString()))
               .thenThrow(new Status550ServiceDomainClientException("Unable to retrieve domain from domain service: notExistDomainName"));
        mockMvc.perform(post(TestConstants.START_GAME_PATH, "notExistDomainName")
                                .params(validStartGameParameters(false))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("550"))
               .andExpect(jsonPath("$.message").value("Unable to retrieve domain from domain service: notExistDomainName"));
    }

    @Test
    @SneakyThrows
    void startGame_Return500AndLithiumStatusCode512_WhenDomainIsNotConfigured() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenThrow(new Status512ProviderNotConfiguredException(DOMAIN_NAME));
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(false))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("512"))
               .andExpect(jsonPath("$.message").value("The provider is not configured for this domain: domain"));
    }

    @Test
    @SneakyThrows
    void startGame_Return401AndLithiumStatusCode401_WhenTokenIsExpired() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenThrow(new SessionTokenExpiredException(
                "SessionKey=" + SESSION_KEY + " for OperatorAccountId=" + OPERATOR_ACCOUNT_ID + " is expired."));
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(false))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.lithiumStatusCode").value("401"))
               .andExpect(jsonPath("$.message").value(
                       "SessionKey=72542ee1-eca8-4df9-93cb-76a5555e3da2 for OperatorAccountId=domain/accountId is expired."));
    }

    @Test
    @SneakyThrows
    void startGame_Return401AndLithiumStatusCode401_WhenTokenIsInvalid() {
        Mockito.when(tokenStore.readAccessToken(anyString()))
               .thenThrow(new InvalidTokenException("Signature length not correct: got 32 but was expecting 512"));

        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(false))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.lithiumStatusCode").value("401"))
               .andExpect(jsonPath("$.message").value("Signature length not correct: got 32 but was expecting 512"));
    }

    @Test
    @SneakyThrows
    void startGame_Return500AndLithiumStatusCode500_WhenUserServiceReturnUserClientServiceFactoryException() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(userService.getUserByGuid(anyString())).thenThrow(new UserClientServiceFactoryException("some message"));

        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(validStartGameParameters(false))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("500"))
               .andExpect(jsonPath("$.message").value("startGame URL exception [domainName: domain, gameId: gameId] some message"));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("startGameNotWellWormedRequest")
    void startGame_Return400AndLithiumStatusCode400_WhenRequestIsNotWellFormed(MultiValueMap<String, String> parameters,
                                                                               String parameterName) {
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                                .params(parameters)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.lithiumStatusCode").value("400"))
               .andExpect(jsonPath("$.message").value(String.format(PARAM_NOT_PRESENT_TEMPLATE, parameterName)));
    }

    @Test
    @SneakyThrows
    void demoGame_Return200_WhenRequestIsValid() {
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(lithiumConfigurationProperties.getGatewayPublicUrl()).thenReturn(TestConstants.LOCAL_GATEWAY_URL);
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME)).thenReturn(validProviderConfig());
        Mockito.when(iforiumModuleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        mockMvc.perform(post(TestConstants.DEMO_GAME_PATH, DOMAIN_NAME)
                                .params(validDemoGameParameters()))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_DEMO_GAME_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void demoGame_Return200_WhenRegulationsDisabled() {
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(lithiumConfigurationProperties.getGatewayPublicUrl()).thenReturn(TestConstants.LOCAL_GATEWAY_URL);
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenReturn(validProviderConfigWithDisableRegulations());
        Mockito.when(iforiumModuleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        mockMvc.perform(post(TestConstants.DEMO_GAME_PATH, DOMAIN_NAME)
                                .params(validDemoGameParameters()))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_DEMO_GAME_RESPONSE_WITHOUT_REGULATIONS_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void demoGame_Return500AndLithiumStatusCode550_WhenDomainNameIsNotValid() {
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString()))
               .thenThrow(new Status550ServiceDomainClientException("Unable to retrieve domain from domain service: notExistDomainName"));

        mockMvc.perform(post(TestConstants.DEMO_GAME_PATH, "notExistDomainName")
                                .params(validDemoGameParameters())
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("550"))
               .andExpect(jsonPath("$.message").value("Unable to retrieve domain from domain service: notExistDomainName"));
    }

    @Test
    @SneakyThrows
    void demoGame_Return500AndLithiumStatusCode512_WhenDomainIsNotConfigured() {
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME))
               .thenThrow(new Status512ProviderNotConfiguredException(DOMAIN_NAME));

        mockMvc.perform(post(TestConstants.DEMO_GAME_PATH, DOMAIN_NAME)
                                .params(validDemoGameParameters())
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.lithiumStatusCode").value("512"))
               .andExpect(jsonPath("$.message").value("The provider is not configured for this domain: domain"));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("demoGameNotWellWormedRequest")
    void demoGame_Return400AndLithiumStatusCode400_WhenRequestIsNotWellFormed(MultiValueMap<String, String> parameters,
                                                                              String parameterName) {
        mockMvc.perform(post(TestConstants.DEMO_GAME_PATH, DOMAIN_NAME)
                                .params(parameters)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.lithiumStatusCode").value("400"))
               .andExpect(jsonPath("$.message").value(String.format(PARAM_NOT_PRESENT_TEMPLATE, parameterName)));
    }

    @Test
    @SneakyThrows
    void listFrbGamesReturn501() {
        performRequestWithExpectedNotImplementedStatus(TestConstants.LIST_FRB_GAMES_REPORT_PATH, DOMAIN_NAME);
    }

    @Test
    @SneakyThrows
    void toggleLockedReturn501() {
        String gameBody = mapper.writeValueAsString(Game.builder().id(1).build());
        mockMvc.perform(post(TOGGLE_LOCKED_PATH, 1L)
                .content(gameBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(fixture(TestConstants.FAILURE_LITHIUM_RESPONSE_NOT_IMPLEMENTED_STATUS_501_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void listAddGameReturn501() {
        mockMvc.perform(post(TestConstants.ADD_GAME_PATH)
                .param("providerGuid", STRING_QUERY_PARAM)
                .param("providerGameId", STRING_QUERY_PARAM)
                .param("gameName", STRING_QUERY_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.lithiumStatusCode").value(HttpStatus.NOT_IMPLEMENTED.value()))
                .andExpect(jsonPath("$.message").value("Not yet implemented"));
    }

    @Test
    @SneakyThrows
    void findByIdReturn501() {
        performRequestWithExpectedNotImplementedStatus(TestConstants.FIND_BY_ID_PATH, 1);
    }

    @Test
    @SneakyThrows
    void editGraphicReturn501() {
        MultipartFile multipartFile = new MockMultipartFile("file", new byte[5]);
        mockMvc.perform(multipart(TestConstants.EDIT_GRAPHIC_PATH, 1L, STRING_QUERY_PARAM)
                .file("file", multipartFile.getBytes())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.lithiumStatusCode").value(HttpStatus.NOT_IMPLEMENTED.value()))
                .andExpect(jsonPath("$.message").value("Not yet implemented"));
    }

    @Test
    @SneakyThrows
    void editReturn501() {
        String gameBody = mapper.writeValueAsString(Game.builder().id(1).build());
        mockMvc.perform(post(TestConstants.EDIT_PATH)
                .content(gameBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.lithiumStatusCode").value(HttpStatus.NOT_IMPLEMENTED.value()))
                .andExpect(jsonPath("$.message").value("Not yet implemented"));
    }

    @Test
    @SneakyThrows
    void listDomainGamesReturn501() {
        performRequestWithExpectedNotImplementedStatus(TestConstants.LIST_DOMAIN_GAMES_PATH, DOMAIN_NAME);
    }

    @Test
    @SneakyThrows
    void findByGuidAndDomainNameReturn501() {
        performRequestWithExpectedNotImplementedStatus(TestConstants.FIND_BY_GUID_AND_DOMAIN_NAME_PATH, DOMAIN_NAME, GAME_GUID);
    }

    @Test
    @SneakyThrows
    void findByGuidAndDomainNameNoLabelsReturn501() {
        performRequestWithExpectedNotImplementedStatus(TestConstants.FIND_BY_GUID_AND_DOMAIN_NAME_NO_LABELS_PATH, DOMAIN_NAME, GAME_GUID);
    }

    @Test
    @SneakyThrows
    void listDomainGamesDTReturnEmptyResponse() {
        mockMvc.perform(post(TestConstants.LIST_DOMAIN_GAMES_DT_PATH, DOMAIN_NAME)
                .param("draw", STRING_QUERY_PARAM)
                .param("start", NUMBER_QUERY_PARAM)
                .param("length", NUMBER_QUERY_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(fixture(TestConstants.SUCCESS_EMPTY_DATATABLE_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void listDomainGamesReportReturnEmptyResponse() {
        mockMvc.perform(post(TestConstants.LIST_DOMAIN_GAMES_REPORT_PATH, DOMAIN_NAME)
                .param("draw", STRING_QUERY_PARAM)
                .param("start", NUMBER_QUERY_PARAM)
                .param("length", NUMBER_QUERY_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(fixture(TestConstants.SUCCESS_EMPTY_DATATABLE_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void isGameLockedForPlayerReturnLithium501Response() {
        mockMvc.perform(post(TestConstants.IS_GAME_LOCKED_FOR_PLAYER_PATH, DOMAIN_NAME)
                .param("gameGuid", STRING_QUERY_PARAM)
                .param("playerGuid", STRING_QUERY_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(fixture(TestConstants.FAILURE_LITHIUM_RESPONSE_NOT_IMPLEMENTED_STATUS_501_RESPONSE_FIXTURE_PATH)));
    }

    @SneakyThrows
    private void performRequestWithExpectedNotImplementedStatus(String path, Object... pathParameters) {
        mockMvc.perform(post(path, pathParameters)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotImplemented())
               .andExpect(jsonPath("$.lithiumStatusCode").value(HttpStatus.NOT_IMPLEMENTED.value()))
               .andExpect(jsonPath("$.message").value("Not yet implemented"));
    }

    @Test
    @SneakyThrows
    void startGame_200_WhenUserIsVerified() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(lithiumConfigurationProperties.getGatewayPublicUrl()).thenReturn(TestConstants.LOCAL_GATEWAY_URL);
        Mockito.when(providerConfigService.getIforiumConfig(TestConstants.DOMAIN_NAME)).thenReturn(validProviderConfig());
        Mockito.when(iforiumModuleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                        .params(validStartGameParameters(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(fixture(TestConstants.SUCCESS_START_GAME_RESPONSE_FIXTURE_PATH)));
    }

    @Test
    @SneakyThrows
    void startGame_500AndLithiumCode483_WhenUserIsUnVerified() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        Mockito.doThrow(new Status483PlayerCasinoNotAllowedException()).when(limitInternalSystemService).checkPlayerCasinoAllowed(anyString());
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                        .params(validStartGameParameters(true)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.lithiumStatusCode").value(483))
                .andExpect(jsonPath("$.message").value("Player casino not allowed"));
    }

    @Test
    @SneakyThrows
    void startGame_500AndLithiumCode483_WhenUserHasCasinoBlock() {
        Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
        Mockito.when(sessionService.createToken(any(CreateSessionTokenRequest.class))).thenReturn(validResponse());
        Mockito.when(userService.getUserByGuid(anyString())).thenReturn(getUser());
        Mockito.doThrow(new Status483PlayerCasinoNotAllowedException()).when(limitInternalSystemService).checkPlayerCasinoAllowed(anyString());
        mockMvc.perform(post(TestConstants.START_GAME_PATH, DOMAIN_NAME)
                        .params(validStartGameParameters(true)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.lithiumStatusCode").value(483))
                .andExpect(jsonPath("$.message").value("Player casino not allowed"));
    }

    private static Stream<Arguments> startGameNotWellWormedRequest() {
        MultiValueMap<String, String> absentToken = getAndUpdateStartGameParameters(p -> p.remove(QUERY_PARAM_TOKEN));
        MultiValueMap<String, String> absentGameId = getAndUpdateStartGameParameters(p -> p.remove(QUERY_PARAM_GAME_ID));
        MultiValueMap<String, String> absentLang = getAndUpdateStartGameParameters(p -> p.remove(QUERY_PARAM_LANG));

        return Stream.of(
                Arguments.of(absentToken, QUERY_PARAM_TOKEN),
                Arguments.of(absentGameId, QUERY_PARAM_GAME_ID),
                Arguments.of(absentLang, QUERY_PARAM_LANG)
        );
    }

    private static Stream<Arguments> demoGameNotWellWormedRequest() {
        MultiValueMap<String, String> absentGameId = getAndUpdateDemoGameParameters(p -> p.remove(QUERY_PARAM_GAME_ID));
        MultiValueMap<String, String> absentLang = getAndUpdateDemoGameParameters(p -> p.remove(QUERY_PARAM_LANG));

        return Stream.of(
                Arguments.of(absentGameId, QUERY_PARAM_GAME_ID),
                Arguments.of(absentLang, QUERY_PARAM_LANG)
        );
    }

    private static MultiValueMap<String, String> validStartGameParameters(boolean optional) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(QUERY_PARAM_TOKEN, "token");
        parameters.add(QUERY_PARAM_GAME_ID, GAME_ID);
        parameters.add(QUERY_PARAM_LANG, LANG);
        parameters.add(QUERY_PARAM_CURRENCY, GBP_CURRENCY);
        parameters.add(QUERY_PARAM_OS, "mac");

        if (optional) {
            parameters.add(QUERY_PARAM_MACHINE_GUID, UUID.randomUUID().toString());
            parameters.add(QUERY_PARAM_TUTORIAL, "false");
            parameters.add(QUERY_PARAM_PLATFORM, "desktop");
        }

        return parameters;
    }

    private static MultiValueMap<String, String> validDemoGameParameters() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(QUERY_PARAM_GAME_ID, GAME_ID);
        parameters.add(QUERY_PARAM_LANG, LANG);
        parameters.add(QUERY_PARAM_OS, "mac");

        return parameters;
    }

    private static MultiValueMap<String, String> getAndUpdateStartGameParameters(Consumer<MultiValueMap<String, String>> consumer) {
        MultiValueMap<String, String> parameters = validStartGameParameters(false);
        consumer.accept(parameters);
        return parameters;
    }

    private static MultiValueMap<String, String> getAndUpdateDemoGameParameters(Consumer<MultiValueMap<String, String>> consumer) {
        MultiValueMap<String, String> parameters = validDemoGameParameters();
        consumer.accept(parameters);
        return parameters;
    }

    private static User getUser() {
        User user = new User();
        user.setGuid(TestConstants.OPERATOR_ACCOUNT_ID);
        user.setAgeVerified(true);
        user.setAddressVerified(true);
        return user;
    }

    private static SessionTokenResponse validResponse() {
        SessionTokenResponse response = new SessionTokenResponse();
        response.setResult(new SessionTokenResult("3Dddbd9956-5224-49d4-bfa2-c288b126938d-17be3568291"));
        return response;
    }

    public static Domain validDomain() {
        return Domain.builder().name(TestConstants.DOMAIN_NAME).currency(TestConstants.GBP_CURRENCY).build();
    }

    private static IforiumProviderConfig validProviderConfig() {
        return IforiumProviderConfig.builder()
                                    .startGameUrl("baseurl.com")
                                    .casinoId("S0009")
                                    .lobbyUrl("https://www.operator.com/lobby")
                                    .listGameUrl("")
                                    .regulationsEnabled(true)
                                    .regulationSessionDuration(0)
                                    .regulationInterval(86400)
                                    .regulationGameHistoryUrl("https://www.operator.com/history")
                                    .regulationBonusUrl("https://www.operator.com/bonud")
                                    .regulationOverrideRts13Mode("disabled")
                                    .regulationOverrideCmaMode("disabled")
                                    .blueprintProgressiveJackpotFeedUrl("https://sapirgsuat.blueprintgaming.com/iforium/SAPI.asmx/Progressive")
                                    .build();
    }

    private static IforiumProviderConfig validProviderConfigWithDisableRegulations() {
        return IforiumProviderConfig.builder()
                                    .startGameUrl("baseurl.com")
                                    .casinoId("S0009")
                                    .lobbyUrl("https://www.operator.com/lobby")
                                    .listGameUrl("")
                                    .regulationsEnabled(false)
                                    .regulationSessionDuration(0)
                                    .regulationInterval(86400)
                                    .regulationGameHistoryUrl("https://www.operator.com/history")
                                    .regulationBonusUrl("https://www.operator.com/bonud")
                                    .regulationOverrideRts13Mode("disabled")
                                    .regulationOverrideCmaMode("disabled")
                                    .blueprintProgressiveJackpotFeedUrl("https://sapirgsuat.blueprintgaming.com/iforium/SAPI.asmx/Progressive")
                                    .build();
    }

    private static IforiumProviderConfig listGameUrlProviderConfig(String gameUrl) {
        return IforiumProviderConfig.builder()
                                    .listGameUrl(gameUrl)
                                    .build();
    }
}
