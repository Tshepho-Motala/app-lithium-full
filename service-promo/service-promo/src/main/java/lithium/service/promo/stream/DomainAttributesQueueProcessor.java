package lithium.service.promo.stream;


import lithium.service.domain.client.objects.DomainAttributesData;
import lithium.service.promo.services.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@EnableBinding(DomainAttributesQueueSink.class)
@Slf4j
public class DomainAttributesQueueProcessor
{
    private final DomainService domainService;
    @StreamListener(DomainAttributesQueueSink.INPUT)
    public void processDomainUpdates(DomainAttributesData data) {
        log.debug("Received event to update domain, {}", data);
        domainService.update(data);
    }
}