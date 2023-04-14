package lithium.service.document.generation.config.streams.user;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserCsvProviderOutputStream implements CsvGenerationOutputStream {

    private UserCsvGenerationOutputQueue queue;

    @Override
    public CsvProvider getProvider() {
        return CsvProvider.USER;
    }

    @Override
    public MessageChannel getChannel() {
        return queue.getChannel();
    }
}
