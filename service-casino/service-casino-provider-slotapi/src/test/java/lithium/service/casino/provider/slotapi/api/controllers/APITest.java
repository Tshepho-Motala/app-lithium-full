package lithium.service.casino.provider.slotapi.api.controllers;

import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.provider.slotapi.config.ProviderConfig;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.services.BetResultService;
import lithium.service.casino.provider.slotapi.services.BetService;
import lithium.service.casino.provider.slotapi.services.bet.BetPhase1Validate;
import lithium.service.casino.provider.slotapi.services.bet.BetPhase3CallCasino;
import lithium.service.casino.provider.slotapi.services.betresult.BetResultPhase1Validate;
import lithium.service.casino.provider.slotapi.services.betresult.BetResultPhase2Persist;
import lithium.service.casino.provider.slotapi.services.betresult.BetResultPhase3CallCasino;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult;
import lithium.service.casino.provider.slotapi.storage.entities.Currency;
import lithium.service.casino.provider.slotapi.storage.entities.Domain;
import lithium.service.casino.provider.slotapi.storage.entities.User;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultKindRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRoundRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.DomainRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.GameRepository;
import lithium.service.casino.provider.slotapi.storage.repositories.UserRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class APITest {

    // Mocks
    ModuleInfo moduleInfo = Mockito.mock(ModuleInfo.class);
    LithiumTokenUtil tokenUtil = mock(LithiumTokenUtil.class);
    LimitInternalSystemService limitInternalSystemService = Mockito.mock(LimitInternalSystemService.class);
    LithiumTokenUtilService tokenService = Mockito.mock(LithiumTokenUtilService.class);
    ProviderConfigService configService = Mockito.mock(ProviderConfigService.class);
    BetRepository betRepository = Mockito.mock(BetRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    CurrencyRepository currencyRepository = Mockito.mock(CurrencyRepository.class);
    DomainRepository domainRepository = Mockito.mock(DomainRepository.class);
    BetResultRepository settlementRepository = Mockito.mock(BetResultRepository.class);
    BetResultKindRepository settlementResultRepository = Mockito.mock(BetResultKindRepository.class);
    CachingDomainClientService cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
    LithiumServiceClientFactory lithiumServiceClientFactory = Mockito.mock(LithiumServiceClientFactory.class);
    GameRepository gameRepository = Mockito.mock(GameRepository.class);
    BetRoundRepository betRoundRepository = Mockito.mock(BetRoundRepository.class);
    CasinoClient casinoClient = Mockito.mock(CasinoClient.class);
    UserApiInternalClientService userApiInternalClientService = Mockito.mock(UserApiInternalClientService.class);
    BetResultRepository betResultRepository = Mockito.mock(BetResultRepository.class);

    // Services under test
    CasinoClientService casinoClientService = new CasinoClientService();
    BetController placementController = new BetController();
    BetService placementService = new BetService();
    BetPhase1Validate phase1Validate = new BetPhase1Validate();
//    BetPhase2Persist phase2Persist = new BetPhase2Persist(userRepository, currencyRepository, betRepository, domainRepository,
//            gameRepository, betRoundRepository
//    );
    BetPhase3CallCasino phase3CallCasino = new BetPhase3CallCasino();

    BetResultController settlementController = new BetResultController();
    BetResultService settlementService = new BetResultService();
    BetResultPhase1Validate settlementPhase1Validate = new BetResultPhase1Validate();
    BetResultPhase2Persist settlementPhase2Persist = new BetResultPhase2Persist();
    BetResultPhase3CallCasino settlementPhase3CallCasino = new BetResultPhase3CallCasino();


    Domain domain = Domain.builder().name("domain").build();
    Currency currency = Currency.builder().code("USD").build();

    final lithium.math.CurrencyAmount startingBalance = lithium.math.CurrencyAmount.fromAmount(1000);

    @Before
    public void setUp() throws Exception {

        ProviderConfig config = new ProviderConfig();
        config.setHashPassword("HASHPASSWORD");

        when(configService.getConfig(any(), any())).thenReturn(config);

        when(lithiumServiceClientFactory.target(CasinoClient.class,"service-casino", true)).thenReturn(casinoClient);

        casinoClientService.setServices(lithiumServiceClientFactory);
        casinoClientService.setCachingDomainClientService(cachingDomainClientService);

        when(cachingDomainClientService.retrieveDomainFromDomainService(any())).thenReturn(
                lithium.service.domain.client.objects.Domain.builder().name(
                        domain.getName()).currency(currency.getCode())
                        .build());

        phase1Validate.setModuleInfo(moduleInfo);
        phase1Validate.setConfigService(configService);

        phase3CallCasino.setCasinoService(casinoClientService);
        phase3CallCasino.setConfigService(configService);
        phase3CallCasino.setModuleInfo(moduleInfo);
        phase3CallCasino.setBetRepository(betRepository);

        placementService.setTokenService(tokenService);
        placementService.setModuleInfo(moduleInfo);
        placementService.setLimits(limitInternalSystemService);
        placementService.setPhase1Validate(phase1Validate);
//        placementService.setPhase2Persist(phase2Persist);
        placementService.setPhase3CallCasino(phase3CallCasino);
        placementService.setUserApiInternalClientService(userApiInternalClientService);
        placementController.setService(placementService);

        settlementPhase1Validate.setConfigService(configService);
        settlementPhase1Validate.setModule(moduleInfo);

        settlementPhase2Persist.setCurrencyRepository(currencyRepository);

        settlementPhase3CallCasino.setCasinoService(casinoClientService);
        settlementPhase3CallCasino.setConfigService(configService);
        settlementPhase3CallCasino.setModuleInfo(moduleInfo);

        settlementService.setPhase1Validate(settlementPhase1Validate);
        settlementService.setPhase2Persist(settlementPhase2Persist);
        settlementService.setPhase3CallCasino(settlementPhase3CallCasino);

        when(tokenService.getUtil(any())).thenReturn(tokenUtil);
        when(betResultRepository.save(any(BetResult.class))).thenAnswer(invocation -> {
            BetResult betResult = (BetResult) invocation.getArguments()[0];
            betResult.setId(1L);
            return betResult;
        });
        when(currencyRepository.findOrCreateByCode(any(), any())).thenReturn(currency);
        when(domainRepository.findOrCreateByName(any(), any())).thenReturn(domain);
        when(userRepository.findOrCreateByGuid(any(), any())).thenReturn(User.builder().build());
        when(settlementRepository.save(any(BetResult.class))).then(invocation -> {
           BetResult settlement = invocation.getArgument(0, BetResult.class);
           settlement.setId(1L);
           return settlement;
        });

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("performUserChecks called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(userApiInternalClientService).performUserChecks(anyString(), anyString(), anyLong(), anyBoolean(),
                anyBoolean(), anyBoolean());

        when(casinoClient.handleBetRequest(any())).then(invocation -> {
            log.debug(invocation.toString());

            lithium.service.casino.client.objects.request.BetRequest request = invocation.getArgument(0, lithium.service.casino.client.objects.request.BetRequest.class);

            lithium.math.CurrencyAmount balance = lithium.math.CurrencyAmount.fromCents(startingBalance.toCents());
            if (request.getWin() != null) {
                balance.addCents(request.getWin());
            }
            if (request.getBet() != null) {
                balance.subtractCents(request.getBet());
            }

            lithium.service.casino.client.objects.response.BetResponse response = lithium.service.casino.client.objects.response.BetResponse.builder()
                    .extSystemTransactionId("1")
                    .balanceCents(balance.toCents())
                    .build();

            return response;
        });

        when(casinoClient.handleBetRequestV2(any(lithium.service.casino.client.objects.request.BetRequest.class), anyString())).then(invocation -> {
            log.debug(invocation.toString());

            lithium.service.casino.client.objects.request.BetRequest request = invocation.getArgument(0, lithium.service.casino.client.objects.request.BetRequest.class);

            lithium.math.CurrencyAmount balance = lithium.math.CurrencyAmount.fromCents(startingBalance.toCents());
            if (request.getWin() != null) {
                balance.addCents(request.getWin());
            }
            if (request.getBet() != null) {
                balance.subtractCents(request.getBet());
            }

            if ((request.getBet() != null) && (request.getBet() == 7770L)) {
                return Response.<lithium.service.casino.client.objects.response.BetResponse>builder().status(Response.Status.CUSTOM.id(472)).message("Not allowed to Transact").build();
            }

            lithium.service.casino.client.objects.response.BetResponse response = lithium.service.casino.client.objects.response.BetResponse.builder()
                .extSystemTransactionId("1")
                .balanceCents(balance.toCents())
                .build();

            return Response.<lithium.service.casino.client.objects.response.BetResponse>builder().data(response).status(Response.Status.OK).build();
        });

        when(betRepository.findByBetTransactionId(any())).thenReturn(
            Bet.builder()
            .build());

        when(betRepository.save(any(Bet.class))).thenReturn(
            Bet.builder()
            .build()
        );
    }

//    public BetResponse placement(double amount) throws Exception {
//        SW.storeInThread(new StopWatch("test"));
//        BetRequest placementRequest = new BetRequest();
//        PlacementRequestBet betRequest = new PlacementRequestBet();
//
//        betRequest.setBetTransactionId("BETTRANSACTIONID1");
//        betRequest.setMaxPotentialWin(100.00);
//        betRequest.setTotalOdds(100.00);
//        betRequest.setTotalStake(amount);
//        betRequest.setTransactionTimestamp(DateTime.parse("2010-06-30T01:20+00:00").getMillis());
//        betRequest.setEvents(new ArrayList<>());
//        betRequest.getEvents().add(PlacementRequestEvent.builder()
//                .build());
//
//        placementRequest.setSha256("e98554564cfbf8d6e9cc70b55d833185be7e0954285fe8c8aad474a2445a19cd");
//        placementRequest.setBets(Collections.singletonList(betRequest));
//
//        return placementController.placement(Locale.US.getISO3Language(), placementRequest, null);
//    }
//
//    public SettlementResponse settlement(double amount) throws Exception {
//        SW.storeInThread(new StopWatch("test"));
//        BetResultRequest request = new BetResultRequest();
//        request.setSettlementTransactionId("SETTLEMENTTRANSACTIONID1");
//        request.setBetTransactionId("BETTRANSACTIONID1");
//        request.setSelections(new ArrayList<>());
//        request.setResult("WIN");
//        request.setCurrencyCode(currency.getCode());
//        request.setReturns(amount);
//        request.setSha256("67f73ceb73f7d96bd54aaedbdd74d7336d8dd02430073eebf1e269830ab607d8");
//        return settlementService.settle(request);
//    }

//    // See LIVESCORE-303
//    @Test
//    public void testPlacementAmountSixtyNinePointFourtyNine() throws Exception {
//        BetResponse response = placement(69.49);
//        BigDecimal balance = BigDecimal.valueOf(response.getBalance());
//        BigDecimal diff = startingBalance.toAmount().subtract(balance);
//        assertEquals("Adjustment", 69.49, diff.doubleValue(), 0);
//    }
//
//    @Test
//    public void testSettlementAmountSixtyNinePointFourtyNine() throws Exception {
//        SettlementResponse response = settlement(69.49);
//        BigDecimal balance = BigDecimal.valueOf(response.getPlayerBalance());
//        BigDecimal diff = balance.subtract(startingBalance.toAmount());
//        assertEquals("Adjustment", 69.49, diff.doubleValue(), 0);
//    }
//
//    @Test(expected = Status472NotAllowedToTransactException.class)
//    public void tesPlayerAllowedToTransact() throws Exception {
//        BetResponse response = placement(77.7);
//        BigDecimal balance = BigDecimal.valueOf(response.getBalance());
//        BigDecimal diff = startingBalance.toAmount().subtract(balance);
//        assertEquals("Adjustment", 77.7, diff.doubleValue(), 0);
//    }

//    @Test
//    public void testPlacementAmounts() throws Exception {
//        for (int amount = 0; amount < 100; amount++) {
//            for (int decimal1 = 0; decimal1 < 10; decimal1++) {
//                for (int decimal2 = 0; decimal2 < 10; decimal2++) {
//                    Double amountDouble = Double.parseDouble(amount + "." + decimal1 + decimal2);
//                    log.info(amountDouble.toString());
//                    PlacementResponse response = placement(amountDouble);
//                    assertEquals("Adjustment", amountDouble,
//                            startingBalance.toAmount().subtract(BigDecimal.valueOf(response.getBalance())).doubleValue(), 0);
//                }
//            }
//        }
//    }
//
//    @Test
//    public void testSettlementAmounts() throws Exception {
//        for (int amount = 1; amount < 100; amount++) {
//            for (int decimal1 = 0; decimal1 < 10; decimal1++) {
//                for (int decimal2 = 0; decimal2 < 10; decimal2++) {
//                    Double amountDouble = Double.parseDouble(amount + "." + decimal1 + decimal2);
//                    log.info(amountDouble.toString());
//                    SettlementResponse response = settlement(amountDouble);
//                    assertEquals("Adjustment", amountDouble,
//                            BigDecimal.valueOf(response.getPlayerBalance()).subtract(startingBalance.toAmount()).doubleValue(), 0);
//                }
//            }
//        }
//    }

}