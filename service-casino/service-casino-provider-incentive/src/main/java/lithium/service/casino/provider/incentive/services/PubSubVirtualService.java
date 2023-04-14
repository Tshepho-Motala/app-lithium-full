package lithium.service.casino.provider.incentive.services;

import lithium.service.casino.provider.incentive.context.SettlementContext;
import lithium.service.casino.provider.incentive.data.PubSubVirtualBetVirtualMessage;
import lithium.service.casino.provider.incentive.data.PubSubVirtualMessage;
import lithium.service.casino.provider.incentive.data.PubSubVirtualVirtualSettlementMesage;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lithium.service.datafeed.provider.google.service.EnablePubSubExchangeStream;
import lithium.service.datafeed.provider.google.service.ServicePubSubStream;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static lithium.service.domain.client.DomainSettings.PUB_SUB_VIRTUALS;


@Slf4j
@Service
@EnablePubSubExchangeStream
@AllArgsConstructor
public class PubSubVirtualService {

    private final ServicePubSubStream servicePubSubStream;
    public static final String PRODUCT_NAME = "incentive";
    private final static String BET_PLACEMENT = "Bet Placement";
    private final CachingDomainClientService cachingDomainClientService;

    public boolean isPubSubChannelActivated (String domainName){
        Domain domain = null;
        try {
            domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        } catch (Status550ServiceDomainClientException e) {
            log.error("can't find domain from cachingDomainClientService" + e.getMessage());
            return false;
        }
        Optional<String> labelValue = domain.findDomainSettingByName(PUB_SUB_VIRTUALS.key());
        String result = labelValue.orElse(PUB_SUB_VIRTUALS.defaultValue());
        return result.equalsIgnoreCase("true");
    }

    public void buildPubSubVirtualPlacementMessage(Bet bet) {
        if (isPubSubChannelActivated(bet.getPlacement().getDomain().getName())) {
            PubSubVirtualBetVirtualMessage message = PubSubVirtualBetVirtualMessage.builder()
                    .accountGuid(bet.getPlacement().getUser().getGuid())
                    .accountId(bet.getPlacement().getUser().getId())
                    .eventType(BET_PLACEMENT)
                    .product(PRODUCT_NAME)
                    .value(bet.getTotalStake())
                    .build();
            try {
                addMessageToQueue(message,bet.getPlacement().getDomain().getName());
                log.info("Message " + message + " sent to pup sub channel");
            } catch (PubSubInternalErrorException e) {
                log.error("can not send a pub-sub message to google service" + e.getMessage());
            }
        }
    }

    public void buildPubSubVirtualSettlementMessage(SettlementContext context) {
        if (isPubSubChannelActivated(context.getDomainName())) {
            PubSubVirtualVirtualSettlementMesage message = PubSubVirtualVirtualSettlementMesage.builder()
                    .accountGuid(context.getBet().getPlacement().getUser().getGuid())
                    .eventType(context.getResult().toString())
                    .product(PRODUCT_NAME)
                    .value(context.getBalance().toAmount().doubleValue())
                    .build();
            try {
                addMessageToQueue(message, context.getDomainName());
                log.info("Message " + message + " sent to pup sub channel");
            } catch (PubSubInternalErrorException e) {
                log.error("can not send a pub-sub message to google service" + e.getMessage());
            }
        }
    }

    private void addMessageToQueue(PubSubVirtualMessage message, String domainName) throws PubSubInternalErrorException {
        servicePubSubStream.processVirtualChange(PubSubMessage.builder()
                .timestamp(new Date().getTime())
                .data(message)
                .dataType(DataType.VIRTUAL_TRANSACTIONS)
                .domainName(domainName)
                .build());
    }
}
