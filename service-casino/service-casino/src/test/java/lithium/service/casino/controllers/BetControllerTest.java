package lithium.service.casino.controllers;

import lithium.client.changelog.ChangeLogService;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Counter;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.data.entities.Winner;
import lithium.service.casino.data.projection.repositories.PlayerBonusHistoryActivationProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusHistoryProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusPendingProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusProjectionRepository;
import lithium.service.casino.data.repositories.BonusExternalGameConfigRepository;
import lithium.service.casino.data.repositories.BonusFreeMoneyRepository;
import lithium.service.casino.data.repositories.BonusRepository;
import lithium.service.casino.data.repositories.BonusRequirementsDepositRepository;
import lithium.service.casino.data.repositories.BonusRequirementsSignupRepository;
import lithium.service.casino.data.repositories.BonusRevisionRepository;
import lithium.service.casino.data.repositories.BonusRulesFreespinGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesFreespinsRepository;
import lithium.service.casino.data.repositories.BonusRulesGamesPercentagesRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.GameCategoryRepository;
import lithium.service.casino.data.repositories.PlayerBonusExternalGameLinkRepository;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.casino.data.repositories.PlayerBonusPendingRepository;
import lithium.service.casino.data.repositories.PlayerBonusRepository;
import lithium.service.casino.service.BonusRoundTrackService;
import lithium.service.casino.service.CasinoBalanceAdjustmentService;
import lithium.service.casino.service.CasinoBonusFreespinService;
import lithium.service.casino.service.CasinoBonusService;
import lithium.service.casino.service.CasinoBonusUnlockGamesService;
import lithium.service.casino.service.CasinoGeoService;
import lithium.service.casino.service.CasinoMailSmsService;
import lithium.service.casino.service.CasinoService;
import lithium.service.casino.service.CasinoTriggerBonusService;
import lithium.service.casino.service.CurrencyService;
import lithium.service.casino.service.GraphicsService;
import lithium.service.casino.service.WinnerFeedService;
import lithium.service.casino.test.CasinoTestData;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.event.client.stream.EventStream;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.service.LoginEventClientService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;


/**
 * A suite of tests against the bet API for service casino. Some of these are kept in here to continue
 * support of legacy bet API.
 *
 * TODO Develop a test that rejects duplicate bet transactions with same provider reference
 * TODO Develop a test that rejects duplicate reversals
 */
@Slf4j
public class BetControllerTest {

    private final String domainName = "playerdomain";
    private final String currency = "ZAR";
    private final String userGuid = domainName + "/" + "100";
    private final Locale locale = new Locale("en");

    private Domain domain = Mockito.mock(Domain.class);

