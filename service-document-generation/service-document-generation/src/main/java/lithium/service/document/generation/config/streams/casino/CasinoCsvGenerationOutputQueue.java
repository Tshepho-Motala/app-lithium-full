package lithium.service.document.generation.config.streams.casino;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CasinoCsvGenerationOutputQueue {
    @Output("service-csv-provider-casino-output")
    MessageChannel getChannel();
}
