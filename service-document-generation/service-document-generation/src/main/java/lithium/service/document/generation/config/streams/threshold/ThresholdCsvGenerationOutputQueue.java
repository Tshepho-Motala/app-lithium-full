package lithium.service.document.generation.config.streams.threshold;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ThresholdCsvGenerationOutputQueue {
    @Output("service-csv-provider-threshold-output")
    MessageChannel getChannel();
}
