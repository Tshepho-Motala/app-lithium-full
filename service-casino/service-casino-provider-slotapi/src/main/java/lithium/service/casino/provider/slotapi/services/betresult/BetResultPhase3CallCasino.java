package lithium.service.casino.provider.slotapi.services.betresult;

import lithium.exceptions.ErrorCodeException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.context.BetResultContext;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultRepository;
import lithium.math.CurrencyAmount;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BetResultPhase3CallCasino {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetResultRepository betResultRepository;

    public void callCasino(
        BetResultContext context,
        BetResultRequest request
    ) throws
        Status422DataValidationError,
        Status500UnhandledCasinoClientException
    {
        SW.start("betresult.casino.handle." + context.getBetResult().getBetResultTransactionId());

        try {
            long returns = CurrencyAmount.fromAmount(context.getBetResult().getReturns()).toCents();

            CasinoTranType tranType;

            switch (request.getKind()) {
                case WIN: tranType = CasinoTranType.CASINO_WIN; break;
                case LOSS: tranType = CasinoTranType.CASINO_LOSS; break;
                case VOID: tranType = CasinoTranType.CASINO_VOID; break;
                case FREE_WIN: tranType = CasinoTranType.CASINO_WIN_FREESPIN; break;
                case FREE_LOSS: tranType = CasinoTranType.CASINO_LOSS_FREESPIN; break;
                default: throw new Status422DataValidationError("Unhandled bet result kind: " + request.getKind());
            }

            BetResponse response = casinoService.handleSettle(
                context.getDomainName(),
                request.getCurrencyCode(),
                tranType,
                moduleInfo.getModuleName(),
                returns,
                context.getBetRound().getGuid() + ":" + context.getBetResult().getBetResultTransactionId(),
                context.getBetRound().getUser().getGuid(),
                context.getBetRound().getGame().getGuid(),
                context.getBetRound().getGuid(),
                null,
                null,
                true,
                request.getBetResultTransactionId(),
                request.getRoundId(),
                request.isRoundComplete(),
                true,
                request.getSequenceNumber(),
                request.getReturns(),
                request.getTransactionTimestamp(),
                context.getBetRound().getSessionId()
            );

            log.debug("Response from casino " + response);

            // TODO Why is this a string?
            context.getBetResult().setLithiumAccountingId(Long.parseLong(response.getExtSystemTransactionId()));
            context.getBetResult().setBalanceAfter(CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue());

        } catch (ErrorCodeException ece) {
            throw ece;
        } finally {
            SW.stop();
            SW.start("betresult.casino.storeresult." + context.getBetResult().getBetResultTransactionId());
            betResultRepository.save(context.getBetResult());
            SW.stop();
        }
    }
}
