package lithium.service.casino.provider.incentive.api.controllers;

import lithium.metrics.SW;
import lithium.metrics.StopWatch;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequest;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequestBet;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequestEvent;
import lithium.service.casino.provider.incentive.api.schema.placement.response.PlacementResponse;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementResponse;
import lithium.service.casino.provider.incentive.config.ProviderConfig;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.services.EventService;
import lithium.service.casino.provider.incentive.services.PlacementService;
import lithium.service.casino.provider.incentive.services.PubSubVirtualService;
import lithium.service.casino.provider.incentive.services.SettlementService;
import lithium.service.casino.provider.incentive.services.placement.PlacementPhase1Validate;
import lithium.service.casino.provider.incentive.services.placement.PlacementPhase2Persist;
import lithium.service.casino.provider.incentive.services.placement.PlacementPhase3CallCasino;
import lithium.service.casino.provider.incentive.services.settlement.SettlementPhase1Validate;
import lithium.service.casino.provider.incentive.services.settlement.SettlementPhase2Persist;
import lithium.service.casino.provider.incentive.services.settlement.SettlementPhase3CallCasino;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.Currency;
import lithium.service.casino.provider.incentive.storage.entities.Domain;
import lithium.service.casino.provider.incentive.storage.entities.Placement;
import lithium.service.casino.provider.incentive.storage.entities.Settlement;
import lithium.service.casino.provider.incentive.storage.entities.User;
import lithium.service.casino.provider.incentive.storage.repositories.BetRepository;
import lithium.service.casino.provider.incentive.storage.repositories.BetSelectionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.CompetitionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.incentive.storage.repositories.DomainRepository;
import lithium.service.casino.provider.incentive.storage.repositories.EventNameRepository;
import lithium.service.casino.provider.incentive.storage.repositories.IncentiveUserRepository;
import lithium.service.casino.provider.incentive.storage.repositories.MarketRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PlacementRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SelectionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SelectionResultRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SelectionTypeRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementResultRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SportRepository;
import lithium.service.casino.provider.incentive.storage.repositories.UserRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@Disabled
@Ignore
public class APITest {

