package lithium.service.document.generation.config.streams.casino;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CasinoCsvProviderOutputStream implements CsvGenerationOutputStream {

    private CasinoCsvGenerationOutputQueue queue;

    @Override
    public CsvProvider getProvider() {
        return CsvProvider.CASINO;
    }

    @Override
    public MessageChannel getChannel() {
        return queue.getChannel();
    }
}
