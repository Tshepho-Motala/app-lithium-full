package lithium.service.migration.config;

import lithium.service.migration.stream.accounting.OpeningBalanceOutputQueue;
import lithium.service.migration.stream.accounting.UpdatingBalanceOutputQueue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({OpeningBalanceOutputQueue.class, UpdatingBalanceOutputQueue.class})
@ComponentScan
public class OpeningBalanceStreamConfiguration {
}
