package lithium.service.access.client.gamstop;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(GamstopStreamOutputQueue.class)
@ComponentScan
public class GamstopStreamConfiguration {
}
