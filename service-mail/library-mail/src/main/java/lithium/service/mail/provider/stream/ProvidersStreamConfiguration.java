package lithium.service.mail.provider.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(ProvidersStreamOutputQueue.class)
@ComponentScan
public class ProvidersStreamConfiguration {
}