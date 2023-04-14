package lithium.service.affiliate.provider.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(ExportOutputQueue.class)
@ComponentScan
public class ExportStreamConfiguration {
}
