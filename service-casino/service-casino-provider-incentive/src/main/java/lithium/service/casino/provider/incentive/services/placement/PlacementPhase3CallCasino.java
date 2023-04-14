package lithium.service.casino.provider.incentive.services.placement;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.services.PubSubVirtualService;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.Placement;
import lithium.service.casino.provider.incentive.storage.repositories.BetRepository;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.math.CurrencyAmount;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlacementPhase3CallCasino {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    PubSubVirtualService pubSubVirtualService;

    public double callAccounting(
        Placement placement
    ) throws
        Status405UserDisabledException,
        Status423InvalidBonusTokenException,
        Status424InvalidBonusTokenStateException,
        Status438PlayTimeLimitReachedException,
        Status471InsufficientFundsException,
        Status473DomainBettingDisabledException,
        Status474DomainProviderDisabledException,
        Status478TimeSlotLimitException,
        Status484WeeklyLossLimitReachedException,
        Status485WeeklyWinLimitReachedException,
        Status490SoftSelfExclusionException,
        Status491PermanentSelfExclusionException,
        Status492DailyLossLimitReachedException,
        Status493MonthlyLossLimitReachedException,
        Status494DailyWinLimitReachedException,
        Status495MonthlyWinLimitReachedException,
        Status496PlayerCoolingOffException,
        Status500UnhandledCasinoClientException
    {
        double balance = 0.0;

        for (Bet bet : placement.getBets()) {

            SW.start("placement.casino.bet." + bet.getBetTransactionId());

            try {
                BetResponse response = casinoService.handleBet(placement.getDomain().getName(),
                    placement.getCurrency().getCode(),
                    bet.getVirtualCoinId() == null ? CasinoTranType.VIRTUAL_BET : CasinoTranType.VIRTUAL_FREE_BET,
                    moduleInfo.getModuleName(),
                    CurrencyAmount.fromAmount(bet.getTotalStake()).toCents(),
                    bet.getBetTransactionId(),
                    placement.getUser().getGuid(),
                    "virtual",
                    placement.getId().toString(),
                    null,
                    bet.getVirtualCoinId(),
                    placement.getSessionId()
                );

                log.debug("BetResponse from casino " + response);
                balance = CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue();

                // TODO Why is this a string?
                bet.setLithiumAccountingId(Long.parseLong(response.getExtSystemTransactionId()));

            } catch (ErrorCodeException ece) {
                bet.setErrorCode(ece.getCode());
                bet.setErrorMessage(ece.getMessage());
                throw ece;
            } finally {
                SW.stop();
                SW.start("placement.casino.storeresult." + bet.getBetTransactionId());
                betRepository.save(bet);
                pubSubVirtualService.buildPubSubVirtualPlacementMessage(bet);
                SW.stop();
            }
        }
        return balance;
    }
}
