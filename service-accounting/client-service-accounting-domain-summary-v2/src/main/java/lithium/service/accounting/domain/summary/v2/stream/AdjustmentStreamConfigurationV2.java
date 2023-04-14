package lithium.service.accounting.domain.summary.v2.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.accounting.domain.summary.v2"})
@EnableBinding({AdjustmentOutputQueueV2.class})
public class AdjustmentStreamConfigurationV2 {
}
