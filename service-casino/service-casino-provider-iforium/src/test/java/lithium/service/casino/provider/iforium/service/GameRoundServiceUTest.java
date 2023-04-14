package lithium.service.casino.provider.iforium.service;

import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.EBalanceAdjustmentResponseStatus;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.exception.AccountNotFoundException;
import lithium.service.casino.provider.iforium.exception.CurrencyMismatchException;
import lithium.service.casino.provider.iforium.exception.GameRoundNotFoundException;
import lithium.service.casino.provider.iforium.exception.GatewaySessionTokenExpiredException;
import lithium.service.casino.provider.iforium.exception.InsufficientFundsException;
import lithium.service.casino.provider.iforium.exception.InternalServerErrorException;
import lithium.service.casino.provider.iforium.exception.InvalidGameException;
import lithium.service.casino.provider.iforium.exception.InvalidGatewaySessionTokenException;
import lithium.service.casino.provider.iforium.exception.LossLimitReachedException;
import lithium.service.casino.provider.iforium.exception.NotSupportedAccountTransactionTypeException;
import lithium.service.casino.provider.iforium.exception.SessionLengthLimitReachedException;
import lithium.service.casino.provider.iforium.exception.TransactionNotFoundException;
import lithium.service.casino.provider.iforium.model.request.AwardWinningsRequest;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.request.RollBackBetRequest;
import lithium.service.casino.provider.iforium.model.request.VoidBetRequest;
import lithium.service.casino.provider.iforium.model.response.Balance;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.FundsPriorities;
import lithium.service.casino.provider.iforium.model.response.GameRoundResponse;
import lithium.service.casino.provider.iforium.model.response.GameRoundResult;
import lithium.service.casino.provider.iforium.model.response.OperatorTransactionSplit;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResponse;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResult;
import lithium.service.casino.provider.iforium.service.impl.GameRoundServiceImpl;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GBP_CURRENCY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SHADOW_DEPOSIT_TRANSACTION_TYPE_ID;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.setUpGameResponseMockByGuidAndDomainName;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.setUpSystemLoginEventsClientMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GameRoundServiceUTest {

    private static final BigDecimal AMOUNT_ZERO_POUND = new BigDecimal("0.0");
    private static final BigDecimal AMOUNT_ONE_POUND = new BigDecimal("1.0");
    private static final BigDecimal AMOUNT_TEN_POUNDS = new BigDecimal("10.0");

    private GameRoundServiceImpl gameRoundServiceImpl;

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
        this.gameRoundServiceImpl = new GameRoundServiceImpl(new LithiumClientUtils(lithiumServiceClientFactory),
                                                             cachingDomainClientService, casinoClientService, moduleInfo, queryRewardClientService);
    }

    @Test
    void placeBet_SuccessResponse_WhenRequestLoginEventLogoutIsNull() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        PlaceBetResponse actualPlaceBetResponse = this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME);

        PlaceBetResponse expectedPlaceBetResponse = expectedSuccessPlaceBetResponse();

        Assertions.assertThat(actualPlaceBetResponse).isEqualTo(expectedPlaceBetResponse);
    }

    @Test
    void placeBet_SuccessResponse_WhenRequestLoginEventLogoutIsAfterCurrentTime() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(new Date().getTime() + 60000));
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        PlaceBetResponse actualPlaceBetResponse = this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME);

        PlaceBetResponse expectedPlaceBetResponse = expectedSuccessPlaceBetResponse();

        Assertions.assertThat(actualPlaceBetResponse).isEqualTo(expectedPlaceBetResponse);
    }

    @SneakyThrows
    @Test
    void placeBet_ThrowsGatewaySessionTokenExpiredException_WhenTokenIsExpired() {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(GatewaySessionTokenExpiredException.class);
    }

    @SneakyThrows
    @Test
    void placeBet_ThrowsInvalidGatewaySessionTokenException_WhenAccountIdDoesNotMatchLoginEvent() {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn("otherOperatorAccountId");

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(InvalidGatewaySessionTokenException.class);
    }

    @Test
    void placeBet_ThrowsCurrencyMismatchException_WhenCurrencyDoesNotMatchDomainCurrency() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(TestConstants.USD_CURRENCY);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void placeBet_ThrowsInvalidGameException_WhenGameResponseIsNotSuccessful() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Response<Game> game = TestMockUtils.setUpGameResponseMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.isSuccessful()).thenReturn(false);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(InvalidGameException.class);
    }

    @Test
    void placeBet_ThrowsInvalidGameException_WhenGameResponseDataIsNull() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Response<Game> game = TestMockUtils.setUpGameResponseMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.isSuccessful()).thenReturn(true);
        Mockito.when(game.getData()).thenReturn(null);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(InvalidGameException.class);
    }

    @Test
    void placeBet_ThrowsInsufficientFundsException_WhenBalanceAdjustmentResponseResultIsInsufficientFunds() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.INSUFFICIENT_FUNDS);

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void placeBet_ThrowsInternalServerErrorException_WhenBalanceAdjustmentResponseResultIsInternalServerError() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.INTERNAL_ERROR);

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(InternalServerErrorException.class);
    }

    @Test
    void placeBet_ThrowsLossLimitReachedException_WhenMultiBetV1ReturnStatus484WeeklyLossLimitReachedException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status484WeeklyLossLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        assertThrows(LossLimitReachedException.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsLossLimitReachedException_WhenMultiBetV1ReturnStatus492DailyLossLimitReachedException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status492DailyLossLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        assertThrows(LossLimitReachedException.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsLossLimitReachedException_WhenMultiBetV1ReturnStatus493MonthlyLossLimitReachedException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status493MonthlyLossLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        assertThrows(LossLimitReachedException.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsSessionLengthLimitReachedException_WhenMultiBetV1ReturnStatus478TimeSlotLimitException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status478TimeSlotLimitException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        assertThrows(SessionLengthLimitReachedException.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsSessionLengthLimitReachedException_WhenMultiBetV1ReturnStatus438PlayTimeLimitReachedException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status438PlayTimeLimitReachedException(""));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        assertThrows(SessionLengthLimitReachedException.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsException_WhenMultiBetV1ReturnStatus494DailyWinLimitReachedException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status494DailyWinLimitReachedException(""));

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsException_WhenMultiBetV1ReturnStatus495MonthlyWinLimitReachedException() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);
        Mockito.when(casinoClientService.multiBetV1(any(), any())).thenThrow(new Status495MonthlyWinLimitReachedException(""));

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @Test
    void placeBet_ThrowsException_WhenGameIsNull() throws Exception {
        PlaceBetRequest placeBetRequest = TestGameRoundUtils.validPlaceBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnNull(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.placeBet(placeBetRequest, DOMAIN_NAME));
    }

    @SneakyThrows
    @Test
    void end_SuccessResponse() {
        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("100"));

        EndRequest endRequest = TestGameRoundUtils.validEndRequest();

        BalanceResponse actualEndResponse = this.gameRoundServiceImpl.end(endRequest, DOMAIN_NAME);

        BalanceResponse expectedEndResponse = expectedSuccessEndResponse(AMOUNT_ONE_POUND);

        Assertions.assertThat(actualEndResponse).isEqualTo(expectedEndResponse);
    }

    @SneakyThrows
    @Test
    void end_SuccessResponse_WhenGatewaySessionTokenIsExpired() {
        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("100"));

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));

        EndRequest endRequest = TestGameRoundUtils.validEndRequest();

        BalanceResponse actualEndResponse = this.gameRoundServiceImpl.end(endRequest, DOMAIN_NAME);

        BalanceResponse expectedEndResponse = expectedSuccessEndResponse(AMOUNT_ONE_POUND);

        Assertions.assertThat(actualEndResponse).isEqualTo(expectedEndResponse);
    }

    @SneakyThrows
    @Test
    void end_SuccessResponse_WhenGatewaySessionTokenIsAbsent() {
        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.doNothing().when(casinoClientService).completeBetRound(any(), any(), any());

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("100"));

        EndRequest endRequest = TestGameRoundUtils.validEndRequest();
        endRequest.setGatewaySessionToken(null);

        BalanceResponse actualEndResponse = this.gameRoundServiceImpl.end(endRequest, DOMAIN_NAME);

        BalanceResponse expectedEndResponse = expectedSuccessEndResponse(AMOUNT_ONE_POUND);

        Assertions.assertThat(actualEndResponse).isEqualTo(expectedEndResponse);
    }

    @SneakyThrows
    @Test
    void end_SuccessResponse_WhenLoginEventDoesNotExist() {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);
        Domain domain = TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);
        Mockito.when(domain.getCurrency()).thenReturn(GBP_CURRENCY);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse("100"));

        EndRequest endRequest = TestGameRoundUtils.validEndRequest();

        BalanceResponse actualEndResponse = this.gameRoundServiceImpl.end(endRequest, DOMAIN_NAME);

        BalanceResponse expectedEndResponse = expectedSuccessEndResponse(AMOUNT_ONE_POUND);

        Assertions.assertThat(actualEndResponse).isEqualTo(expectedEndResponse);
    }

    @SneakyThrows
    @Test
    void end_ThrowGameRoundNotFoundException_WhenGameRoundDoesNotExist() {
        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doThrow(Status474BetRoundNotFoundException.class).when(casinoClientService).completeBetRound(any(), any(), any());
        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any())).thenReturn(validCasinoClientBalanceResponse("100"));

        EndRequest endRequest = TestGameRoundUtils.validEndRequest();

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.end(endRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(GameRoundNotFoundException.class);
    }

    @SneakyThrows
    @Test
    void end_ReturnsUnknownResponse_WhenCompleteBetRoundResultIsInternalServerError() {
        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        Mockito.when(casinoClientService.findLastBetResult(any(), any(), any()))
                .thenReturn(validLastBetResultResponse(0.0));
        Mockito.doThrow(Status500UnhandledCasinoClientException.class).when(casinoClientService).completeBetRound(any(), any(), any());

        EndRequest endRequest = TestGameRoundUtils.validEndRequest();

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.end(endRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(Status500UnhandledCasinoClientException.class);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenSessionIsValid() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenSessionIsExpired() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ZERO_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setGatewaySessionToken(null);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ZERO_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenFindBySessionKeyThrowsStatus412LoginEventNotFoundException() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ZERO_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZero() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);

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

        GameRoundResponse actualAwardWinningsResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedPlaceBetResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                      TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualAwardWinningsResponse).isEqualTo(expectedPlaceBetResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZeroAndEndRoundIsTrue() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZeroAndEndRoundIsFalse() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(false);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsNotZeroAndEndRoundIsFalse() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ONE_POUND);
        awardWinningsRequest.setEndRound(false);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsNotZeroAndEndRoundIsTrue() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setAmount(AMOUNT_ONE_POUND);
        awardWinningsRequest.setEndRound(true);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_SuccessResponse_WhenAmountIsZeroAndEndRoundIsTrueAndGatewaySessionTokenIsAbsent() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setGatewaySessionToken(null);
        awardWinningsRequest.setAmount(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void awardWinnings_ThrowsException_WhenGameIsNull() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnNull(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME));
    }

    @Test
    void awardWinnings_ThrowsException_WhenGameThrowException() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnException(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME));
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsExpired() throws Exception {

        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsValid() throws Exception {

        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void rollBackBet_SuccessResponse_WhenSessionIsAbsent() throws Exception {

        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        rollBackBetRequest.setGatewaySessionToken(null);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void rollBAckBet_SuccessResponse_WhenFindBySessionKeyThrowsStatus412LoginEventNotFoundException() throws Exception {

        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
               .thenThrow(Status412LoginEventNotFoundException.class);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void rollBackBet_SuccessResponse_WhenEndRoundIsTrue() throws Exception {
        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        rollBackBetRequest.setEndRound(true);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @SneakyThrows
    @Test
    void rollBackBet_ThrowGameRoundNotFoundException_WhenGameRoundDoesNotExist() {
        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.TRANSACTION_DATA_VALIDATION_ERROR);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any())).thenReturn(validCasinoClientBalanceResponse("100"));

        Assertions.assertThatThrownBy(() -> this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME))
                  .isExactlyInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    void rollBackBet_ThrowsException_WhenGameIsNull() throws Exception {

        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnNull(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME));
    }

    @Test
    void rollBackBet_ThrowsException_WhenGameThrowException() throws Exception {

        RollBackBetRequest rollBackBetRequest = TestGameRoundUtils.validRollBackBetRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnException(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.rollBackBet(rollBackBetRequest, DOMAIN_NAME));
    }

    @Test
    @SneakyThrows
    void voidBet_SuccessResponse() {
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

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.voidBet(TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS),
                                                                                 DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    @SneakyThrows
    void voidBet_SuccessResponse_WhenRequestContainsOptionalParameters() {
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

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.voidBet(TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS), DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void voidBet_SuccessResponse_WhenSessionIsExpired() throws Exception {
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

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.voidBet(TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS), DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void voidBet_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        VoidBetRequest request = TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS);
        request.setGatewaySessionToken(null);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.voidBet(request, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void voidBet_SuccessResponse_WhenGatewaySessionTokenIsEmpty() throws Exception {
        VoidBetRequest request = TestGameRoundUtils.validVoidBetRequest(true, AMOUNT_TEN_POUNDS);
        request.setGatewaySessionToken("");

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.voidBet(request, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void voidBet_ReturnStatus550ServiceDomainClientException_WhenDomainIsNotValid() throws Exception {
        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenThrow(Status550ServiceDomainClientException.class);

        assertThrows(Status550ServiceDomainClientException.class, () -> gameRoundServiceImpl.voidBet(
                TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS), DOMAIN_NAME));
    }

    @Test
    void voidBet_ReturnsAccountNotFoundException_WhenAccountIdDoesNotMatchLoginEvent() throws Exception {
        LoginEvent loginEvent = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn("otherOperatorAccountId");

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEvent);

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> gameRoundServiceImpl.voidBet(
                TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS), DOMAIN_NAME));

        assertThat(exception.getMessage()).contains(
                "loginEvent.user.guid=otherOperatorAccountId does not match VoidBetRequest.operatorAccountId=domain/accountId");
    }

    @Test
    void voidBet_ReturnsInvalidGameException_WhenGameIdDoesNotExist() throws Exception {
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

        InvalidGameException exception = assertThrows(InvalidGameException.class, () -> gameRoundServiceImpl.voidBet(
                TestGameRoundUtils.validVoidBetRequest(false, AMOUNT_TEN_POUNDS), DOMAIN_NAME));

        assertThat(exception.getMessage()).contains("GameGuid=service-casino-provider-iforium_gameId is not configured for domain=domain");
    }

    @Test
    void credit_SuccessResponse() throws Exception {
        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.credit(TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS), DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.ACCOUNT_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void credit_ReturnNotSupportedAccountTransactionTypeException_WhenAccountTransactionTypeIs811() {
        CreditRequest creditRequest = TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS);
        creditRequest.setAccountTransactionTypeId(SHADOW_DEPOSIT_TRANSACTION_TYPE_ID);

        NotSupportedAccountTransactionTypeException exception = assertThrows(NotSupportedAccountTransactionTypeException.class,
                                                                             () -> gameRoundServiceImpl.credit(creditRequest, DOMAIN_NAME));

        Assertions.assertThat(exception.getMessage()).isEqualTo("accountTransactionTypeId=811 is not supported");
    }

    @Test
    void credit_SuccessResponse_WhenAmountIsZero() throws Exception {
        CreditRequest creditRequest = TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS);
        creditRequest.setAmount(AMOUNT_ZERO_POUND);

        TestMockUtils.setUpDomainMockByDomainName(cachingDomainClientService);

        Mockito.when(casinoClientService.getPlayerBalance(any(), any(), any()))
               .thenReturn(validCasinoClientBalanceResponse(TestConstants.AMOUNT_ONE_HUNDRED_CENTS.toString()));

        GameRoundResponse actualGameRoundResponse = gameRoundServiceImpl.credit(creditRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                                                                                       TestConstants.ACCOUNT_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void credit_ReturnStatus550ServiceDomainClientException_WhenDomainIsNotValid() throws Exception {

        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(DOMAIN_NAME))
               .thenThrow(Status550ServiceDomainClientException.class);

        assertThrows(Status550ServiceDomainClientException.class,
                     () -> gameRoundServiceImpl.credit(TestGameRoundUtils.validCreditRequest(AMOUNT_TEN_POUNDS), DOMAIN_NAME));
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenSessionIsValid() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenSessionIsExpired() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(new Date(1));

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ZERO_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenGatewaySessionTokenIsAbsent() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setGatewaySessionToken(null);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ZERO_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenFindBySessionKeyThrowsStatus412LoginEventNotFoundException() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock(lithiumServiceClientFactory);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY))
                .thenThrow(Status412LoginEventNotFoundException.class);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ZERO_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZero() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);

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

        GameRoundResponse actualAwardWinningsResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedPlaceBetResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualAwardWinningsResponse).isEqualTo(expectedPlaceBetResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZeroAndEndRoundIsTrue() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZeroAndEndRoundIsFalse() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(false);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsNotZeroAndEndRoundIsFalse() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ONE_POUND);
        awardWinningsRequest.setEndRound(false);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsNotZeroAndEndRoundIsTrue() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ONE_POUND);
        awardWinningsRequest.setEndRound(true);

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

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_SuccessResponse_WhenJackPotWinningsIsZeroAndEndRoundIsTrueAndGatewaySessionTokenIsAbsent() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);
        awardWinningsRequest.setGatewaySessionToken(null);
        awardWinningsRequest.setJackpotWinnings(AMOUNT_ZERO_POUND);
        awardWinningsRequest.setEndRound(true);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        Game game = TestMockUtils.setUpGameMockByGuidAndDomainName(lithiumServiceClientFactory);
        Mockito.when(game.getGuid()).thenReturn(TestConstants.GAME_GUID);

        BalanceAdjustmentResponse balanceAdjustmentResponse = TestMockUtils.setUpBalanceAdjustmentResponseMockForMultiBetV1(
                casinoClientService);
        Mockito.when(balanceAdjustmentResponse.getResult()).thenReturn(EBalanceAdjustmentResponseStatus.SUCCESS);
        Mockito.when(balanceAdjustmentResponse.getBalanceCents()).thenReturn(TestConstants.AMOUNT_ONE_HUNDRED_CENTS);

        GameRoundResponse actualGameRoundResponse = this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME);

        GameRoundResponse expectedGameRoundResponse = expectedSuccessGameRoundResponse(AMOUNT_ONE_POUND,
                TestConstants.GAME_ROUND_TRANSACTION_ID);

        Assertions.assertThat(actualGameRoundResponse).isEqualTo(expectedGameRoundResponse);
    }

    @Test
    void jackPotAwardWinnings_ThrowsException_WhenGameIsNull() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnNull(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME));
    }

    @Test
    void jackPotAwardWinnings_ThrowsException_WhenGameThrowException() throws Exception {
        AwardWinningsRequest awardWinningsRequest = TestGameRoundUtils.validJackpotAwardWinningsRequest(AMOUNT_TEN_POUNDS);

        LoginEvent loginEvent = TestMockUtils.setUpLoginEventMockBySessionKey(lithiumServiceClientFactory);
        Mockito.when(loginEvent.getLogout()).thenReturn(null);
        Mockito.when(loginEvent.getUser().getGuid()).thenReturn(TestConstants.OPERATOR_ACCOUNT_ID);

        Mockito.when(moduleInfo.getModuleName()).thenReturn(TestConstants.MODULE_NAME);
        TestMockUtils.setUpGameResponseMockByGuidAndDomainNameReturnException(lithiumServiceClientFactory);

        assertThrows(Exception.class, () -> this.gameRoundServiceImpl.awardWinnings(awardWinningsRequest, DOMAIN_NAME));
    }

    private static PlaceBetResponse expectedSuccessPlaceBetResponse() {
        PlaceBetResponse expectedPlaceBetResponse = new PlaceBetResponse();
        expectedPlaceBetResponse.setErrorCode(ErrorCodes.SUCCESS.getCode());
        expectedPlaceBetResponse.setBalance(Balance.builder()
                                                   .currencyCode(GBP_CURRENCY)
                                                   .cashFunds(AMOUNT_ONE_POUND)
                                                   .bonusFunds(AMOUNT_ZERO_POUND)
                                                   .fundsPriority(FundsPriorities.UNKNOWN.getName())
                                                   .build());
        expectedPlaceBetResponse.setResult(
                new PlaceBetResult(TestConstants.GAME_ROUND_TRANSACTION_ID, new OperatorTransactionSplit(AMOUNT_ZERO_POUND, AMOUNT_TEN_POUNDS)));
        return expectedPlaceBetResponse;
    }

    private static BalanceResponse expectedSuccessEndResponse(BigDecimal cashFounds) {
        BalanceResponse expectedEndResponse = new BalanceResponse();
        expectedEndResponse.setErrorCode(ErrorCodes.SUCCESS.getCode());
        expectedEndResponse.setBalance(Balance.builder()
                                              .currencyCode(GBP_CURRENCY)
                                              .cashFunds(cashFounds)
                                              .bonusFunds(AMOUNT_ZERO_POUND)
                                              .fundsPriority(FundsPriorities.UNKNOWN.getName())
                                              .build());

        return expectedEndResponse;
    }

    private static GameRoundResponse expectedSuccessGameRoundResponse(BigDecimal cashFunds, String operatorTransactionReference) {
        GameRoundResponse gameRoundResponse = new GameRoundResponse();
        gameRoundResponse.setErrorCode(ErrorCodes.SUCCESS.getCode());
        gameRoundResponse.setBalance(Balance.builder()
                                            .currencyCode(GBP_CURRENCY)
                                            .cashFunds(cashFunds)
                                            .bonusFunds(AMOUNT_ZERO_POUND)
                                            .fundsPriority(FundsPriorities.UNKNOWN.getName())
                                            .build());
        gameRoundResponse.setResult(new GameRoundResult(operatorTransactionReference));
        return gameRoundResponse;
    }

    private static lithium.service.casino.client.objects.response.BalanceResponse validCasinoClientBalanceResponse(String balanceCents) {
        return lithium.service.casino.client.objects.response.BalanceResponse.builder().balanceCents(Long.valueOf(balanceCents)).build();
    }

    private static lithium.service.casino.client.objects.response.LastBetResultResponse validLastBetResultResponse(Double amount) {
        return lithium.service.casino.client.objects.response.LastBetResultResponse.builder()
                .returns(amount)
                .betResultKindCode("LOSS")
                .roundComplete(Boolean.FALSE)
                .transactionTimestamp(new Date()).build();
    }
}
