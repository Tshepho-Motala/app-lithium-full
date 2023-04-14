package lithium.service.user.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.user"})
@EnableBinding({UserAttributesTriggerOutputQueue.class})
public class UserAttributesTriggerStreamConfiguration {
}
