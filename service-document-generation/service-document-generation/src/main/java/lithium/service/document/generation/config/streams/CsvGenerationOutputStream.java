package lithium.service.document.generation.config.streams;

import lithium.service.document.generation.client.objects.CsvProvider;
import org.springframework.messaging.MessageChannel;

public interface CsvGenerationOutputStream {
    CsvProvider getProvider();
    MessageChannel getChannel();
}
