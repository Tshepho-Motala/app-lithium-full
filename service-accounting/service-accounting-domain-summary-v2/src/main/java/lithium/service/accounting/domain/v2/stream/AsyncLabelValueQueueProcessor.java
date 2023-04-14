package lithium.service.accounting.domain.v2.stream;

import lithium.service.accounting.domain.v2.services.AsyncLabelValueService;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConditionalOnProperty(name = "lithium.services.accounting.domain.summary.v2.adjustments.process.enabled",
        havingValue = "true")
@Component
@EnableBinding(AsyncLabelValueQueueSink.class)
@Slf4j
public class AsyncLabelValueQueueProcessor {
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private AsyncLabelValueService service;

    @Value("${lithium.services.accounting.domain.summary.v2.dlq-retries:3}")
    private int dlqRetries;

    @Bean
    public Queue asyncLabelValueParkingLot() {
        return new Queue(AsyncLabelValueQueueSink.PARKING_LOT);
    }

    @RabbitListener(queues = AsyncLabelValueQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
        Integer retriesHeader = (Integer) headers.get(AsyncLabelValueQueueSink.X_RETRIES_HEADER);
        if (retriesHeader == null) {
            retriesHeader = Integer.valueOf(0);
        }
        if (retriesHeader < dlqRetries) {
            headers.put(AsyncLabelValueQueueSink.X_RETRIES_HEADER, retriesHeader + 1);
            String exchange = (String) headers.get(AsyncLabelValueQueueSink.X_ORIGINAL_EXCHANGE_HEADER);
            String originalRoutingKey = (String) headers.get(AsyncLabelValueQueueSink.X_ORIGINAL_ROUTING_KEY_HEADER);
            log.warn("DLQ entered. Requeueing attempt ("+(retriesHeader+1)+") [exchange:"+exchange+"]"
                    + " [originalRoutingKey:"+originalRoutingKey+"] [message:"+failedMessage+"]");
            rabbitTemplate.convertAndSend(exchange, originalRoutingKey, failedMessage,
                    (message) -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
        } else {
            log.error("DLQ handling failed more than "+dlqRetries+" times. Moving to parking lot: ["+failedMessage+"]");
            rabbitTemplate.convertAndSend(AsyncLabelValueQueueSink.PARKING_LOT, failedMessage,
                    (message) -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
        }
    }

    @StreamListener(AsyncLabelValueQueueSink.INPUT)
    public void handle(CompleteTransactionV2 transaction) throws Exception {
        log.trace("Received transaction from async label value queue: {}", transaction);
        try {
            service.process(Thread.currentThread().getName(), transaction);
        } catch (Exception e) {
            log.error("Failed to process async label value domain summarisation | {} | {}", e.getMessage(),
                    transaction, e);
            throw e;
        }
    }
}
