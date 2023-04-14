package lithium.service.accounting.client.stream.auxlabel;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(AuxLabelOutputQueue.class)
@ComponentScan
public class AuxLabelStreamConfiguration {
}
