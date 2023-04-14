package lithium.service.casino.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(TriggerBonusStreamOutputQueue.class)
@ComponentScan
public class TriggerBonusStreamConfiguration {
}