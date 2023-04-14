package lithium.service.casino.provider.sportsbook.services;


import lithium.service.casino.CasinoTranType;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveRequest;
import lithium.service.casino.provider.sportsbook.context.SettleCreditContext;
import lithium.service.casino.provider.sportsbook.context.SettleDebitContext;
import lithium.service.casino.provider.sportsbook.context.SettleMultiContext;
import lithium.service.casino.provider.sportsbook.data.PubSubBetPlacementMessage;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementEntry;
import lithium.service.casino.provider.sportsbook.storage.repositories.BetRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lithium.service.datafeed.provider.google.service.EnablePubSubExchangeStream;
import lithium.service.datafeed.provider.google.service.ServicePubSubStream;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static lithium.service.casino.CasinoTranType.SPORTS_BET;
import static lithium.service.domain.client.DomainSettings.PUB_SUB_SPORTSBOOK;

@Slf4j
@Service
@EnablePubSubExchangeStream
@AllArgsConstructor
public class PubSubBetService {
    public static final String PRODUCT_NAME = "sportsbook";
    private final ServicePubSubStream servicePubSubStream;
    @Autowired
    LithiumServiceClientFactory factory;
    @Autowired
    private BetRepository betRepository;
    private final CachingDomainClientService cachingDomainClientService;

    public void buildAndSendPubSubBetReserveMessage(BetReserveRequest betReserveRequest, long userId){

        PubSubBetPlacementMessage message = PubSubBetPlacementMessage.builder()
                .accountGuid(betReserveRequest.getGuid())
                .eventType(SPORTS_BET)
                .accountId(String.valueOf(userId))
                .product(PRODUCT_NAME)
                .value(betReserveRequest.getAmount())
                .reserveId(betReserveRequest.getReserveId())
                .build();
        try {
            addMessageToQueue(message,betReserveRequest.getGuid().split("/")[0]);
            log.info("Message "+ message +" sent to pup sub channel" );
        } catch (PubSubInternalErrorException e) {
            log.error("can not send a pub-sub message to google service" + e.getMessage());
        }

    }

    public void buildAndSendPubSubCreditSettlementMessage(SettleCreditContext context){

        CasinoTranType tranType = CasinoTranType.SPORTS_WIN;
        long returns = CurrencyAmount.fromAmount(context.getRequest().getAmount()).toCents();
        boolean free = context.getBet().getAmount() == 0;
        if (returns > 0) tranType = (free)? CasinoTranType.SPORTS_FREE_LOSS: CasinoTranType.SPORTS_LOSS;
        Bet bet = betRepository.findByBetId(context.getRequest().getBetId());
        PubSubBetPlacementMessage message = PubSubBetPlacementMessage.builder()
                .accountGuid(context.getUser().getGuid())
                .accountId(String.valueOf(context.getUser().getId()))
                .eventType(tranType)
                .product(PRODUCT_NAME)
                .value(context.getBet().getAmount())
                .betId(bet.getBetId())
                .timestamp(context.getRequest().getTimestamp())
                .build();
        try {
            addMessageToQueue(message,context.getDomain().getName());
            log.info("Message "+ message +" sent to pup sub channel" );
        } catch (PubSubInternalErrorException e) {
            log.error("can not send a pub-sub message to google service" + e.getMessage());
        }

    }

    public void buildAndSendPubSubSettlementDebitMessage(SettleDebitContext context) {

        CasinoTranType tranType = CasinoTranType.SPORTS_WIN;
        boolean free = context.getSettlementDebit().getAmount() == 0;
        long returns = CurrencyAmount.fromAmount(context.getRequest().getAmount()).toCents();
        if (returns == 0) tranType = (free)? CasinoTranType.SPORTS_FREE_WIN: CasinoTranType.SPORTS_WIN;
        Bet bet = betRepository.findByRequestId(context.getRequest().getRequestId());
        PubSubBetPlacementMessage message = PubSubBetPlacementMessage.builder()
                .accountGuid(context.getUser().getGuid())
                .eventType(tranType)
                .accountId(String.valueOf(context.getUser().getId()))
                .product(PRODUCT_NAME)
                .value(context.getSettlementDebit().getAmount())
                .betId(bet.getBetId())
                .timestamp(context.getRequest().getTimestamp())
                .build();
        try {
            addMessageToQueue(message,context.getDomain().getName());
            log.info("Message "+ message +" sent to pup sub channel" );
        } catch (PubSubInternalErrorException e) {
            log.error("can not send a pub-sub message to google service" + e.getMessage());
        }
    }

    public void addMessageToQueue(PubSubBetPlacementMessage message, String domainName) throws PubSubInternalErrorException {
        servicePubSubStream.processSportsBookChange(
                PubSubMessage
                        .builder()
                        .timestamp(new Date().getTime())
                        .data(message)
                        .dataType(DataType.SPORTSBOOK_TRANSACTIONS)
                        .eventType(message.getEventType().name())
                        .domainName(domainName)
                        .build()
        );
    }

    public boolean isChannelActivated(String domainName) {
        Domain domain = null;
        try {
            domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        } catch (Status550ServiceDomainClientException e) {
            log.error("can't find domain from cachingDomainClientService" + e.getMessage());
            return false;
        }
        Optional<String> labelValue = domain.findDomainSettingByName(PUB_SUB_SPORTSBOOK.key());
        String result = labelValue.orElse(PUB_SUB_SPORTSBOOK.defaultValue());
        return result.equalsIgnoreCase("true");
    }

    public void buildMultipleSettlementsMessages(SettleMultiContext context) {
        for (SettlementEntry entry : context.getSettlement().getSettlementEntries()){
            Bet bet = betRepository.findByBetId(entry.getBet().getBetId());
            PubSubBetPlacementMessage message = PubSubBetPlacementMessage.builder()
                    .accountGuid(context.getUser().getGuid())
                    .eventType(resolveCasinoSportsType(entry))
                    .accountId(String.valueOf(entry.getBet().getUser().getId()))
                    .product(PRODUCT_NAME)
                    .value(entry.getBet().getAmount())
                    .reserveId(entry.getBet().getReservation().getReserveId())
                    .betId(bet.getBetId())
                    .timestamp(context.getRequest().getTimestamp())
                    .build();
            try {
                addMessageToQueue(message,context.getDomain().getName());
                log.info("Message "+ message +" sent to pup sub channel" );
            } catch (PubSubInternalErrorException e) {
                log.error("can not send a pub-sub message to google service" + e.getMessage());
            }
        }
    }

    private CasinoTranType resolveCasinoSportsType(final SettlementEntry entry) {
        long returns = CurrencyAmount.fromAmount(entry.getAmount()).toCents();
        CasinoTranType tranType = null;
        boolean free = entry.getBet().getAmount() == 0;
        if (returns > 0) tranType = (free)? CasinoTranType.SPORTS_FREE_WIN : CasinoTranType.SPORTS_WIN;
        if (returns < 0) tranType = (free)? CasinoTranType.SPORTS_FREE_RESETTLEMENT: CasinoTranType.SPORTS_RESETTLEMENT;
        if (returns == 0) tranType = (free)? CasinoTranType.SPORTS_FREE_LOSS: CasinoTranType.SPORTS_LOSS;
        return tranType;
    }
}
