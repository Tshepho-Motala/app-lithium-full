package lithium.service.domain.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableBinding(DomainAttributesOutputQueue.class)
@Configuration
@ComponentScan
public class DomainAttributesStreamConfiguration {
}
