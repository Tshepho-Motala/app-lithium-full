package lithium.service.reward.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableBinding( GiveRewardStreamOutputQueue.class )
public class GiveRewardStreamConfiguration {

}