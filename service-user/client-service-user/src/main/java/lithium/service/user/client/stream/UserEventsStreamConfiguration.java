package lithium.service.user.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(UserEventsStreamOutputQueue.class)
@ComponentScan
public class UserEventsStreamConfiguration {
}