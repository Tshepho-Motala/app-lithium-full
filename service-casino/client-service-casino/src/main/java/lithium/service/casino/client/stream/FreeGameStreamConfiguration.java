package lithium.service.casino.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(FreeGameStreamOutputQueue.class)
@ComponentScan
public class FreeGameStreamConfiguration {
}
