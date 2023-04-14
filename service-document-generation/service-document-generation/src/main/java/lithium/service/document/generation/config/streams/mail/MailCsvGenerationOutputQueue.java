package lithium.service.document.generation.config.streams.mail;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MailCsvGenerationOutputQueue {
    @Output("service-csv-provider-mail-output")
    MessageChannel getChannel();
}