    // Mocks
    ModuleInfo moduleInfo = Mockito.mock(ModuleInfo.class);
    LithiumTokenUtil tokenUtil = mock(LithiumTokenUtil.class);
    LimitInternalSystemService limitInternalSystemService = Mockito.mock(LimitInternalSystemService.class);
    LithiumTokenUtilService tokenService = Mockito.mock(LithiumTokenUtilService.class);
    ProviderConfigService configService = Mockito.mock(ProviderConfigService.class);
    BetRepository betRepository = Mockito.mock(BetRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    IncentiveUserRepository incentiveUserRepository = Mockito.mock(IncentiveUserRepository.class);
    CurrencyRepository currencyRepository = Mockito.mock(CurrencyRepository.class);
    MarketRepository marketRepository = Mockito.mock(MarketRepository.class);
    EventNameRepository eventNameRepository = Mockito.mock(EventNameRepository.class);
    PlacementRepository placementRepository = Mockito.mock(PlacementRepository.class);
    EventService eventService = Mockito.mock(EventService.class);
    DomainRepository domainRepository = Mockito.mock(DomainRepository.class);
    BetSelectionRepository betSelectionRepository = Mockito.mock(BetSelectionRepository.class);
    SportRepository sportRepository = Mockito.mock(SportRepository.class);
    CompetitionRepository competitionRepository = Mockito.mock(CompetitionRepository.class);
    SelectionTypeRepository selectionTypeRepository = Mockito.mock(SelectionTypeRepository.class);
    SelectionRepository selectionRepository = Mockito.mock(SelectionRepository.class);
    SelectionResultRepository selectionResultRepository = Mockito.mock(SelectionResultRepository.class);
    SettlementRepository settlementRepository = Mockito.mock(SettlementRepository.class);
    SettlementResultRepository settlementResultRepository = Mockito.mock(SettlementResultRepository.class);
    CachingDomainClientService cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
    LithiumServiceClientFactory lithiumServiceClientFactory = Mockito.mock(LithiumServiceClientFactory.class);
    CasinoClient casinoClient = Mockito.mock(CasinoClient.class);
    UserApiInternalClientService userApiInternalClientService = Mockito.mock(UserApiInternalClientService.class);
    ProviderClientService providerClientService = Mockito.mock(ProviderClientService.class);
    PubSubVirtualService pubSubVirtualService = Mockito.mock(PubSubVirtualService.class);
    LocaleContextProcessor localeContextProcessor = Mockito.mock(LocaleContextProcessor.class);

    // Services under test
    CasinoClientService casinoClientService = new CasinoClientService();
    PlacementController placementController = new PlacementController();
    PlacementService placementService = new PlacementService();
    PlacementPhase1Validate phase1Validate = new PlacementPhase1Validate();
    PlacementPhase2Persist phase2Persist = new PlacementPhase2Persist(
            userRepository, incentiveUserRepository, currencyRepository, marketRepository, eventService,
            placementRepository, betRepository, domainRepository, betSelectionRepository,
            sportRepository, competitionRepository, selectionTypeRepository, selectionRepository
    );
    PlacementPhase3CallCasino phase3CallCasino = new PlacementPhase3CallCasino();

    SettlementController settlementController = new SettlementController();
    SettlementService settlementService = new SettlementService();
    SettlementPhase1Validate settlementPhase1Validate = new SettlementPhase1Validate();
    SettlementPhase2Persist settlementPhase2Persist = new SettlementPhase2Persist();
    SettlementPhase3CallCasino settlementPhase3CallCasino = new SettlementPhase3CallCasino();

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
        phase3CallCasino.setPubSubVirtualService(pubSubVirtualService);

        placementService.setTokenService(tokenService);
        placementService.setModuleInfo(moduleInfo);
        placementService.setLimits(limitInternalSystemService);
        placementService.setPhase1Validate(phase1Validate);
        placementService.setPhase2Persist(phase2Persist);
        placementService.setPhase3CallCasino(phase3CallCasino);
        placementService.setUserApiInternalClientService(userApiInternalClientService);
        placementService.setCachingDomainClientService(cachingDomainClientService);
        placementService.setProviderClientService(providerClientService);
        placementController.setService(placementService);
        placementController.setLocaleContextProcessor(localeContextProcessor);

        settlementPhase1Validate.setBetRepository(betRepository);
        settlementPhase1Validate.setConfigService(configService);
        settlementPhase1Validate.setModule(moduleInfo);
        settlementPhase1Validate.setBetSelectionRepository(betSelectionRepository);

        settlementPhase2Persist.setBetSelectionRepository(betSelectionRepository);
        settlementPhase2Persist.setCurrencyRepository(currencyRepository);
        settlementPhase2Persist.setSelectionResultRepository(selectionResultRepository);
        settlementPhase2Persist.setSettlementRepository(settlementRepository);
        settlementPhase2Persist.setSettlementResultRepository(settlementResultRepository);
        settlementPhase2Persist.setBetRepository(betRepository);

        settlementPhase3CallCasino.setCasinoService(casinoClientService);
        settlementPhase3CallCasino.setConfigService(configService);
        settlementPhase3CallCasino.setModuleInfo(moduleInfo);
        settlementPhase3CallCasino.setSettlementRepository(settlementRepository);
        settlementPhase3CallCasino.setPubSubVirtualService(pubSubVirtualService);

        settlementService.setPhase1Validate(settlementPhase1Validate);
        settlementService.setPhase2Persist(settlementPhase2Persist);
        settlementService.setPhase3CallCasino(settlementPhase3CallCasino);
        settlementController.setSettlementService(settlementService);

        when(tokenService.getUtil(any())).thenReturn(tokenUtil);
        when(placementRepository.save(any(Placement.class))).thenAnswer(invocation -> {
            Placement placement = (Placement) invocation.getArguments()[0];
            placement.setId(1L);
            return placement;
        });
        when(currencyRepository.findOrCreateByCode(any(), any())).thenReturn(currency);
        when(domainRepository.findOrCreateByName(any(), any())).thenReturn(domain);
        when(userRepository.findOrCreateByGuid(any(), any())).thenReturn(User.builder().build());
        when(settlementRepository.save(any(Settlement.class))).then(invocation -> {
           Settlement settlement = invocation.getArgument(0, Settlement.class);
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

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                log.info("checkProviderEnabled called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(providerClientService).checkProviderEnabled(anyString(), anyString(), anyString());

        when(casinoClient.handleBetRequest(any())).then(invocation -> {
            log.debug(invocation.toString());

            BetRequest request = invocation.getArgument(0, BetRequest.class);

            lithium.math.CurrencyAmount balance = lithium.math.CurrencyAmount.fromCents(startingBalance.toCents());
            if (request.getWin() != null) {
                balance.addCents(request.getWin());
            }
            if (request.getBet() != null) {
                balance.subtractCents(request.getBet());
            }

            BetResponse response = BetResponse.builder()
                    .extSystemTransactionId("1")
                    .balanceCents(balance.toCents())
                    .build();

            return response;
        });

        when(casinoClient.handleBetRequestV2(any(BetRequest.class), anyString())).then(invocation -> {
            log.debug(invocation.toString());

            BetRequest request = invocation.getArgument(0, BetRequest.class);

            lithium.math.CurrencyAmount balance = lithium.math.CurrencyAmount.fromCents(startingBalance.toCents());
            if (request.getWin() != null) {
                balance.addCents(request.getWin());
            }
            if (request.getBet() != null) {
                balance.subtractCents(request.getBet());
            }

            if ((request.getBet() != null) && (request.getBet() == 7770L)) {
                return Response.<BetResponse>builder().status(Response.Status.CUSTOM.id(472)).message("Not allowed to Transact").build();
            }

            BetResponse response = BetResponse.builder()
                .extSystemTransactionId("1")
                .balanceCents(balance.toCents())
                .build();

            return Response.<BetResponse>builder().data(response).status(Response.Status.OK).build();
        });

        when(casinoClient.handleSettleRequestV2(any(BetRequest.class), anyString())).then(invocation -> {
            log.debug(invocation.toString());

            BetRequest request = invocation.getArgument(0, BetRequest.class);

            lithium.math.CurrencyAmount balance = lithium.math.CurrencyAmount.fromCents(startingBalance.toCents());
            if (request.getWin() != null) {
                balance.addCents(request.getWin());
            }
            if (request.getBet() != null) {
                balance.subtractCents(request.getBet());
            }

            if ((request.getBet() != null) && (request.getBet() == 7770L)) {
                return Response.<BetResponse>builder().status(Response.Status.CUSTOM.id(472)).message("Not allowed to Transact").build();
            }

            BetResponse response = BetResponse.builder()
                .extSystemTransactionId("1")
                .balanceCents(balance.toCents())
                .build();

            return Response.<BetResponse>builder().data(response).status(Response.Status.OK).build();
        });

        when(betRepository.findByBetTransactionId(any())).thenReturn(
                Bet.builder()
                        .placement(Placement.builder()
                                .user(User.builder().build())
                                .domain(domain)
                            .build())
                        .build());

        when(betRepository.save(any(Bet.class))).thenReturn(
            Bet.builder()
            .placement(
                Placement.builder()
                .user(User.builder().build())
                .domain(domain)
                .build()
            )
            .build()
        );
    }

    public PlacementResponse placement(double amount) throws Exception {
        SW.storeInThread(new StopWatch("test"));
        PlacementRequest placementRequest = new PlacementRequest();
        PlacementRequestBet betRequest = new PlacementRequestBet();

        betRequest.setBetTransactionId("BETTRANSACTIONID1");
        betRequest.setMaxPotentialWin(100.00);
        betRequest.setTotalOdds(100.00);
        betRequest.setTotalStake(amount);
        betRequest.setTransactionTimestamp(DateTime.parse("2010-06-30T01:20+00:00").getMillis());
        betRequest.setEvents(new ArrayList<>());
        betRequest.getEvents().add(PlacementRequestEvent.builder()
                .build());

        placementRequest.setSha256("e98554564cfbf8d6e9cc70b55d833185be7e0954285fe8c8aad474a2445a19cd");
        placementRequest.setBets(Collections.singletonList(betRequest));

        return placementController.placement("en", placementRequest, null);
    }

    public SettlementResponse settlement(double amount) throws Exception {
        SW.storeInThread(new StopWatch("test"));
        SettlementRequest request = new SettlementRequest();
        request.setSettlementTransactionId("SETTLEMENTTRANSACTIONID1");
        request.setBetTransactionId("BETTRANSACTIONID1");
        request.setSelections(new ArrayList<>());
        request.setResult("WIN");
        request.setCurrencyCode(currency.getCode());
        request.setReturns(amount);
        request.setSha256("67f73ceb73f7d96bd54aaedbdd74d7336d8dd02430073eebf1e269830ab607d8");
        return settlementService.settle(request);
    }

    // See LIVESCORE-303
    @Test
    public void testPlacementAmountSixtyNinePointFourtyNine() throws Exception {
        PlacementResponse response = placement(69.49);
        BigDecimal balance = response.getBalance().toAmount();
        BigDecimal diff = startingBalance.toAmount().subtract(balance);
        assertEquals("Adjustment", 69.49, diff.doubleValue(), 0);
    }

    @Test
    public void testSettlementAmountSixtyNinePointFourtyNine() throws Exception {
        SettlementResponse response = settlement(69.49);
        BigDecimal balance = response.getPlayerBalance().toAmount();
        BigDecimal diff = balance.subtract(startingBalance.toAmount());
        assertEquals("Adjustment", 69.49, diff.doubleValue(), 0);
    }

    @Test(expected = Status500UnhandledCasinoClientException.class)
    public void tesPlayerAllowedToTransact() throws Exception {
        PlacementResponse response = placement(77.7);
        BigDecimal balance = response.getBalance().toAmount();
        BigDecimal diff = startingBalance.toAmount().subtract(balance);
        assertEquals("Adjustment", 77.7, diff.doubleValue(), 0);
    }

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
