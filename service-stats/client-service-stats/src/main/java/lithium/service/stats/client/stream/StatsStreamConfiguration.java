package lithium.service.stats.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({StatsOutputQueue.class, StatsLoginOutputQueue.class})
@ComponentScan
public class StatsStreamConfiguration {
}
