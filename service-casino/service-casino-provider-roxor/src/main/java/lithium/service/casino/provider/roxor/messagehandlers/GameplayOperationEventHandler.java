package lithium.service.casino.provider.roxor.messagehandlers;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.provider.roxor.config.GameplayOperationEventHandlerConfiguration;
import lithium.service.casino.provider.roxor.data.GameplayOperationEventRequest;
import lithium.service.casino.provider.roxor.services.GamePlayOperationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameplayOperationEventHandler {
    @Autowired private GamePlayOperationEventService gamePlayOperationService;
	@Autowired private RabbitTemplate rabbitTemplate;

	@Value("${lithium.services.casino.provider.roxor.message-handlers.gameplay-operation-event.dlq.retries:1}")
	private int dlqRetries;

    @TimeThisMethod
    @RabbitListener(queues = GameplayOperationEventHandlerConfiguration.QUEUE,
		    containerFactory = "rabbitListenerContainerFactory")
    public void handleGameplayOperationEventImport(GameplayOperationEventRequest gameplayOperationEventRequest) throws Exception {
	    try {
			SW.start("findOrCreateUser");
		    String userGuid = gamePlayOperationService.findOrCreateUser(gameplayOperationEventRequest);
		    SW.stop();
			SW.start("GameplayOperationEventHandler::handleGameplayOperationEventImport::gamePlayOperationService.gamePlay");
		    log.info("Processing GameplayOperationEventRequest :  {}", gameplayOperationEventRequest);
		    gamePlayOperationService.gamePlay(gameplayOperationEventRequest, userGuid);
		    SW.stop();
	    } catch (Exception e) {
		    log.error("Error while processing GameplayOperationEventRequest :  {}" + e.getMessage(), gameplayOperationEventRequest, e);
		    throw e;
	    }
    }

	@RabbitListener(queues = GameplayOperationEventHandlerConfiguration.DLQ)
	public void dlqHandle(GameplayOperationEventRequest gameplayOperationEventRequest, Message message) {
		log.trace("dlqHandle | {}, {}", gameplayOperationEventRequest, message);
		Map<String, Object> headers = message.getMessageProperties().getHeaders();
		Integer retriesHeader = (Integer) headers.get(GameplayOperationEventHandlerConfiguration.X_RETRIES_HEADER);
		if (retriesHeader == null) {
			retriesHeader = Integer.valueOf(0);
		}
		if (retriesHeader < dlqRetries) {
			headers.put(GameplayOperationEventHandlerConfiguration.X_RETRIES_HEADER, retriesHeader + 1);
			log.warn("DLQ entered. Requeueing attempt ("+(retriesHeader+1)+")"
					+ " [exchange:"+GameplayOperationEventHandlerConfiguration.EXCHANGE+"]"
					+ " [routingKey:"+GameplayOperationEventHandlerConfiguration.ROUTING_KEY+"]"
					+ " [message:"+message+"]");
			rabbitTemplate.convertAndSend(GameplayOperationEventHandlerConfiguration.EXCHANGE,
					GameplayOperationEventHandlerConfiguration.ROUTING_KEY, message,
					(m) -> {
						m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						return m;
					});
		} else {
			log.error("DLQ handling failed more than ("+dlqRetries+") times. Moving to parking lot: ["+message+"]");
			rabbitTemplate.convertAndSend(GameplayOperationEventHandlerConfiguration.PLQ, message,
					(m) -> {
						m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						return m;
					});
		}
	}
}
