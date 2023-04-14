package lithium.service.pushmsg.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableBinding(PushMsgStreamOutputQueue.class)
public class PushMsgStreamConfiguration {
}