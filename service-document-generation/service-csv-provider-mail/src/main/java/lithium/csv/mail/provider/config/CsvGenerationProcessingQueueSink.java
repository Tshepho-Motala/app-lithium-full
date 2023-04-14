package lithium.csv.mail.provider.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CsvGenerationProcessingQueueSink {
    String INPUT = "service-csv-provider-mail-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
