package lithium.service.accounting.domain.v2.stream;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.domain.v2.services.SummaryService;
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

import java.util.List;
import java.util.Map;

@ConditionalOnProperty(name = "lithium.services.accounting.domain.summary.v2.adjustments.process.enabled",
		havingValue = "true")
@Component
@EnableBinding(AdjustmentQueueSink.class)
@Slf4j
public class AdjustmentQueueProcessor {
	@Autowired private RabbitTemplate rabbitTemplate;
	@Autowired private SummaryService service;

	@Value("${lithium.services.accounting.domain.summary.v2.dlq-retries:3}")
	private int dlqRetries;

	@Bean
	public Queue adjustmentQueueParkingLot() {
		return new Queue(AdjustmentQueueSink.PARKING_LOT);
	}

	@RabbitListener(queues = AdjustmentQueueSink.DLQ)
	public void dlqHandle(Message failedMessage) {
		Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
		Integer retriesHeader = (Integer) headers.get(AdjustmentQueueSink.X_RETRIES_HEADER);
		if (retriesHeader == null) {
			retriesHeader = Integer.valueOf(0);
		}
		if (retriesHeader < dlqRetries) {
			headers.put(AdjustmentQueueSink.X_RETRIES_HEADER, retriesHeader + 1);
			String exchange = (String) headers.get(AdjustmentQueueSink.X_ORIGINAL_EXCHANGE_HEADER);
			String originalRoutingKey = (String) headers.get(AdjustmentQueueSink.X_ORIGINAL_ROUTING_KEY_HEADER);
			log.warn("DLQ entered. Requeueing attempt ("+(retriesHeader+1)+") [exchange:"+exchange+"]"
					+ " [originalRoutingKey:"+originalRoutingKey+"] [message:"+failedMessage+"]");
			rabbitTemplate.convertAndSend(exchange, originalRoutingKey, failedMessage,
					(message) -> {
						message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						return message;
					});
		} else {
			log.error("DLQ handling failed more than "+dlqRetries+" times. Moving to parking lot: ["+failedMessage+"]");
			rabbitTemplate.convertAndSend(AdjustmentQueueSink.PARKING_LOT, failedMessage,
					(message) -> {
						message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						return message;
					});
		}
	}

	@StreamListener(AdjustmentQueueSink.INPUT)
	public void handle(List<CompleteTransactionV2> transactions) throws Status500InternalServerErrorException {
		String threadName = Thread.currentThread().getName();
		service.process(threadName, transactions);
	}
}
