package lithium.service.document.generation.config.streams;

import lithium.service.document.generation.config.streams.cashier.CashierTransactionsCsvGenerationOutputQueue;
import lithium.service.document.generation.config.streams.casino.CasinoCsvGenerationOutputQueue;
import lithium.service.document.generation.config.streams.mail.MailCsvGenerationOutputQueue;
import lithium.service.document.generation.config.streams.threshold.ThresholdCsvGenerationOutputQueue;
import lithium.service.document.generation.config.streams.user.UserCsvGenerationOutputQueue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableBinding({
        CashierTransactionsCsvGenerationOutputQueue.class,
        ThresholdCsvGenerationOutputQueue.class,
        MailCsvGenerationOutputQueue.class,
        CasinoCsvGenerationOutputQueue.class,
        UserCsvGenerationOutputQueue.class
})
public class CsvGenerationStreamConfig {
}
