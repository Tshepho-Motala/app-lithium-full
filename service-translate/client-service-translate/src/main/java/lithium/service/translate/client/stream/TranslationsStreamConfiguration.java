package lithium.service.translate.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(TranslationsStreamOutputQueue.class)
@ComponentScan
public class TranslationsStreamConfiguration {
}
