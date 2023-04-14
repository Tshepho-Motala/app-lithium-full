package lithium.service.affiliate.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(TransactionOutputQueue.class)
@ComponentScan
public class TransactionStreamConfiguration {
}
