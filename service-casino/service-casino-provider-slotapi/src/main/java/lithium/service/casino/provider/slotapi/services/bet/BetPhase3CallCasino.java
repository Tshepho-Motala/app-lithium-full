package lithium.service.casino.provider.slotapi.services.bet;

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
import lithium.service.casino.provider.slotapi.api.schema.bet.BetRequestKindEnum;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.context.BetContext;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
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
public class BetPhase3CallCasino {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetRepository betRepository;

    public void callCasinos(
        BetContext context
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

        SW.start("bet.casino.bet." + context.getBet().getBetTransactionId());

        try {
            CasinoTranType tranType = CasinoTranType.CASINO_BET;

            if (context.getRequest().getKind() == BetRequestKindEnum.FREE_BET) {
                tranType = CasinoTranType.CASINO_BET_FREESPIN;
            }

            BetResponse response = casinoService.handleBet(context.getBet().getBetRound().getUser().getDomain().getName(),
                context.getBet().getCurrency().getCode(),
                tranType,
                moduleInfo.getModuleName(),
                CurrencyAmount.fromAmount(context.getBet().getAmount()).toCents(),
                context.getBet().getBetRound().getGuid() + ":" + context.getBet().getBetTransactionId(),
                context.getBet().getBetRound().getUser().getGuid(),
                context.getBet().getBetRound().getGame().getGuid(),
                context.getBet().getId().toString(),
                null,
                null,
                null,
                true,
                context.getRequest().getBetTransactionId(),
                context.getRequest().getRoundId(),
                true,
                context.getRequest().getSequenceNumber(),
                context.getRequest().getTransactionTimestamp(),
                context.getBet().getBetRound().getSessionId()
            );

            log.debug("BetResponse from casino " + response);
            balance = CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue();

            // TODO Why is this a string?
            context.getBet().setLithiumAccountingId(Long.parseLong(response.getExtSystemTransactionId()));
            context.getBet().setBalanceAfter(balance);

        } catch (ErrorCodeException ece) {
            throw ece;
        } finally {
            SW.stop();
            SW.start("bet.casino.storeresult." + context.getBet().getBetTransactionId());
            betRepository.save(context.getBet());
            SW.stop();
        }
    }
}
