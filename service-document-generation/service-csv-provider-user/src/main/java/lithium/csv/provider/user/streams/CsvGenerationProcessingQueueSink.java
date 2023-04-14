package lithium.csv.provider.user.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CsvGenerationProcessingQueueSink {
    String INPUT = "service-csv-provider-user-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
