package lithium.service.reward.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding( RewardTypeStreamOutputQueue.class )
@ComponentScan
public class RewardTypeStreamConfiguration {

}