    private ServiceCasinoConfigurationProperties config = Mockito.mock(ServiceCasinoConfigurationProperties.class);
    private LithiumServiceClientFactory services = Mockito.mock(LithiumServiceClientFactory.class);
    private CasinoBalanceAdjustmentService adjustmentService = Mockito.mock(CasinoBalanceAdjustmentService.class);
    private CasinoGeoService casinoGeoService = Mockito.mock(CasinoGeoService.class);
    private EventStream eventStream = Mockito.mock(EventStream.class);
    private CurrencyService currencyService = Mockito.mock(CurrencyService.class);
    private CasinoBonusFreespinService casinoBonusFreespinService = Mockito.mock(CasinoBonusFreespinService.class);
    private LithiumMetricsService metrics = Mockito.mock(LithiumMetricsService.class);
    private BonusRepository bonusRepository = Mockito.mock(BonusRepository.class);
    private PlayerBonusRepository playerBonusRepository = Mockito.mock(PlayerBonusRepository.class);
    private PlayerBonusProjectionRepository playerBonusProjectionRepository = Mockito.mock(PlayerBonusProjectionRepository.class);
    private PlayerBonusHistoryActivationProjectionRepository playerBonusHistoryActivationProjectionRepository = Mockito.mock(PlayerBonusHistoryActivationProjectionRepository.class);
    private PlayerBonusHistoryRepository playerBonusHistoryRepository = Mockito.mock(PlayerBonusHistoryRepository.class);
    private BonusRequirementsDepositRepository bonusRequirementsDepositRepository = Mockito.mock(BonusRequirementsDepositRepository.class);
    private BonusRulesFreespinsRepository bonusRulesFreespinsRepository = Mockito.mock(BonusRulesFreespinsRepository.class);
    private BonusRulesFreespinGamesRepository bonusRulesFreespinGamesRepository = Mockito.mock(BonusRulesFreespinGamesRepository.class);
    private BonusRulesGamesPercentagesRepository bonusRulesGamesPercentagesRepository = Mockito.mock(BonusRulesGamesPercentagesRepository.class);
    private BonusRevisionRepository bonusRevisionRepository = Mockito.mock(BonusRevisionRepository.class);
    private BonusRequirementsSignupRepository bonusRequirementsSignupRepository = Mockito.mock(BonusRequirementsSignupRepository.class);
    private FreeRoundBonusController freeRoundBonusController = Mockito.mock(FreeRoundBonusController.class);
    private GameCategoryRepository gameCategoryRepository = Mockito.mock(GameCategoryRepository.class);
    private DomainRepository domainRepository = Mockito.mock(DomainRepository.class);
    private ChangeLogService changeLogService = Mockito.mock(ChangeLogService.class);
    private NotificationStream notificationStream = Mockito.mock(NotificationStream.class);
    private PlayerBonusPendingRepository playerBonusPendingRepository = Mockito.mock(PlayerBonusPendingRepository.class);
    private PlayerBonusPendingProjectionRepository playerBonusPendingProjectionRepository = Mockito.mock(PlayerBonusPendingProjectionRepository.class);
    private BonusRoundTrackService bonusRoundTrackService = Mockito.mock(BonusRoundTrackService.class);
    private GraphicsService graphicsService = Mockito.mock(GraphicsService.class);
    private CasinoMailSmsService casinoMailSmsService = Mockito.mock(CasinoMailSmsService.class);
    private CasinoBonusUnlockGamesService casinoBonusUnlockGamesService = Mockito.mock(CasinoBonusUnlockGamesService.class);
    private BonusFreeMoneyRepository bonusFreeMoneyRepository = Mockito.mock(BonusFreeMoneyRepository.class);
    private MissionStatsStream missionStatsStream = Mockito.mock(MissionStatsStream.class);
    private StatsStream statsStream = Mockito.mock(StatsStream.class);
    private BonusExternalGameConfigRepository bonusExternalGameConfigRepository = Mockito.mock(BonusExternalGameConfigRepository.class);
    private PlayerBonusExternalGameLinkRepository playerBonusExternalGameLinkRepository = Mockito.mock(PlayerBonusExternalGameLinkRepository.class);
    private PlayerBonusHistoryProjectionRepository playerBonusHistoryProjectionRepository = Mockito.mock(PlayerBonusHistoryProjectionRepository.class);
    private CasinoTriggerBonusService casinoTriggerBonusService = Mockito.mock(CasinoTriggerBonusService.class);
    private CasinoMailSmsService casinoSmsService = Mockito.mock(CasinoMailSmsService.class);
    private ModelMapper modelMapper = Mockito.mock(ModelMapper.class);
    private LimitInternalSystemService limitInternalSystemService = Mockito.mock(LimitInternalSystemService.class);
    private LoginEventClientService loginEventHelperService = Mockito.mock(LoginEventClientService.class);
    private LocaleContextProcessor localeContextProcessor = Mockito.mock(LocaleContextProcessor.class);

    private AccountingClient accountingClient = Mockito.mock(AccountingClient.class);
    private Counter accountingAdjustmentCounter;

    private Counter limitCounter;

    private WinnerFeedService winnerFeedService = Mockito.mock(WinnerFeedService.class);
    private Counter winnerFeedCounter;

    private BetController betController;
    private CasinoService casinoService;
    private CasinoBonusService casinoBonusService;

    private BetRequest.BetRequestBuilder betRequestBuilder = BetRequest.builder();

    private UserApiInternalClientService userApiInternalClientService = Mockito.mock(UserApiInternalClientService.class);

