package lithium.service.accounting.client.stream.transactionlabel;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(TransactionLabelOutputQueue.class)
@ComponentScan
public class TransactionLabelStreamConfiguration {
}
