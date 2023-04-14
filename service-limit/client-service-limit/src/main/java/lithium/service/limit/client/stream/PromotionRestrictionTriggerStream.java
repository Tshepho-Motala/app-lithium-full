package lithium.service.limit.client.stream;

import lithium.service.limit.client.objects.PromotionRestrictionTriggerData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromotionRestrictionTriggerStream {
    @Autowired private RabbitTemplate rabbitTemplate;

    @Value("${promotion.restriction.trigger.delay:5000}") // Has to be defined in all modules that @EnablePromotionRestrictionTriggerStream, or, global application.yml...
    private int promotionRestrictionTriggerDelay;

    public void trigger(PromotionRestrictionTriggerData data) {
        rabbitTemplate.convertAndSend(
                PromotionRestrictionTriggerConfiguration.EXCHANGE, PromotionRestrictionTriggerConfiguration.ROUTING_KEY,
                data, (m) -> {
                    m.getMessageProperties().setHeader("x-delay", promotionRestrictionTriggerDelay);
                    m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return m;
                });
    }
}
