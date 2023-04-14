package lithium.service.casino.provider.incentive.services.settlement;

import lithium.exceptions.ErrorCodeException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementResultEnum;
import lithium.service.casino.provider.incentive.config.ProviderConfigService;
import lithium.service.casino.provider.incentive.context.SettlementContext;
import lithium.service.casino.provider.incentive.services.PubSubVirtualService;
import lithium.service.casino.provider.incentive.storage.repositories.SettlementRepository;
import lithium.math.CurrencyAmount;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SettlementPhase3CallCasino {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    SettlementRepository settlementRepository;

    @Autowired @Setter
    PubSubVirtualService pubSubVirtualService;

    public void callCasino(
        SettlementContext context,
        SettlementRequest request
    ) throws
        Status500UnhandledCasinoClientException
    {
        SW.start("settlement.casino.handle." + context.getSettlement().getSettlementTransactionId());
        try {
            long returns = CurrencyAmount.fromAmount(context.getSettlement().getReturns()).toCents();

            CasinoTranType tranType = CasinoTranType.VIRTUAL_WIN;
            if (context.getBet().getVirtualCoinId() != null) {
                tranType = CasinoTranType.VIRTUAL_FREE_WIN;
            }
            if (context.getResult() == SettlementResultEnum.LOST) {
                tranType = CasinoTranType.VIRTUAL_LOSS;
                if (context.getBet().getVirtualCoinId() != null) {
                    tranType = CasinoTranType.VIRTUAL_FREE_LOSS;
                }
            }
            if (context.getResult() == SettlementResultEnum.VOID) {
                tranType = CasinoTranType.VIRTUAL_BET_VOID;
                if (context.getBet().getVirtualCoinId() != null) {
                    tranType = CasinoTranType.VIRTUAL_FREE_BET_VOID;
                }
            }

            BetResponse response = casinoService.handleSettle(
                context.getDomainName(),
                request.getCurrencyCode(),
                tranType,
                moduleInfo.getModuleName(),
                returns,
                context.getBet().getBetTransactionId() + ":" + context.getSettlement().getSettlementTransactionId(),
                context.getBet().getPlacement().getUser().getGuid(),
                "virtual",
                context.getSettlement().getId().toString(),
                context.getBet().getLithiumAccountingId(),
                context.getBet().getVirtualCoinId(),
                context.getBet().getPlacement().getSessionId()
            );

            log.debug("Response from casino " + response);
            context.setBalance(CurrencyAmount.fromCents(response.getBalanceCents()));

            // TODO Why is this a string?
            context.getSettlement().setLithiumAccountingId(Long.parseLong(response.getExtSystemTransactionId()));
        } catch (ErrorCodeException ece) {
            context.getSettlement().setErrorCode(ece.getCode());
            context.getSettlement().setErrorMessage(ece.getMessage());
            throw ece;
        } finally {
            SW.stop();
            SW.start("settlement.casino.storeresult." + context.getSettlement().getSettlementTransactionId());
            settlementRepository.save(context.getSettlement());
            pubSubVirtualService.buildPubSubVirtualSettlementMessage(context);
            SW.stop();
        }
    }
}
