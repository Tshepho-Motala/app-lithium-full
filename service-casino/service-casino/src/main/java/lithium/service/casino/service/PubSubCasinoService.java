package lithium.service.casino.service;

import lithium.service.casino.data.entities.Bet;
import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.pubsub.PubSubCasinoBetMessage;
import lithium.service.casino.pubsub.PubSubCasinoMessage;
import lithium.service.casino.pubsub.PubSubCasinoSettlementMessage;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lithium.service.datafeed.provider.google.service.EnablePubSubExchangeStream;
import lithium.service.datafeed.provider.google.service.ServicePubSubStream;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;


@Slf4j
@Service
@EnablePubSubExchangeStream
@AllArgsConstructor
public class PubSubCasinoService {

    private final ServicePubSubStream servicePubSubStream;
    public static final String PRODUCT_NAME = "casino";
    private final static String BET_PLACEMENT = "Bet Placement";
    private final CachingDomainClientService cachingDomainClientService;

    public boolean isPubSubChannelActivated(String domainName) {
        Domain domain = null;
        try {
            domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        } catch (Status550ServiceDomainClientException e) {
            log.error("can't find domain from cachingDomainClientService" + e.getMessage());
            return false;
        }
        Optional<String> labelValue = domain.findDomainSettingByName(
            DomainSettings.PUB_SUB_CASINO.key());
        String result = labelValue.orElse(DomainSettings.PUB_SUB_CASINO.defaultValue());
        return result.equalsIgnoreCase("true");
    }

    public void buildPubSubCasinoPlacementMessage(String domainName, Bet bet) {
        if (isPubSubChannelActivated(domainName)) {
            PubSubCasinoBetMessage message = PubSubCasinoBetMessage.builder()
                .accountGuid(bet.getBetRound().getUser().getGuid())
                .eventType(BET_PLACEMENT)
                .product(PRODUCT_NAME)
                .value(bet.getAmount())
                .betId(bet.getId())
                .betKind(bet.getKind().getCode())
                .createdDate(bet.getCreatedDate())
                .build();
            try {
                addMessageToQueue(message, domainName);
                log.trace("Message " + message + " sent to pup sub channel");
            } catch (PubSubInternalErrorException e) {
                log.error("can not send a pub-sub message to google service" + e.getMessage());
            }
        }
    }
    public void buildPubSubCasinoSettlementMessage(String domainName, BetResult betResult) {
        if (isPubSubChannelActivated(domainName)) {
            PubSubCasinoSettlementMessage message = PubSubCasinoSettlementMessage.builder()
                .accountGuid(betResult.getBetRound().getUser().getGuid())
                .eventType(betResult.getBetResultKind().getCode())
                .product(PRODUCT_NAME)
                .value(betResult.getReturns())
                .betId(betResult.getId())
                .betKind(betResult.getBetResultKind().getCode())
                .createdDate(betResult.getCreatedDate())
                .build();
            try {
                addMessageToQueue(message, domainName);
                log.trace("Message " + message + " sent to pup sub channel");
            } catch (PubSubInternalErrorException e) {
                log.error("can not send a pub-sub message to google service" + e.getMessage());
            }
        }
    }

    private void addMessageToQueue(PubSubCasinoMessage message, String domainName)
        throws PubSubInternalErrorException {
        servicePubSubStream.processCasinoChange(PubSubMessage.builder()
            .timestamp(new Date().getTime())
            .data(message)
            .dataType(DataType.CASINO_TRANSACTIONS)
            .domainName(domainName)
            .build());
    }

}

