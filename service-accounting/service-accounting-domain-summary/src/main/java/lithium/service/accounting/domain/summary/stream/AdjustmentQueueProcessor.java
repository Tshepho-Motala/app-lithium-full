package lithium.service.accounting.domain.summary.stream;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.domain.summary.services.SummaryService;
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

import java.util.List;

@ConditionalOnProperty(name = "lithium.services.accounting.domain.summary.adjustments.process.enabled",
		havingValue = "true")
@Component
@Data
@EnableBinding(AdjustmentQueueSink.class)
@Slf4j
public class AdjustmentQueueProcessor implements IDeadLetterQueueHandler {
	@Autowired private RabbitTemplate rabbitTemplate;
	@Autowired private SummaryService service;

	@Value("${lithium.services.accounting.domain.summary.dlq-retries:3}")
	private int maxDlqRetries;
	private final String parkingLotQueueName = AdjustmentQueueSink.PARKING_LOT;

	@Override
	@Bean
	@Qualifier("adjustmentQueueParkingLot")
	public Queue parkingLotQueue() {
		return new Queue(AdjustmentQueueSink.PARKING_LOT);
	}

	@Override
	@RabbitListener(queues = AdjustmentQueueSink.DLQ)
	public void dlqHandle(Message failedMessage) {
		IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
	}

	@StreamListener(AdjustmentQueueSink.INPUT)
	public void handle(List<CompleteTransaction> transactions) throws Status500InternalServerErrorException {
		String threadName = Thread.currentThread().getName();
		service.process(threadName, transactions);
	}
}
