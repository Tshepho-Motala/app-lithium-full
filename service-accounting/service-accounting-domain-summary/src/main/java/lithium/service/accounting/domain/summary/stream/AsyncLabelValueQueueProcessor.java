package lithium.service.accounting.domain.summary.stream;

import lithium.service.accounting.domain.summary.services.AsyncLabelValueService;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "lithium.services.accounting.domain.summary.adjustments.process.enabled",
        havingValue = "true")
@Component
@Data
@EnableBinding(AsyncLabelValueQueueSink.class)
@Slf4j
public class AsyncLabelValueQueueProcessor implements IDeadLetterQueueHandler {
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private AsyncLabelValueService service;

    @Value("${lithium.services.accounting.domain.summary.dlq-retries:3}")
    private int maxDlqRetries;
    private final String parkingLotQueueName = AsyncLabelValueQueueSink.PARKING_LOT;

    @Override
    @Bean
    @Qualifier("asyncLabelValueQueueParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(AsyncLabelValueQueueSink.PARKING_LOT);
    }

    @Override
    @RabbitListener(queues = AsyncLabelValueQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
    }

    @StreamListener(AsyncLabelValueQueueSink.INPUT)
    public void handle(CompleteTransaction transaction) throws Exception {
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
