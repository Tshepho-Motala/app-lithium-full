package lithium.service.document.generation.config.streams.mail;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MailCsvProviderOutputStream implements CsvGenerationOutputStream {

    private MailCsvGenerationOutputQueue queue;

    @Override
    public CsvProvider getProvider() {
        return CsvProvider.MAIL;
    }

    @Override
    public MessageChannel getChannel() {
        return queue.getChannel();
    }
}
