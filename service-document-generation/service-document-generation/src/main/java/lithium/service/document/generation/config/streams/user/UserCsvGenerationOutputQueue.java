package lithium.service.document.generation.config.streams.user;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserCsvGenerationOutputQueue {
    @Output("service-csv-provider-user-output")
    MessageChannel getChannel();
}
