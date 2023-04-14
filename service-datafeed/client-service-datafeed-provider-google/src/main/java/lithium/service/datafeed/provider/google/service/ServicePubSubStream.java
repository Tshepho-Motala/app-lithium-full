package lithium.service.datafeed.provider.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ServicePubSubStream {
    private final PubSubExchangeQueue pubSubExchangeQueue;

    public void processUserChange(PubSubMessage change) throws PubSubInternalErrorException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            pubSubExchangeQueue.channelUserChange().send(MessageBuilder.withPayload((mapper).writeValueAsString(change)).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        } catch (JsonProcessingException e) {
            throw new PubSubInternalErrorException(e.getLocalizedMessage());
        }
    }

    public void processSportsBookChange(PubSubMessage change) throws PubSubInternalErrorException {
        try {
            pubSubExchangeQueue.channelSportsbookChange().send(MessageBuilder.withPayload((new ObjectMapper()).writeValueAsString(change)).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        } catch (JsonProcessingException e) {
            throw new PubSubInternalErrorException(e.getLocalizedMessage());
        }
    }

    public void processWalletChange(PubSubMessage change) throws PubSubInternalErrorException {
        try {
            pubSubExchangeQueue.channelWalletChange().send(MessageBuilder.withPayload((new ObjectMapper()).writeValueAsString(change)).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        } catch (JsonProcessingException e) {
            throw new PubSubInternalErrorException(e.getLocalizedMessage());
        }
    }

    public void processVirtualChange(PubSubMessage change) throws PubSubInternalErrorException {
        try {
            pubSubExchangeQueue.channelVirtualChange().send(MessageBuilder.withPayload((new ObjectMapper()).writeValueAsString(change)).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        } catch (JsonProcessingException e) {
            throw new PubSubInternalErrorException(e.getLocalizedMessage());
        }
    }

    public void processCasinoChange(PubSubMessage change) throws PubSubInternalErrorException {
        try {
            pubSubExchangeQueue.channelCasinoChange().send(MessageBuilder.withPayload((new ObjectMapper()).writeValueAsString(change)).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        } catch (JsonProcessingException e) {
            throw new PubSubInternalErrorException(e.getLocalizedMessage());
        }
    }

    public void processAccountLinkChange(PubSubMessage change) throws PubSubInternalErrorException {
        try {
            pubSubExchangeQueue.channelAccountLincChange().send(MessageBuilder.withPayload((new ObjectMapper()).writeValueAsString(change)).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        } catch (JsonProcessingException e) {
            throw new PubSubInternalErrorException(e.getLocalizedMessage());
        }
    }

    public void processMarketingPreferences(PubSubMessage message) throws PubSubInternalErrorException {
        try {
            pubSubExchangeQueue.channelMarketingPreferences().send(MessageBuilder.withPayload((new ObjectMapper()).writeValueAsString(message)).build());
        } catch (JsonProcessingException ex) {
            throw new PubSubInternalErrorException(ex.getMessage());
        }
    }
}
