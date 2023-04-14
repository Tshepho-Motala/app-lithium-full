package lithium.service.domain.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DomainAttributesOutputQueue {
    @Output("domain-attributes-output")
    MessageChannel channel();
}
