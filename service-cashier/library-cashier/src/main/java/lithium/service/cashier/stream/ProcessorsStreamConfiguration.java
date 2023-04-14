package lithium.service.cashier.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(ProcessorsStreamOutputQueue.class)
@ComponentScan
public class ProcessorsStreamConfiguration {
}