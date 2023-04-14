package lithium.service.document.generation.config.streams.cashier;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CashierTransactionsCsvGenerationOutputQueue {
    @Output("service-csv-provider-cashier-transactions-output")
    MessageChannel getChannel();
}
