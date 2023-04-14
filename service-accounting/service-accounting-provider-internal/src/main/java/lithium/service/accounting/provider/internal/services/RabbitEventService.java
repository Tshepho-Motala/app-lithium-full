package lithium.service.accounting.provider.internal.services;

import lithium.service.accounting.client.stream.event.CompletedTransactionEventService;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.PlayerBalanceLimitReachedEvent;
import lithium.service.accounting.stream.CompletedSummaryAccountTransactionTypeEventService;
import lithium.service.accounting.stream.PlayerBalanceReachedQueueSink;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static lithium.service.domain.client.DomainSettings.INITIATE_WD_ON_BALANCE_LIMIT_REACHED_DELAY_IN_MS;

@Slf4j
@Service
@AllArgsConstructor
public class RabbitEventService {
	private final RabbitTemplate rabbitTemplate;
	private final CachingDomainClientService cachingDomainClientService;

	public void send(CompleteTransaction payload) {
		String transactionTypeCode = payload.getTransactionType();
		rabbitTemplate.convertAndSend(CompletedTransactionEventService.FANOUT_EXCHANGE, CompletedTransactionEventService.ROUTING_KEY_PRE+transactionTypeCode.toLowerCase().replaceAll("_", "."), payload);
	}

	public void sendCompletedSummaryAccountTransactionTypeEvent(CompleteSummaryAccountTransactionType payload) {
		String transactionTypeCode = payload.getTransactionType();
		payload.getDetails().forEach(sa -> log.trace("SA: "+transactionTypeCode+": "+sa));
		log.debug("convertAndSend: "+payload);
		rabbitTemplate.convertAndSend(CompletedSummaryAccountTransactionTypeEventService.FANOUT_EXCHANGE, CompletedSummaryAccountTransactionTypeEventService.ROUTING_KEY_PRE+transactionTypeCode.toLowerCase().replaceAll("_", "."), payload);
	}

	public void sendPlayerBalanceLimitReachedEvent(PlayerBalanceLimitReachedEvent payload) {
		rabbitTemplate.convertAndSend(PlayerBalanceReachedQueueSink.EXCHANGE_NAME, "", payload,
				message -> {
					message.getMessageProperties().setHeader("x-delay", getDelay(payload.getDomainName()));
					return message;
				});
	}

	private Long getDelay(String domainName) {
		String delay = INITIATE_WD_ON_BALANCE_LIMIT_REACHED_DELAY_IN_MS.defaultValue();
		try {
			delay = cachingDomainClientService.retrieveDomainFromDomainService(domainName)
					.findDomainSettingByName(INITIATE_WD_ON_BALANCE_LIMIT_REACHED_DELAY_IN_MS.key())
					.orElse(delay);
		} catch (Status550ServiceDomainClientException e) {
			log.warn("Can't retrieve domain due " + e.getMessage() + ". Used default delay value: " + delay);
		}
		return Long.parseLong(delay);
	}

}

