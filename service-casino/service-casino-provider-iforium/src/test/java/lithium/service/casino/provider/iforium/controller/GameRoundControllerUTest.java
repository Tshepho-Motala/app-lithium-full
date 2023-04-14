package lithium.service.casino.provider.iforium.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.EBalanceAdjustmentResponseStatus;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.handler.MainExceptionHandler;
import lithium.service.casino.provider.iforium.model.request.AwardWinningsRequest;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.request.RollBackBetRequest;
import lithium.service.casino.provider.iforium.model.request.VoidBetRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.service.GameRoundService;
import lithium.service.casino.provider.iforium.service.impl.GameRoundServiceImpl;
import lithium.service.casino.provider.iforium.util.Fixtures;
import lithium.service.casino.provider.iforium.util.LithiumClientUtils;
import lithium.service.casino.provider.iforium.util.TestGameRoundUtils;
import lithium.service.casino.provider.iforium.util.TestMockUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.objects.Game;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.reward.client.QueryRewardClientService;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static lithium.service.casino.provider.iforium.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.INVALID_LONG_PARAMETER;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SHADOW_DEPOSIT_TRANSACTION_TYPE_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.util.Fixtures.fixture;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.setUpGameResponseMockByGuidAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.setUpSystemLoginEventsClientMock;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class GameRoundControllerUTest {

    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    private static final BigDecimal AMOUNT_ZERO_POUND = BigDecimal.ZERO.setScale(2, RoundingMode.FLOOR);
    private static final BigDecimal AMOUNT_ONE_POUND = BigDecimal.ONE.setScale(2, RoundingMode.FLOOR);
    private static final BigDecimal AMOUNT_TEN_POUNDS = BigDecimal.TEN.setScale(2, RoundingMode.FLOOR);

    private MockMvc mockMvc;

    @Mock
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @Mock
    private CachingDomainClientService cachingDomainClientService;

    @Mock
    private CasinoClientService casinoClientService;

    @Mock
    private ModuleInfo moduleInfo;

    @Mock
    private QueryRewardClientService queryRewardClientService;

    @BeforeEach
    void setUp() {
        GameRoundService gameRoundService = new GameRoundServiceImpl(new LithiumClientUtils(lithiumServiceClientFactory),
                                                                     cachingDomainClientService, casinoClientService, moduleInfo, queryRewardClientService);
        GameRoundController gameRoundController = new GameRoundController(gameRoundService, SECURITY_CONFIG_UTILS);

        this.mockMvc = MockMvcBuilders.standaloneSetup(gameRoundController)
                                      .setControllerAdvice(new MainExceptionHandler())
                                      .addPlaceholderValue(TestConstants.PLACE_BET_API_PATH_PROPERTY_NAME, TestConstants.PLACE_BET_API_PATH)
                                      .addPlaceholderValue(TestConstants.END_API_PATH_PROPERTY_NAME, TestConstants.END_API_PATH)
                                      .addPlaceholderValue(TestConstants.AWARD_WINNINGS_API_PATH_PROPERTY_NAME,
                                                           TestConstants.AWARD_WINNINGS_API_PATH)
                                      .addPlaceholderValue(TestConstants.ROLL_BACK_BET_API_PATH_PROPERTY_NAME,
                                                           TestConstants.ROLL_BACK_BET_API_PATH)
                                      .addPlaceholderValue(TestConstants.VOID_BET_API_PATH_PROPERTY_NAME, TestConstants.VOID_BET_API_PATH)
                                      .addPlaceholderValue(TestConstants.CREDIT_API_PATH_PROPERTY_NAME, TestConstants.CREDIT_API_PATH)
                                      .build();
    }

    @Test
    void placeBet_SuccessResponse_WhenRequestLoginEventLogoutIsNull() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_PLACE_BET_RESPONSE_FIXTURE_PATH, AMOUNT_TEN_POUNDS)));
    }

    @Test
    void placeBet_SuccessResponse_WhenRequestLoginEventLogoutIsAfterCurrentTime() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(new Date().getTime() + 60000));
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_PLACE_BET_RESPONSE_FIXTURE_PATH, AMOUNT_TEN_POUNDS)));
    }

    @Test
    void placeBet_SuccessResponse_WhenAmountIsNull() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);
        placeBetRequest.setAmount(AMOUNT_ZERO_POUND);
        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(placeBetRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_PLACE_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void placeBet_SuccessResponse_WhenRequestContainsOptionalParameters() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_PLACE_BET_RESPONSE_FIXTURE_PATH, AMOUNT_TEN_POUNDS)));
    }

    @Test
    void placeBet_ReturnsSessionNotFoundResponse_WhenTokenIsExpired() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.SESSION_NOT_FOUND.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnsSessionNotFoundResponse_WhenAccountIdDoesNotMatchLoginEvent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn("otherOperatorAccountId");

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.SESSION_NOT_FOUND.getCode())));
    }

    @Test
    void placeBet_ReturnsCurrencyMismatchResponse_WhenCurrencyDoesNotMatchDomainCurrency() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.USD_CURRENCY);
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.CURRENCY_MISMATCH.getCode(), TestConstants.USD_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnsUnknownErrorResponse_WhenGetBalanceReturnStatus500UnhandledCasinoClientException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.USD_CURRENCY);
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenThrow(Status500UnhandledCasinoClientException.class);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    void placeBet_ReturnSessionNotFoundResponse_WhenLoginEventDoesNotExist() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        SystemLoginEventsClient systemLoginEventsClientMock = TestMockUtils.setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.SESSION_NOT_FOUND.getCode())));
    }

    @Test
    void placeBet_ReturnsApiAuthFailedResponse_WhenGameResponseIsNotSuccessful() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Response<Game> game = setUpGameResponseMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.isSuccessful()).thenReturn(false);
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.GAME_NOT_FOUND.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnsApiAuthFailedResponse_WhenGameResponseDataIsNull() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Response<Game> game = setUpGameResponseMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.isSuccessful()).thenReturn(true);
        Mockito.when(game.getData()).thenReturn(null);
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.GAME_NOT_FOUND.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnsInsufficientFundsResponse_WhenBalanceAdjustmentResponseResultIsInsufficientFunds() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.INSUFFICIENT_FUNDS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.INSUFFICIENT_FUNDS.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnsInsufficientFundsResponse_WhenBalanceAdjustmentResponseResultIsInsufficientFundsAndAmountIsNull() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.INSUFFICIENT_FUNDS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(null);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.INSUFFICIENT_FUNDS.getCode())));
    }

    @Test
    void placeBet_ReturnsUnknownResponse_WhenBalanceAdjustmentResponseResultIsInternalServerError() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.INTERNAL_ERROR);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    void placeBet_ReturnAccountNotFoundResponse_WhenDomainIsNotValid() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenThrow(Status550ServiceDomainClientException.class);

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    @Test
    void placeBet_ReturnLossLimitResponse_WhenMultiBetReturnWeeklyLossLimitException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);
        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status484WeeklyLossLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.LOSS_LIMIT.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnLossLimitResponse_WhenMultiBetReturnDailyLossLimitException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status492DailyLossLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.LOSS_LIMIT.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnSessionLimitResponse_WhenMultiBetReturnMonthlyLossLimitException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status493MonthlyLossLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.LOSS_LIMIT.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnSessionLimitResponse_WhenMultiBetReturnTimeSlotLimitException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status478TimeSlotLimitException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.SESSION_LIMIT.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnLossLimitResponse_WhenMultiBetReturnPlayTimeLimitException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status438PlayTimeLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.SESSION_LIMIT.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void placeBet_ReturnUnknownErrorResponse_WhenMultiBetReturnDailyWinLimitReachedException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status494DailyWinLimitReachedException(""));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    void placeBet_ReturnUnknownErrorResponse_WhenMultiBetReturnMonthlyWinLimitReachedException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status495MonthlyWinLimitReachedException(""));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @Test
    void placeBet_ReturnUnknownErrorResponse_WhenMultiBetReturnWeeklyWinLimitReachedException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status485WeeklyWinLimitReachedException(""));

        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.PLACE_BET_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @ParameterizedTest
    @MethodSource("placeBetNotWellFormedRequestBodies")
    void placeBet_ReturnApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(PlaceBetRequest placeBetRequest) throws Exception {
        mockMvc.perform(post(TestConstants.PLACE_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(placeBetRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    void end_SuccessResponse() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    void end_SuccessResponse_WithOptionalParameters() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    void end_SuccessResponse_WhenGatewaySessionTokenIsExpired() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));

        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    void end_SuccessResponse_WhenRequestLoginEventLogoutIsAfterCurrentTime() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(new Date().getTime() + 60000));
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    void end_SuccessResponse_WhenGatewaySessionTokenIsEmpty() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(getAndUpdateEndRequest(r -> r.setGatewaySessionToken("")))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    void end_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(getAndUpdateEndRequest(r -> r.setGatewaySessionToken(null)))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @Test
    void end_SuccessResponse_WhenLoginEventDoesNotExist() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        SystemLoginEventsClient systemLoginEventsClientMock = TestMockUtils.setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.GBP_CURRENCY);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH, "100.00")));
    }

    @SneakyThrows
    @Test
    void end_ReturnsGameRoundNotFoundException_WhenGameRoundDoesNotExist() {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doThrow(Status474BetRoundNotFoundException.class).when(casinoClientService).completeBetRound(any(), any(), any());
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any())).thenReturn(validCasinoClientBalanceResponse("100"));

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH,
                                                 ErrorCodes.GAME_ROUND_NOT_FOUND.getCode(), TestConstants.GBP_CURRENCY,
                                                 AMOUNT_ONE_POUND)));
    }

    @Test
    void end_ReturnsUnknownResponse_WhenCompleteBetRoundResultIsInternalServerError() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));

        Mockito.doThrow(Status500UnhandledCasinoClientException.class).when(casinoClientService).completeBetRound(any(), any(), any());

        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(Fixtures.fixture(TestConstants.END_REQUEST_FIXTURE_PATH)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.UNKNOWN_ERROR.getCode())));
    }

    @ParameterizedTest
    @MethodSource("endNotWellFormedRequestBodies")
    void end_ReturnApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(EndRequest endRequest) throws Exception {
        mockMvc.perform(post(TestConstants.END_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(endRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenSessionIsValid() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenSessionIsExpired() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(
                                        getAndUpdateAwardWinningsRequest(r -> r.setGatewaySessionToken(null)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenGatewaySessionTokenIsEmpty() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(
                                        getAndUpdateAwardWinningsRequest(r -> r.setGatewaySessionToken("")))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenFindBySessionKeyThrowsStatus412LoginEventNotFoundException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString((TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZero() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(
                                        getAndUpdateAwardWinningsRequest(r -> r.setAmount(AMOUNT_ZERO_POUND)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZeroAndEndRoundIsTrue() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(awardWinningsRequest)))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZeroAndEndRoundIsFalse() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(false);
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(awardWinningsRequest)))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZeroAndEndRoundIsTrueAndGatewaySessionTokenIsAbsent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setGatewaySessionToken(null);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(awardWinningsRequest)))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenSessionIsValid() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS))))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenSessionIsExpired() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS))))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(
                                getAndUpdateJackPotAwardWinningsRequest(r -> r.setGatewaySessionToken(null)))))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenGatewaySessionTokenIsEmpty() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(
                                getAndUpdateJackPotAwardWinningsRequest(r -> r.setGatewaySessionToken("")))))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenFindBySessionKeyThrowsStatus412LoginEventNotFoundException() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
                .thenThrow(Status412LoginEventNotFoundException.class);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString((TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS)))))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZero() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(
                                getAndUpdateJackPotAwardWinningsRequest(r -> r.setJackpotWinnings(AMOUNT_ZERO_POUND)))))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZeroAndEndRoundIsTrue() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(awardWinningsRequest)))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZeroAndEndRoundIsFalse() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(false);
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(awardWinningsRequest)))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZeroAndEndRoundIsTrueAndGatewaySessionTokenIsAbsent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setGatewaySessionToken(null);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                        .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                        .content(objectMapper.writeValueAsString(awardWinningsRequest)))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @ParameterizedTest
    @MethodSource("awardWinningsNotWellFormedRequestBodies")
    void awardWinnings_ReturnApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(
            AwardWinningsRequest awardWinningsRequest) throws Exception {
        mockMvc.perform(post(TestConstants.AWARD_WINNINGS_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(awardWinningsRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsValid() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform((post(TestConstants.ROLL_BACK_BET_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(TestGameRoundUtils.validRollBackBetRequest(AMOUNT_ONE_POUND)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(fixture(TestConstants.SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsExpired() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform((post(TestConstants.ROLL_BACK_BET_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(TestGameRoundUtils.validRollBackBetRequest(AMOUNT_ONE_POUND)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(fixture(TestConstants.SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsAbsent() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform((post(TestConstants.ROLL_BACK_BET_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(getAndUpdateRollBackBetRequest(r -> r.setGatewaySessionToken(null))))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(fixture(TestConstants.SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsEmpty() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform((post(TestConstants.ROLL_BACK_BET_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(getAndUpdateRollBackBetRequest(r -> r.setGatewaySessionToken(""))))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(fixture(TestConstants.SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenFindBySessionKeyThrowsStatus412LoginEventNotFoundException() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        mockMvc.perform(post(TestConstants.ROLL_BACK_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString((TestGameRoundUtils.validRollBackBetRequest(AMOUNT_ONE_POUND)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(
                               fixture(TestConstants.SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ZERO_POUND)));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenEndRoundIsTrue() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.ROLL_BACK_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(getAndUpdateRollBackBetRequest(r -> r.setEndRound(true)))))
               .andExpect(status().isOk())
               .andExpect(
                       content().json(fixture(TestConstants.SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @ParameterizedTest
    @MethodSource("rollBackBetNotWellFormedRequestBodies")
    void rollBackBet_ReturnApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(
            RollBackBetRequest rollBackBetRequest) throws Exception {
        mockMvc.perform(post(TestConstants.ROLL_BACK_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(rollBackBetRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    void rollBackBet_ReturnTransactionNotFoundResponse_WhenBalanceAdjustmentResponseWithStatusTransactionDataValidationError() throws Exception {

        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.TRANSACTION_DATA_VALIDATION_ERROR);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform((post(TestConstants.ROLL_BACK_BET_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(TestGameRoundUtils.validRollBackBetRequest(AMOUNT_ONE_POUND)))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.TRANSACTION_NOT_FOUND.getCode())));
    }

    @Test
    @SneakyThrows
    void voidBet_SuccessResponse() {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    @SneakyThrows
    void voidBet_SuccessResponse_WhenRequestContainsOptionalParameters() {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void voidBet_SuccessResponse_WhenSessionIsExpired() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void voidBet_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        VoidBetRequest request = TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS);
        request.setGatewaySessionToken(null);

        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void voidBet_SuccessResponse_WhenGatewaySessionTokenIsEmpty() throws Exception {
        VoidBetRequest request = TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS);
        request.setGatewaySessionToken("");

        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void voidBet_ReturnAccountNotFoundResponse_WhenDomainIsNotValid() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenThrow(Status550ServiceDomainClientException.class);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    @Test
    void voidBet_ReturnsSessionNotFoundResponse_WhenAccountIdDoesNotMatchLoginEvent() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn("otherOperatorAccountId");

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));
    }

    @Test
    void voidBet_ReturnsSessionNotFoundResponse_WhenGameIdDoesNotExist() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Response<Game> gameResponse = setUpGameResponseMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(gameResponse.isSuccessful()).thenReturn(false);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS))))
               .andExpect(status().isOk())
               .andExpect(content().json(
                       fixture(TestConstants.FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH, ErrorCodes.GAME_NOT_FOUND.getCode(),
                               TestConstants.GBP_CURRENCY, AMOUNT_ONE_POUND)));
    }

    @ParameterizedTest
    @MethodSource("voidBetNotWellFormedRequestBodies")
    void voidBet_ReturnApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(VoidBetRequest voidBetRequest) throws Exception {
        mockMvc.perform(post(TestConstants.VOID_BET_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(voidBetRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH,
                                                 ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @Test
    void credit_SuccessResponse() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        mockMvc.perform((post(TestConstants.CREDIT_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS)))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_CREDIT_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void credit_SuccessResponse_WhenAmountIsZero() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        mockMvc.perform((post(TestConstants.CREDIT_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(getAndUpdateCreditRequest(r -> r.setAmount(AMOUNT_ZERO_POUND))))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.SUCCESS_CREDIT_RESPONSE_FIXTURE_PATH, AMOUNT_ONE_POUND)));
    }

    @Test
    void credit_ReturnAccountNotFoundResponse_WhenDomainIsNotValid() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenThrow(Status550ServiceDomainClientException.class);

        mockMvc.perform((post(TestConstants.CREDIT_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS)))))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.ACCOUNT_NOT_FOUND.getCode())));

    }

    @Test
    void credit_ReturnApiAuthenticationFailedResponse_WhenAccountTransactionTypeIdIs811() throws Exception {
        TestMockUtils.mockSuccessIforiumConfig();

        CreditRequest creditRequest = TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS);
        creditRequest.setAccountTransactionTypeId(SHADOW_DEPOSIT_TRANSACTION_TYPE_ID);

        mockMvc.perform((post(TestConstants.CREDIT_API_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                       .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                       .content(objectMapper.writeValueAsString(creditRequest))))
               .andExpect(content().json(
                       fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    @ParameterizedTest
    @MethodSource("creditNotWellFormedRequestBodies")
    void credit_ReturnApiAuthenticationFailedResponse_WhenRequestIsNotWellFormed(CreditRequest creditRequest) throws Exception {
        mockMvc.perform(post(TestConstants.CREDIT_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(creditRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(
                       fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.API_AUTHENTICATION_FAILED.getCode())));
    }

    private static Stream<Arguments> placeBetNotWellFormedRequestBodies() {
        PlaceBetRequest nullPlatformKey = getAndUpdatePlaceBetRequest(r -> r.setPlatformKey(null));
        PlaceBetRequest nullSequence = getAndUpdatePlaceBetRequest(r -> r.setSequence(null));
        PlaceBetRequest nullTimestamp = getAndUpdatePlaceBetRequest(r -> r.setTimestamp(null));
        PlaceBetRequest nullGatewaySessionToken = getAndUpdatePlaceBetRequest(r -> r.setGatewaySessionToken(null));
        PlaceBetRequest nullOperatorAccountId = getAndUpdatePlaceBetRequest(r -> r.setOperatorAccountId(null));
        PlaceBetRequest nullGameRoundId = getAndUpdatePlaceBetRequest(r -> r.setGameRoundId(null));
        PlaceBetRequest nullGameRoundTransactionId = getAndUpdatePlaceBetRequest(r -> r.setGameRoundTransactionId(null));
        PlaceBetRequest nullGameId = getAndUpdatePlaceBetRequest(r -> r.setGameId(null));
        PlaceBetRequest nullCurrencyCode = getAndUpdatePlaceBetRequest(r -> r.setCurrencyCode(null));
        PlaceBetRequest nullAmount = getAndUpdatePlaceBetRequest(r -> r.setAmount(null));
        PlaceBetRequest nullStartRound = getAndUpdatePlaceBetRequest(r -> r.setStartRound(null));
        PlaceBetRequest nullEndRound = getAndUpdatePlaceBetRequest(r -> r.setEndRound(null));

        PlaceBetRequest emptyPlatformKey = getAndUpdatePlaceBetRequest(r -> r.setPlatformKey(""));
        PlaceBetRequest emptySequence = getAndUpdatePlaceBetRequest(r -> r.setSequence(""));
        PlaceBetRequest emptyGatewaySessionToken = getAndUpdatePlaceBetRequest(r -> r.setGatewaySessionToken(""));
        PlaceBetRequest emptyOperatorAccountId = getAndUpdatePlaceBetRequest(r -> r.setOperatorAccountId(""));
        PlaceBetRequest emptyGameRoundId = getAndUpdatePlaceBetRequest(r -> r.setGameRoundId(""));
        PlaceBetRequest emptyGameRoundTransactionId = getAndUpdatePlaceBetRequest(r -> r.setGameRoundTransactionId(""));
        PlaceBetRequest emptyGameId = getAndUpdatePlaceBetRequest(r -> r.setGameId(""));
        PlaceBetRequest emptyCurrencyCode = getAndUpdatePlaceBetRequest(r -> r.setCurrencyCode(""));

        PlaceBetRequest tooLongPlatformKey = getAndUpdatePlaceBetRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongSequence = getAndUpdatePlaceBetRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongGatewaySessionToken = getAndUpdatePlaceBetRequest(r -> r.setGatewaySessionToken(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongOperatorAccountId = getAndUpdatePlaceBetRequest(r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongGameRoundId = getAndUpdatePlaceBetRequest(r -> r.setGameRoundId(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongGameRoundTransactionId = getAndUpdatePlaceBetRequest(
                r -> r.setGameRoundTransactionId(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongGameId = getAndUpdatePlaceBetRequest(r -> r.setGameId(INVALID_LONG_PARAMETER));
        PlaceBetRequest tooLongCurrencyCode = getAndUpdatePlaceBetRequest(r -> r.setCurrencyCode(INVALID_LONG_PARAMETER));

        PlaceBetRequest negativeAmount = getAndUpdatePlaceBetRequest(r -> r.setAmount(new BigDecimal("-1.00")));
        PlaceBetRequest treeFractionPointsAmount1 = getAndUpdatePlaceBetRequest(r -> r.setAmount(new BigDecimal("1.000")));
        PlaceBetRequest treeFractionPointsAmount2 = getAndUpdatePlaceBetRequest(r -> r.setAmount(new BigDecimal("1.001")));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullGatewaySessionToken),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullGameRoundId),
                Arguments.of(nullGameRoundTransactionId),
                Arguments.of(nullGameId),
                Arguments.of(nullCurrencyCode),
                Arguments.of(nullAmount),
                Arguments.of(nullStartRound),
                Arguments.of(nullEndRound),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyGatewaySessionToken),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyGameRoundId),
                Arguments.of(emptyGameRoundTransactionId),
                Arguments.of(emptyGameId),
                Arguments.of(emptyCurrencyCode),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongGatewaySessionToken),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongGameRoundId),
                Arguments.of(tooLongGameRoundTransactionId),
                Arguments.of(tooLongGameId),
                Arguments.of(tooLongCurrencyCode),

                Arguments.of(negativeAmount),
                Arguments.of(treeFractionPointsAmount1),
                Arguments.of(treeFractionPointsAmount2)
        );
    }

    private static Stream<Arguments> endNotWellFormedRequestBodies() {
        EndRequest nullPlatformKey = getAndUpdateEndRequest(r -> r.setPlatformKey(null));
        EndRequest nullSequence = getAndUpdateEndRequest(r -> r.setSequence(null));
        EndRequest nullTimestamp = getAndUpdateEndRequest(r -> r.setTimestamp(null));
        EndRequest nullOperatorAccountId = getAndUpdateEndRequest(r -> r.setOperatorAccountId(null));
        EndRequest nullGameRoundId = getAndUpdateEndRequest(r -> r.setGameRoundId(null));
        EndRequest nullGameId = getAndUpdateEndRequest(r -> r.setGameId(null));
        EndRequest nullCurrencyCode = getAndUpdateEndRequest(r -> r.setCurrencyCode(null));

        EndRequest emptyPlatformKey = getAndUpdateEndRequest(r -> r.setPlatformKey(""));
        EndRequest emptySequence = getAndUpdateEndRequest(r -> r.setSequence(""));
        EndRequest emptyOperatorAccountId = getAndUpdateEndRequest(r -> r.setOperatorAccountId(""));
        EndRequest emptyGameRoundId = getAndUpdateEndRequest(r -> r.setGameRoundId(""));
        EndRequest emptyGameId = getAndUpdateEndRequest(r -> r.setGameId(""));
        EndRequest emptyCurrencyCode = getAndUpdateEndRequest(r -> r.setCurrencyCode(""));

        EndRequest tooLongPlatformKey = getAndUpdateEndRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        EndRequest tooLongSequence = getAndUpdateEndRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        EndRequest tooLongGatewaySessionToken = getAndUpdateEndRequest(r -> r.setGatewaySessionToken(INVALID_LONG_PARAMETER));
        EndRequest tooLongOperatorAccountId = getAndUpdateEndRequest(r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        EndRequest tooLongGameRoundId = getAndUpdateEndRequest(r -> r.setGameRoundId(INVALID_LONG_PARAMETER));
        EndRequest tooLongGameId = getAndUpdateEndRequest(r -> r.setGameId(INVALID_LONG_PARAMETER));
        EndRequest tooLongContentGameProviderId = getAndUpdateEndRequest(r -> r.setContentGameProviderId(INVALID_LONG_PARAMETER));
        EndRequest tooLongCurrencyCode = getAndUpdateEndRequest(r -> r.setCurrencyCode(INVALID_LONG_PARAMETER));
        EndRequest tooLongJackpotContribution = getAndUpdateEndRequest(r -> r.setJackpotContribution(new BigDecimal("0.01234567890")));
        EndRequest tooLongJackpotWinnings = getAndUpdateEndRequest(r -> r.setJackpotWinnings(new BigDecimal("0.01234567890")));
        EndRequest tooLongFreeGameOfferCode = getAndUpdateEndRequest(r -> r.setFreeGameOfferCode(INVALID_LONG_PARAMETER));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullGameRoundId),
                Arguments.of(nullGameId),
                Arguments.of(nullCurrencyCode),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyGameRoundId),
                Arguments.of(emptyGameId),
                Arguments.of(emptyCurrencyCode),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongGatewaySessionToken),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongGameRoundId),
                Arguments.of(tooLongContentGameProviderId),
// TODO GameFlex documentation states that jackpot contribution will have a maximum precision of 10 decimal places, however, we’ve seen on the staging environment that they sent more than 10 decimal places, and that breaks the validation we have in lithium. We had to remove the validation and comment out a few test cases.
//                Arguments.of(tooLongJackpotContribution),
                Arguments.of(tooLongJackpotWinnings),
                Arguments.of(tooLongFreeGameOfferCode),
                Arguments.of(tooLongGameId),
                Arguments.of(tooLongCurrencyCode)
        );
    }

    private static Stream<Arguments> awardWinningsNotWellFormedRequestBodies() {
        AwardWinningsRequest nullPlatformKey = getAndUpdateAwardWinningsRequest(r -> r.setPlatformKey(null));
        AwardWinningsRequest nullSequence = getAndUpdateAwardWinningsRequest(r -> r.setSequence(null));
        AwardWinningsRequest nullTimestamp = getAndUpdateAwardWinningsRequest(r -> r.setTimestamp(null));
        AwardWinningsRequest nullOperatorAccountId = getAndUpdateAwardWinningsRequest(r -> r.setOperatorAccountId(null));
        AwardWinningsRequest nullGameRoundId = getAndUpdateAwardWinningsRequest(r -> r.setGameRoundId(null));
        AwardWinningsRequest nullGameRoundTransactionId = getAndUpdateAwardWinningsRequest(r -> r.setGameRoundTransactionId(null));
        AwardWinningsRequest nullGameId = getAndUpdateAwardWinningsRequest(r -> r.setGameId(null));
        AwardWinningsRequest nullCurrencyCode = getAndUpdateAwardWinningsRequest(r -> r.setCurrencyCode(null));
        AwardWinningsRequest nullAmount = getAndUpdateAwardWinningsRequest(r -> r.setAmount(null));
        AwardWinningsRequest nullStartRound = getAndUpdateAwardWinningsRequest(r -> r.setStartRound(null));
        AwardWinningsRequest nullEndRound = getAndUpdateAwardWinningsRequest(r -> r.setEndRound(null));

        AwardWinningsRequest emptyPlatformKey = getAndUpdateAwardWinningsRequest(r -> r.setPlatformKey(""));
        AwardWinningsRequest emptySequence = getAndUpdateAwardWinningsRequest(r -> r.setSequence(""));
        AwardWinningsRequest emptyOperatorAccountId = getAndUpdateAwardWinningsRequest(r -> r.setOperatorAccountId(""));
        AwardWinningsRequest emptyGameRoundId = getAndUpdateAwardWinningsRequest(r -> r.setGameRoundId(""));
        AwardWinningsRequest emptyGameRoundTransactionId = getAndUpdateAwardWinningsRequest(r -> r.setGameRoundTransactionId(""));
        AwardWinningsRequest emptyGameId = getAndUpdateAwardWinningsRequest(r -> r.setGameId(""));
        AwardWinningsRequest emptyCurrencyCode = getAndUpdateAwardWinningsRequest(r -> r.setCurrencyCode(""));

        AwardWinningsRequest tooLongPlatformKey = getAndUpdateAwardWinningsRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongSequence = getAndUpdateAwardWinningsRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongGatewaySessionToken = getAndUpdateAwardWinningsRequest(
                r -> r.setGatewaySessionToken(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongOperatorAccountId = getAndUpdateAwardWinningsRequest(
                r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongGameRoundId = getAndUpdateAwardWinningsRequest(r -> r.setGameRoundId(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongGameRoundTransactionId = getAndUpdateAwardWinningsRequest(
                r -> r.setGameRoundTransactionId(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongGameId = getAndUpdateAwardWinningsRequest(r -> r.setGameId(INVALID_LONG_PARAMETER));
        AwardWinningsRequest tooLongCurrencyCode = getAndUpdateAwardWinningsRequest(r -> r.setCurrencyCode(INVALID_LONG_PARAMETER));

        AwardWinningsRequest negativeAmount = getAndUpdateAwardWinningsRequest(r -> r.setAmount(new BigDecimal("-1.00")));
        AwardWinningsRequest treeFractionPointsAmount1 = getAndUpdateAwardWinningsRequest(r -> r.setAmount(new BigDecimal("1.000")));
        AwardWinningsRequest treeFractionPointsAmount2 = getAndUpdateAwardWinningsRequest(r -> r.setAmount(new BigDecimal("1.001")));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullGameRoundId),
                Arguments.of(nullGameRoundTransactionId),
                Arguments.of(nullGameId),
                Arguments.of(nullCurrencyCode),
                Arguments.of(nullAmount),
                Arguments.of(nullStartRound),
                Arguments.of(nullEndRound),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyGameRoundId),
                Arguments.of(emptyGameRoundTransactionId),
                Arguments.of(emptyGameId),
                Arguments.of(emptyCurrencyCode),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongGatewaySessionToken),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongGameRoundId),
                Arguments.of(tooLongGameRoundTransactionId),
                Arguments.of(tooLongGameId),
                Arguments.of(tooLongCurrencyCode),

                Arguments.of(negativeAmount),
                Arguments.of(treeFractionPointsAmount1),
                Arguments.of(treeFractionPointsAmount2)
        );
    }

    private static Stream<Arguments> rollBackBetNotWellFormedRequestBodies() {

        RollBackBetRequest nullPlatformKey = getAndUpdateRollBackBetRequest(r -> r.setPlatformKey(null));
        RollBackBetRequest nullSequence = getAndUpdateRollBackBetRequest(r -> r.setSequence(null));
        RollBackBetRequest nullTimestamp = getAndUpdateRollBackBetRequest(r -> r.setTimestamp(null));
        RollBackBetRequest nullOperatorAccountId = getAndUpdateRollBackBetRequest(r -> r.setOperatorAccountId(null));
        RollBackBetRequest nullGameRoundId = getAndUpdateRollBackBetRequest(r -> r.setGameRoundId(null));
        RollBackBetRequest nullOriginalBetGameRoundTransactionId = getAndUpdateRollBackBetRequest(
                r -> r.setOriginalBetGameRoundTransactionId(null));
        RollBackBetRequest nullGameRoundTransactionId = getAndUpdateRollBackBetRequest(r -> r.setGameRoundTransactionId(null));
        RollBackBetRequest nullGameId = getAndUpdateRollBackBetRequest(r -> r.setGameId(null));
        RollBackBetRequest nullCurrencyCode = getAndUpdateRollBackBetRequest(r -> r.setCurrencyCode(null));
        RollBackBetRequest nullAmount = getAndUpdateRollBackBetRequest(r -> r.setAmount(null));
        RollBackBetRequest nullEndRound = getAndUpdateRollBackBetRequest(r -> r.setEndRound(null));

        RollBackBetRequest emptyPlatformKey = getAndUpdateRollBackBetRequest(r -> r.setPlatformKey(""));
        RollBackBetRequest emptySequence = getAndUpdateRollBackBetRequest(r -> r.setSequence(""));
        RollBackBetRequest emptyOperatorAccountId = getAndUpdateRollBackBetRequest(r -> r.setOperatorAccountId(""));
        RollBackBetRequest emptyGameRoundId = getAndUpdateRollBackBetRequest(r -> r.setGameRoundId(""));
        RollBackBetRequest emptyOriginalPlaceBetGameRoundTransactionId = getAndUpdateRollBackBetRequest(
                r -> r.setOriginalBetGameRoundTransactionId(""));
        RollBackBetRequest emptyGameRoundTransactionId = getAndUpdateRollBackBetRequest(r -> r.setGameRoundTransactionId(""));
        RollBackBetRequest emptyGameId = getAndUpdateRollBackBetRequest(r -> r.setGameId(""));
        RollBackBetRequest emptyCurrencyCode = getAndUpdateRollBackBetRequest(r -> r.setCurrencyCode(""));

        RollBackBetRequest tooLongPlatformKey = getAndUpdateRollBackBetRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongSequence = getAndUpdateRollBackBetRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongGatewaySessionToken = getAndUpdateRollBackBetRequest(
                r -> r.setGatewaySessionToken(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongOperatorAccountId = getAndUpdateRollBackBetRequest(r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongGameRoundId = getAndUpdateRollBackBetRequest(r -> r.setGameRoundId(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongOriginalPlaceBetGameRoundTransactionId = getAndUpdateRollBackBetRequest(
                r -> r.setOriginalBetGameRoundTransactionId(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongGameRoundTransactionId = getAndUpdateRollBackBetRequest(
                r -> r.setGameRoundTransactionId(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongGameId = getAndUpdateRollBackBetRequest(r -> r.setGameId(INVALID_LONG_PARAMETER));
        RollBackBetRequest tooLongCurrencyCode = getAndUpdateRollBackBetRequest(r -> r.setCurrencyCode(INVALID_LONG_PARAMETER));

        RollBackBetRequest negativeAmount = getAndUpdateRollBackBetRequest(r -> r.setAmount(new BigDecimal("-1.00")));
        RollBackBetRequest treeFractionPointsAmount1 = getAndUpdateRollBackBetRequest(r -> r.setAmount(new BigDecimal("1.000")));
        RollBackBetRequest treeFractionPointsAmount2 = getAndUpdateRollBackBetRequest(r -> r.setAmount(new BigDecimal("1.001")));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullGameRoundId),
                Arguments.of(nullOriginalBetGameRoundTransactionId),
                Arguments.of(nullGameRoundTransactionId),
                Arguments.of(nullGameId),
                Arguments.of(nullCurrencyCode),
                Arguments.of(nullAmount),
                Arguments.of(nullEndRound),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyGameRoundId),
                Arguments.of(emptyOriginalPlaceBetGameRoundTransactionId),
                Arguments.of(emptyGameRoundTransactionId),
                Arguments.of(emptyGameId),
                Arguments.of(emptyCurrencyCode),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongGatewaySessionToken),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongGameRoundId),
                Arguments.of(tooLongOriginalPlaceBetGameRoundTransactionId),
                Arguments.of(tooLongGameRoundTransactionId),
                Arguments.of(tooLongGameId),
                Arguments.of(tooLongCurrencyCode),

                Arguments.of(negativeAmount),
                Arguments.of(treeFractionPointsAmount1),
                Arguments.of(treeFractionPointsAmount2)
        );
    }

    private static Stream<Arguments> voidBetNotWellFormedRequestBodies() {
        VoidBetRequest nullPlatformKey = getAndUpdateVoidBetRequest(r -> r.setPlatformKey(null));
        VoidBetRequest nullSequence = getAndUpdateVoidBetRequest(r -> r.setSequence(null));
        VoidBetRequest nullTimestamp = getAndUpdateVoidBetRequest(r -> r.setTimestamp(null));
        VoidBetRequest nullOperatorAccountId = getAndUpdateVoidBetRequest(r -> r.setOperatorAccountId(null));
        VoidBetRequest nullGameRoundId = getAndUpdateVoidBetRequest(r -> r.setGameRoundId(null));
        VoidBetRequest nullGameRoundTransactionId = getAndUpdateVoidBetRequest(r -> r.setGameRoundTransactionId(null));
        VoidBetRequest nullGameId = getAndUpdateVoidBetRequest(r -> r.setGameId(null));
        VoidBetRequest nullCurrencyCode = getAndUpdateVoidBetRequest(r -> r.setCurrencyCode(null));
        VoidBetRequest nullAmount = getAndUpdateVoidBetRequest(r -> r.setAmount(null));
        VoidBetRequest nullEndRound = getAndUpdateVoidBetRequest(r -> r.setEndRound(null));

        VoidBetRequest emptyPlatformKey = getAndUpdateVoidBetRequest(r -> r.setPlatformKey(""));
        VoidBetRequest emptySequence = getAndUpdateVoidBetRequest(r -> r.setSequence(""));
        VoidBetRequest emptyOperatorAccountId = getAndUpdateVoidBetRequest(r -> r.setOperatorAccountId(""));
        VoidBetRequest emptyGameRoundId = getAndUpdateVoidBetRequest(r -> r.setGameRoundId(""));
        VoidBetRequest emptyGameRoundTransactionId = getAndUpdateVoidBetRequest(r -> r.setGameRoundTransactionId(""));
        VoidBetRequest emptyGameId = getAndUpdateVoidBetRequest(r -> r.setGameId(""));
        VoidBetRequest emptyCurrencyCode = getAndUpdateVoidBetRequest(r -> r.setCurrencyCode(""));

        VoidBetRequest tooLongPlatformKey = getAndUpdateVoidBetRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongSequence = getAndUpdateVoidBetRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongGatewaySessionToken = getAndUpdateVoidBetRequest(
                r -> r.setGatewaySessionToken(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongOperatorAccountId = getAndUpdateVoidBetRequest(
                r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongGameRoundId = getAndUpdateVoidBetRequest(r -> r.setGameRoundId(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongGameRoundTransactionId = getAndUpdateVoidBetRequest(
                r -> r.setGameRoundTransactionId(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongGameId = getAndUpdateVoidBetRequest(r -> r.setGameId(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongCurrencyCode = getAndUpdateVoidBetRequest(r -> r.setCurrencyCode(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongGameVersion = getAndUpdateVoidBetRequest(r -> r.setGameVersion(INVALID_LONG_PARAMETER));
        VoidBetRequest tooLongTableId = getAndUpdateVoidBetRequest(r -> r.setTableId(INVALID_LONG_PARAMETER));

        VoidBetRequest negativeAmount = getAndUpdateVoidBetRequest(r -> r.setAmount(new BigDecimal("-1.00")));
        VoidBetRequest treeFractionPointsAmount1 = getAndUpdateVoidBetRequest(r -> r.setAmount(new BigDecimal("1.000")));
        VoidBetRequest treeFractionPointsAmount2 = getAndUpdateVoidBetRequest(r -> r.setAmount(new BigDecimal("1.001")));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullGameRoundId),
                Arguments.of(nullGameRoundTransactionId),
                Arguments.of(nullGameId),
                Arguments.of(nullCurrencyCode),
                Arguments.of(nullAmount),
                Arguments.of(nullEndRound),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyGameRoundId),
                Arguments.of(emptyGameRoundTransactionId),
                Arguments.of(emptyGameId),
                Arguments.of(emptyCurrencyCode),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongGatewaySessionToken),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongGameRoundId),
                Arguments.of(tooLongGameRoundTransactionId),
                Arguments.of(tooLongGameId),
                Arguments.of(tooLongCurrencyCode),
                Arguments.of(tooLongGameVersion),
                Arguments.of(tooLongTableId),

                Arguments.of(negativeAmount),
                Arguments.of(treeFractionPointsAmount1),
                Arguments.of(treeFractionPointsAmount2)
        );
    }

    private static Stream<Arguments> creditNotWellFormedRequestBodies() {
        CreditRequest nullPlatformKey = getAndUpdateCreditRequest(r -> r.setPlatformKey(null));
        CreditRequest nullSequence = getAndUpdateCreditRequest(r -> r.setSequence(null));
        CreditRequest nullTimestamp = getAndUpdateCreditRequest(r -> r.setTimestamp(null));
        CreditRequest nullOperatorAccountId = getAndUpdateCreditRequest(r -> r.setOperatorAccountId(null));
        CreditRequest nullAccountTransactionId = getAndUpdateCreditRequest(r -> r.setAccountTransactionId(null));
        CreditRequest nullAccountTransactionTypeId = getAndUpdateCreditRequest(r -> r.setAccountTransactionTypeId(null));
        CreditRequest nullCurrencyCode = getAndUpdateCreditRequest(r -> r.setCurrencyCode(null));
        CreditRequest nullAmount = getAndUpdateCreditRequest(r -> r.setAmount(null));

        CreditRequest emptyPlatformKey = getAndUpdateCreditRequest(r -> r.setPlatformKey(""));
        CreditRequest emptySequence = getAndUpdateCreditRequest(r -> r.setSequence(""));
        CreditRequest emptyOperatorAccountId = getAndUpdateCreditRequest(r -> r.setOperatorAccountId(""));
        CreditRequest emptyAccountTransactionId = getAndUpdateCreditRequest(r -> r.setAccountTransactionId(""));
        CreditRequest emptyAccountTransactionTypeId = getAndUpdateCreditRequest(r -> r.setAccountTransactionTypeId(""));
        CreditRequest emptyCurrencyCode = getAndUpdateCreditRequest(r -> r.setCurrencyCode(""));

        CreditRequest tooLongPlatformKey = getAndUpdateCreditRequest(r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        CreditRequest tooLongSequence = getAndUpdateCreditRequest(r -> r.setSequence(INVALID_LONG_PARAMETER));
        CreditRequest tooLongOperatorAccountId = getAndUpdateCreditRequest(r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        CreditRequest tooLongAccountTransactionId = getAndUpdateCreditRequest(r -> r.setAccountTransactionId(INVALID_LONG_PARAMETER));
        CreditRequest tooLongAccountTransactionTypeId = getAndUpdateCreditRequest(
                r -> r.setAccountTransactionTypeId(INVALID_LONG_PARAMETER));
        CreditRequest tooLongCurrencyCode = getAndUpdateCreditRequest(r -> r.setCurrencyCode(INVALID_LONG_PARAMETER));

        return Stream.of(
                Arguments.of(nullPlatformKey),
                Arguments.of(nullSequence),
                Arguments.of(nullTimestamp),
                Arguments.of(nullOperatorAccountId),
                Arguments.of(nullAccountTransactionId),
                Arguments.of(nullAccountTransactionTypeId),
                Arguments.of(nullCurrencyCode),
                Arguments.of(nullAmount),

                Arguments.of(emptyPlatformKey),
                Arguments.of(emptySequence),
                Arguments.of(emptyOperatorAccountId),
                Arguments.of(emptyAccountTransactionId),
                Arguments.of(emptyAccountTransactionTypeId),
                Arguments.of(emptyCurrencyCode),

                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongAccountTransactionId),
                Arguments.of(tooLongAccountTransactionTypeId),
                Arguments.of(tooLongCurrencyCode)
        );
    }

    private static PlaceBetRequest getAndUpdatePlaceBetRequest(Consumer<PlaceBetRequest> consumer) {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);
        consumer.accept(placeBetRequest);
        return placeBetRequest;
    }

    private static EndRequest getAndUpdateEndRequest(Consumer<EndRequest> consumer) {
        EndRequest endRequest = TestGameRoundUtils.validEndRequest();
        consumer.accept(endRequest);
        return endRequest;
    }

    private static AwardWinningsRequest getAndUpdateAwardWinningsRequest(Consumer<AwardWinningsRequest> consumer) {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        consumer.accept(awardWinningsRequest);
        return awardWinningsRequest;
    }

    private static VoidBetRequest getAndUpdateVoidBetRequest(Consumer<VoidBetRequest> consumer) {
        VoidBetRequest voidBetRequest = TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS);
        consumer.accept(voidBetRequest);
        return voidBetRequest;
    }

    public static RollBackBetRequest getAndUpdateRollBackBetRequest(Consumer<RollBackBetRequest> consumer) {
        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_ONE_POUND);
        consumer.accept(rollBackBetRequest);
        return rollBackBetRequest;
    }

    public static CreditRequest getAndUpdateCreditRequest(Consumer<CreditRequest> consumer) {
        CreditRequest creditRequest = TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS);
        consumer.accept(creditRequest);
        return creditRequest;
    }

    private static lithium.service.casino.client.objects.response.BalanceResponse validCasinoClientBalanceResponse(String balanceCents) {
        return lithium.service.casino.client.objects.response.BalanceResponse.builder().balanceCents(Long.valueOf(balanceCents)).build();
    }

    private static AwardWinningsRequest getAndUpdateJackPotAwardWinningsRequest(Consumer<AwardWinningsRequest> consumer) {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        consumer.accept(awardWinningsRequest);
        return awardWinningsRequest;
    }

    private static lithium.service.casino.client.objects.response.LastBetResultResponse validLastBetResultResponse(Double amount) {
        return lithium.service.casino.client.objects.response.LastBetResultResponse.builder()
                .returns(amount)
                .betResultKindCode("LOSS")
                .roundComplete(Boolean.FALSE)
                .transactionTimestamp(new Date()).build();
    }
}
