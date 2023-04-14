package lithium.service.limit.client.stream;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"lithium.service.limit"})
public class PromotionRestrictionTriggerConfiguration {
    public static final String EXCHANGE = "promotion.restriction.trigger";
    public static final String ROUTING_KEY = "promotion.restriction.trigger";
}
