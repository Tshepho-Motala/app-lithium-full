package lithium.service.promo.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.promo"})
@EnableBinding({MissionStatsOutputQueue.class})
public class MissionStatsStreamConfiguration {
}
