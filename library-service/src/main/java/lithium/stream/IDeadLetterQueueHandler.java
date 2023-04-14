package lithium.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;

import java.util.Map;

/**
 * Usage:
 * <br><br>
 * In the implementing class:
 * <br>
 * <ul>
 *     <li>Add @Data to the class level annotations to circumvent implementing various accessor methods.
 *     Just provide the respective member variables.</li>
 *     <li>@Autowire a RabbitTemplate, named rabbitTemplate.</li>
 *     <li>Add the maxDlqRetries member variable.</li>
 *     <li>Add the parkingLotQueueName member variable.</li>
 *     <li>Override lithium.stream.IDeadLetterQueueHandler#parkingLotQueue(). Annotate with @Bean,
 *     and provide an @Qualifier: this becomes important if you have multiple parking lot queues within the same
 *     application context.</li>
 *     <li>Override lithium.stream.IDeadLetterQueueHandler#dlqHandle(org.springframework.amqp.core.Message).
 *     Annotate with @RabbitListener and specify your dead letter queue. Then call the super dlqHandle method:
 *     the generic implementation provided here will take care of the DLQ and PL processing.</li>
 * </ul>
 */
public interface IDeadLetterQueueHandler {
    Logger log = LoggerFactory.getLogger(IDeadLetterQueueHandler.class);

    RabbitTemplate getRabbitTemplate();

    int getMaxDlqRetries();

    String getParkingLotQueueName();

    Queue parkingLotQueue();

    default void dlqHandle(Message failedMessage) {
        Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
        Integer retriesHeader = (Integer) headers.get("x-retries");
        if (retriesHeader == null) {
            retriesHeader = Integer.valueOf(0);
        }
        if (retriesHeader < getMaxDlqRetries()) {
            headers.put("x-retries", retriesHeader + 1);
            int retries = retriesHeader +1;
            String exchange = (String) headers.get(RepublishMessageRecoverer.X_ORIGINAL_EXCHANGE);
            String originalRoutingKey = (String) headers.get(RepublishMessageRecoverer.X_ORIGINAL_ROUTING_KEY);
            log.warn("DLQ entered. Requeueing attempt (" + (retriesHeader + 1) + ") [exchange:" + exchange + "]"
                    + " [originalRoutingKey:" + originalRoutingKey + "] [message:" + failedMessage + "]");
            getRabbitTemplate().convertAndSend(exchange, originalRoutingKey, failedMessage,
                    (message) -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        message.getMessageProperties().setHeader("x-reties", retries);
                        return message;
                    });
        } else {
            log.error("DLQ handling failed more than " + getMaxDlqRetries() + " times. Moving to parking lot:"
                    + " [" + failedMessage + "]");
            getRabbitTemplate().convertAndSend(getParkingLotQueueName(), failedMessage,
                    (message) -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
        }
    }
}
