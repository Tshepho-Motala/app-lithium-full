package lithium.csv.provider.threshold.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CsvGenerationProcessingQueueSink {
    String INPUT = "service-csv-provider-threshold-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
