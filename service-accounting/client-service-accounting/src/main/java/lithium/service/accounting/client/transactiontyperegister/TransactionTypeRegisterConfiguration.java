package lithium.service.accounting.client.transactiontyperegister;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(TransactionTypeRegisterOutputQueue.class)
@ComponentScan
public class TransactionTypeRegisterConfiguration {
}
