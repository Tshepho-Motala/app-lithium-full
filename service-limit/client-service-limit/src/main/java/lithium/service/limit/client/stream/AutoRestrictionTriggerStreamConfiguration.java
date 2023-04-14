package lithium.service.limit.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.limit"})
@EnableBinding({AutoRestrictionTriggerOutputQueue.class})
public class AutoRestrictionTriggerStreamConfiguration {
}
