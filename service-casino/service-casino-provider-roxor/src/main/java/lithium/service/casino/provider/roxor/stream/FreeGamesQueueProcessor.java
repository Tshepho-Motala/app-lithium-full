package lithium.service.casino.provider.roxor.stream;

import lithium.service.casino.client.objects.FreeGamePayload;
import lithium.service.casino.provider.roxor.services.ExternalGamesAvailabilityProxyService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@EnableBinding(FreeGamesQueueSink.class)
public class FreeGamesQueueProcessor {
    @Autowired
    ExternalGamesAvailabilityProxyService externalGamesAvailabilityProxyService;

    @StreamListener(FreeGamesQueueSink.INPUT)
    public void handle(FreeGamePayload freeGamePayload) throws Exception {
        log.info("DFG/MFG Game received : " + freeGamePayload);
        //Send the received DFG/MFG to the external games availability service
        externalGamesAvailabilityProxyService.doExternalGamesAvailabilityChecks(freeGamePayload.getProviderGuid() ,freeGamePayload.getUserGuid());
    }
}
