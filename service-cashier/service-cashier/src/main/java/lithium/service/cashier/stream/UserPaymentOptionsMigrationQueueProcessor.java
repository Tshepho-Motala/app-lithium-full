package lithium.service.cashier.stream;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.objects.UserPaymentOptionsMigrationRequest;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.ProcessorUserCardMigrationService;
import lithium.service.cashier.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableBinding(UserPaymentOptionsMigrationQueueSink.class)
@Slf4j
public class UserPaymentOptionsMigrationQueueProcessor {
	private final RabbitTemplate rabbitTemplate;
	private final ProcessorUserCardMigrationService service;
	private final UserService userService;

	@Autowired
	public UserPaymentOptionsMigrationQueueProcessor(RabbitTemplate rabbitTemplate,
			ProcessorUserCardMigrationService service, UserService userService) {
		this.rabbitTemplate = rabbitTemplate;
		this.service = service;
		this.userService = userService;
	}

	@Value("${lithium.services.cashier.user-payment-options-migration.dlq-retries:3}")
	private int dlqRetries;

	@Bean
	public Queue userPaymentOptionsMigrationQueueParkingLot() {
		return new Queue(UserPaymentOptionsMigrationQueueSink.PARKING_LOT);
	}

	@RabbitListener(queues =  UserPaymentOptionsMigrationQueueSink.DLQ)
	public void dlqHandle(Message failedMessage) {
		Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
		Integer retriesHeader = (Integer) headers.get(UserPaymentOptionsMigrationQueueSink.X_RETRIES_HEADER);
		if (retriesHeader == null) {
			retriesHeader = Integer.valueOf(0);
		}
		if (retriesHeader < dlqRetries) {
			headers.put(UserPaymentOptionsMigrationQueueSink.X_RETRIES_HEADER, retriesHeader + 1);
			String exchange = (String) headers.get(UserPaymentOptionsMigrationQueueSink.X_ORIGINAL_EXCHANGE_HEADER);
			String originalRoutingKey = (String) headers.get(UserPaymentOptionsMigrationQueueSink.X_ORIGINAL_ROUTING_KEY_HEADER);
			log.warn("DLQ entered. Requeueing attempt ("+(retriesHeader+1)+") [exchange:"+exchange+"]"
					+ " [originalRoutingKey:"+originalRoutingKey+"] [message:"+failedMessage+"]");
			rabbitTemplate.convertAndSend(exchange, originalRoutingKey, failedMessage,
					(message) -> {
						message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						return message;
					});
		} else {
			log.error("DLQ handling failed more than "+dlqRetries+" times. Moving to parking lot: ["+failedMessage+"]");
			rabbitTemplate.convertAndSend(UserPaymentOptionsMigrationQueueSink.PARKING_LOT, failedMessage,
					(message) -> {
						message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						return message;
					});
		}
	}

	@StreamListener(UserPaymentOptionsMigrationQueueSink.INPUT)
	public void handle(UserPaymentOptionsMigrationRequest request) throws Status500InternalServerErrorException {
		log.trace("Received request to migrate user payment options | {}", request);

		User user = userService.findOrCreateRetryable(request.getUserGuid());

		service.migrateProcessorUserCards(request.getDomainName(), user, request.getUserTokenId(),
				request.getMethodCode(), request.getProcessorCode());
	}
}
