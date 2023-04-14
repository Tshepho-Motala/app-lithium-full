package lithium.service.document.generation.config.streams.threshold;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ThresholdCsvProviderOutputStream implements CsvGenerationOutputStream {

    private ThresholdCsvGenerationOutputQueue queue;

    @Override
    public CsvProvider getProvider() {
        return CsvProvider.THRESHOLD;
    }

    @Override
    public MessageChannel getChannel() {
        return queue.getChannel();
    }
}
