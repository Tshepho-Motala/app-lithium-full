package lithium.service.accounting.domain.summary.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.accounting.domain.summary"})
@EnableBinding({AdjustmentOutputQueue.class})
public class AdjustmentStreamConfiguration {
}
