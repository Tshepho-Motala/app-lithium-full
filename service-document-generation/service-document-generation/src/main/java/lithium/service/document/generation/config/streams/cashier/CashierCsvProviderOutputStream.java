package lithium.service.document.generation.config.streams.cashier;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CashierCsvProviderOutputStream implements CsvGenerationOutputStream {

    private CashierTransactionsCsvGenerationOutputQueue queue;

    @Override
    public CsvProvider getProvider() {
        return CsvProvider.CASHIER_TRANSACTION;
    }

    @Override
    public MessageChannel getChannel() {
        return queue.getChannel();
    }
}
