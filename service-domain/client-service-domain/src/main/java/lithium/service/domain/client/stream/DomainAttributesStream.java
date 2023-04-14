package lithium.service.domain.client.stream;

import lithium.service.domain.client.objects.DomainAttributesData;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainAttributesStream {
    private final DomainAttributesOutputQueue domainAttributesOutputQueue;

    public void process(DomainAttributesData data) {
        domainAttributesOutputQueue.channel().send(MessageBuilder.<DomainAttributesData>withPayload(data).build());
    }
}
