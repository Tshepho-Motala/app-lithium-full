package lithium.service.promo.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DomainAttributesQueueSink {
    String INPUT = "domain-attributes-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
