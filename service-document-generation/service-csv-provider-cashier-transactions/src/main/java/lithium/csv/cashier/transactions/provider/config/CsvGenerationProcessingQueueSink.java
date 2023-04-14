package lithium.csv.cashier.transactions.provider.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CsvGenerationProcessingQueueSink {
    String INPUT = "service-csv-provider-cashier-transactions-input";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
