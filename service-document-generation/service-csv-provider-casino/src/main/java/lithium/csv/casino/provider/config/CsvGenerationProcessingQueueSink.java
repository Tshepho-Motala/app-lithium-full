package lithium.csv.casino.provider.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CsvGenerationProcessingQueueSink {
    String INPUT = "service-csv-provider-casino-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
