package lithium.service.mail.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(DefaultEmailTemplateStreamOutputQueue.class)
@ComponentScan
public class DefaultEmailTemplateStreamConfiguration {
}