package lithium.service.user.client.stream.synchronize;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableBinding(UserSynchronizeOutputQueue.class)
public class UserSynchronizeStreamConfig {
}
