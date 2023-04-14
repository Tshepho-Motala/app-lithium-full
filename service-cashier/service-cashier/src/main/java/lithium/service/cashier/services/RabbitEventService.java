package lithium.service.cashier.services;

import lithium.service.cashier.client.event.CashierFirstDepositEventSink;
import lithium.service.cashier.client.objects.SuccessfulTransactionEvent;
import lithium.service.cashier.data.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class RabbitEventService {
	private final RabbitTemplate rabbitTemplate;

	public void sendFirstDepositEvent(String userGuid, Transaction transaction, boolean isFirstDeposit) {
		rabbitTemplate.convertAndSend(
			CashierFirstDepositEventSink.EXCHANGE_NAME,
			"",
			SuccessfulTransactionEvent.builder()
				.userGuid(userGuid)
				.transactionId(transaction.getId())
				.transactionType(transaction.getTransactionType().description())
				.amount(transaction.getAmountCents())
				.isFirstDeposit(isFirstDeposit)
				.createdDate(transaction.getCreatedOn())
				.updatedDate(transaction.getCurrent().getTimestamp())
				.build());
	}
}