    private CachingDomainClientService cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);

    private ProviderClientService providerClientService = Mockito.mock(ProviderClientService.class);

    // We should be setting all the other dependencies here, but since there are not yet
    // many tests, we skip them for now. The current set of tests do not yet exercise all paths.
    // TODO build a lot of tests that execute both the bonus and non-bonus paths.
    @Before
    public void setUp() throws Exception, UserClientServiceFactoryException {
        casinoService = new CasinoService();
        casinoService.setServices(services);
        casinoService.setCasinoGeoService(casinoGeoService);
        casinoService.setEventStream(eventStream);
        casinoService.setCurrencyService(currencyService);
        casinoService.setLoginEventHelperService(loginEventHelperService);
        casinoBonusService = new CasinoBonusService();
        casinoBonusService.setCasinoBonusFreespinService(casinoBonusFreespinService);
        casinoBonusService.setPlayerBonusRepository(playerBonusRepository);
        casinoBonusService.setMetrics(metrics);
        casinoBonusService.setBonusRepository(bonusRepository);
        casinoBonusService.setXpRequiredForHourlyBonus(100L);
        betController = new BetController(config, casinoService,
                casinoBonusService, winnerFeedService, services, adjustmentService, userApiInternalClientService,
                cachingDomainClientService, providerClientService, limitInternalSystemService, localeContextProcessor);

        // Lets mock some of the more standard responses...
        when(domain.getCurrency()).thenReturn("USD");
        when(currencyService.retrieveDomainFromDomainService(domainName)).thenReturn(domain);
        when(services.target(eq(AccountingClient.class), anyString(), anyBoolean())).thenReturn(accountingClient);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("performUserChecks called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(userApiInternalClientService).performUserChecks(anyString(), anyString(), anyLong(), anyBoolean(),
                anyBoolean(), anyBoolean());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("checkBettingEnabled called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(cachingDomainClientService).checkBettingEnabled(anyString(), anyString());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("checkProviderEnabled called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(providerClientService).checkProviderEnabled(anyString(), anyString(), anyString());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("checkPlayerRestrictions called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(limitInternalSystemService).checkPlayerRestrictions(anyString(), anyString());
        log.info("Setup complete");
    }

    @Test
    public void testCasinoBetSuccess() throws Exception {
        mockBetDefaults();
        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setBet(100L);
        betRequest.setProviderGuid("domainName/providerUrl");
        assertBetResponseSuccess(betController.handleBetRequestV2(betRequest, locale));
    }

    @Test
    public void testCasinoBetInsufficientFunds() throws Exception {
        mockBetDefaults();
        mockBalance(0L);
        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setBet(100L);
        betRequest.setProviderGuid("domainName/providerUrl");
        assertBetResponseInsufficientFunds(betController.handleBetRequestV2(betRequest, locale));
    }

    @Test
    public void testCasinoWinSuccess() throws Exception {
        mockWinDefaults();
        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setWin(100L);
        assertWinResponseSuccess(betController.handleSettleRequestV2(betRequest, locale));
    }

    @Test
    public void testVirtualBetSuccess() throws Exception {
        mockBetDefaults();
        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setBet(100L);
        betRequest.setTranType(CasinoTranType.VIRTUAL_BET);
        betRequest.setProviderGuid("domainName/providerUrl");
        assertBetResponseSuccess(betController.handleBetRequestV2(betRequest, locale));
    }


    @Test
    public void testVirtualFreeBetSuccess() throws Exception {
        mockBetDefaults();
        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setBet(100L);
        betRequest.setTranType(CasinoTranType.VIRTUAL_FREE_BET);
        betRequest.setProviderGuid("domainName/providerUrl");
        assertBetResponseSuccess(betController.handleBetRequestV2(betRequest, locale));
    }

    // This tests the old behavior of insufficient funds. It returns a 0L and success, not ideal but needed
    // for old casino providers.
    @Test
    public void testVirtualBetInsufficientFunds() throws Exception {
        mockBetDefaults();
        mockBalance(0L);
        mockAccountingAdjustInsufficientFunds();
        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setBet(100L);
        betRequest.setTranType(CasinoTranType.VIRTUAL_BET);
        betRequest.setProviderGuid("domainName/providerUrl");
        assertBetResponseInsufficientFunds(betController.handleBetRequestV2(betRequest, locale));
    }

    @Test
    public void testVirtualVoid() throws Exception {
        mockWinDefaults();
        mockAccountingAdjustSuccess(2L);

        BetRequest betRequest = CasinoTestData.createBetRequest(domainName, currency, userGuid);
        betRequest.setWin(100L);
        betRequest.setOriginalTransactionId(1L);
        betRequest.setTransactionId("SOME_TRANSACTION_ID");
        betRequest.setTranType(CasinoTranType.VIRTUAL_BET_VOID);

        assertWinResponseSuccess(betController.handleSettleRequestV2(betRequest, locale));
    }

    private void mockBalance(Long balance) throws Exception {
        when(accountingClient.getByAccountType(domainName, "PLAYER_BALANCE", currency, userGuid))
                .then(invocation -> {
            Response<Map<String, Long>> response = CasinoTestData.balances(balance);
            log.debug("balance request PLAYER_BALANCE " + invocation.toString() + " " + response);
            return response;
        });
    }

    //TODO: Add this in where needed
    private void getByAccountTypeWithExceptions(Long balance) throws Exception {
        when(accountingClient.getByAccountTypeWithExceptions(domainName, "PLAYER_BALANCE", currency, userGuid))
                .then(invocation -> {
            Response<Map<String, Long>> response = CasinoTestData.balances(balance);
            log.debug("balance request PLAYER_BALANCE " + invocation.toString() + " " + response);
            return response;
        });
    }

    private void mockLimitsSuccess()
            throws
            Status484WeeklyLossLimitReachedException,
            Status485WeeklyWinLimitReachedException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492DailyLossLimitReachedException,
            Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException,
            Status500LimitInternalSystemClientException,
            Status478TimeSlotLimitException {
        limitCounter = new Counter();
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("checkLimits called with arguments: " + Arrays.toString(args));
                limitCounter.increment();
                return null;
            }
        }).when(limitInternalSystemService).checkLimits(anyString(), anyString(), anyString(), anyLong(), anyString());
    }

    private void mockWinnerFeedFail() {
        doThrow(new RuntimeException("should not hit a feed"))
                .when(winnerFeedService).addWinner(any(BetRequest.class));
        doThrow(new RuntimeException("should not hit a feed"))
                .when(winnerFeedService).addWinner(any(Winner.class));
    }

    private void mockWinnerFeedSuccess() {
        winnerFeedCounter = new Counter();
        Answer<Void> answer = invocation -> {
            log.debug("winnerFeedService.addWinner " + invocation.toString());
            winnerFeedCounter.increment();
            return null;
        };
        doAnswer(answer).when(winnerFeedService).addWinner(any(BetRequest.class));
        doAnswer(answer).when(winnerFeedService).addWinner(any(Winner.class));
    }

    private void mockAccountingAdjustSuccess(Long tranId) throws Exception {
        accountingAdjustmentCounter = new Counter();
        when(accountingClient.adjust(anyLong(), anyString(), anyString(), anyString(), anyString(), any(String[].class),
                eq(currency), eq(domainName), eq(userGuid), anyString(), eq(false))).then(invocation -> {
            log.debug("adjust called: " + invocation.toString());
            Response<AdjustmentTransaction> response = new Response<>();
            response.setStatus(Response.Status.OK);
            response.setData(new AdjustmentTransaction());
            response.getData().setTransactionId(tranId);
            accountingAdjustmentCounter.increment();
            return response;
        });
    }

    private void mockAccountingAdjustInsufficientFunds() throws Exception {
        accountingAdjustmentCounter = new Counter();
        when(accountingClient.adjust(anyLong(), anyString(), anyString(), anyString(), anyString(), any(String[].class),
                eq(currency), eq(domainName), eq(userGuid), anyString(), eq(false))).then(invocation -> {
            log.debug("adjust called: " + invocation.toString());
            accountingAdjustmentCounter.increment();
            throw new Exception("Insufficient Funds");
        });
    }

    private void mockBetDefaults() throws Exception {
        mockBalance(100L);
        mockLimitsSuccess();
        mockWinnerFeedFail();
        mockAccountingAdjustSuccess(1L);
    }

    private void mockWinDefaults() throws Exception {
        mockBalance(0L);
        mockLimitsSuccess();
        mockWinnerFeedSuccess();
        mockAccountingAdjustSuccess(2L);
    }

    private void assertWinResponseSuccess(Response<BetResponse> response) {
        log.debug(response.toString());
        assertTrue("BetResponse should be successful", response.isSuccessful());
        assertNotEquals("BetResponse should return a transaction ID", "0", response.getData().getExtSystemTransactionId());
        assertEquals("Limits should NOT be checked", 0, limitCounter.getValue());
        assertEquals("Accounting should be called", 1, accountingAdjustmentCounter.getValue());
        assertEquals("Winner feed should increment", 1, winnerFeedCounter.getValue());
    }

    private void assertBetResponseSuccess(Response<BetResponse> response) {
        log.debug(response.toString());
        assertTrue("BetResponse should be successful", response.isSuccessful());
        assertNotEquals("BetResponse should return a transaction ID", "0", response.getData().getExtSystemTransactionId());
        assertEquals("Limits should be checked", 1, limitCounter.getValue());
        assertEquals("Accounting should be called", 1, accountingAdjustmentCounter.getValue());
    }

    private void assertBetResponseSuccess(Response<BetResponse> response, Long expectedTranId) {
        log.debug(response.toString());
        assertTrue("BetResponse should be successful", response.isSuccessful());
        assertEquals("BetResponse should return a transaction ID", expectedTranId.toString(),
                response.getData().getExtSystemTransactionId());
        assertEquals("Limits should be checked", 1, limitCounter.getValue());
        assertEquals("Accounting should be called", 1, accountingAdjustmentCounter.getValue());
    }

    private void assertBetResponseInsufficientFunds(Response<BetResponse> response) {
        log.debug(response.toString());
        assertTrue("BetResponse should be successful", response.isSuccessful());
        assertEquals("BetResponse should not return a transaction ID", "0",
                response.getData().getExtSystemTransactionId());
        assertEquals("Limits should be checked", 1, limitCounter.getValue());
        assertEquals("Accounting should not be called", 0, accountingAdjustmentCounter.getValue());
    }

}
