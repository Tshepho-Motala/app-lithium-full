package lithium.service.role.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(RoleRegisterOutputQueue.class)
@ComponentScan
public class RoleRegisterStreamConfiguration {
}
