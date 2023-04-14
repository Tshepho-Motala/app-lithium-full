package lithium.service.cashier.client.event;

import lithium.service.cashier.client.objects.SuccessfulTransactionEvent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class CashierFirstDepositEventSink {

	private ICashierFirstDepositProcessor processor;

	public CashierFirstDepositEventSink(ICashierFirstDepositProcessor eventProcessor) {
		processor = eventProcessor;
	}

	public static final String EXCHANGE_NAME = "service.cashier.first.deposit";

	@Value("${spring.application.name}")
	protected String applicationName;

	/**
	 * Need to create rabbit admin to allow the creation of exchanges and queues
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {

		return new RabbitAdmin(connectionFactory);
	}

	/**
	 * Explicit queue creation with durable flag.
	 * This queue will be unique per service implementation and bind the the defined exchange
	 * @return
	 */
	@Bean
	public Queue cashierFirstDepositQueue() {
		return new Queue("cashier.first.deposit." + applicationName.replaceAll("-", "."), true, false, false);
	}

	/**
	 * The exchange will already be created at this time but this just serves as a backup mechanism to have an exchange.
	 */
	@Bean
	public FanoutExchange cashierFirstDepositExchange() {
		return new FanoutExchange(EXCHANGE_NAME, true, false);
	}

	/**
	 * The binding of the queue to the fanout exchange.
	 * If this was a topic exchange a routning key would need to be added in this binding to make it function.
	 * @return
	 */
	@Bean
	public Binding firstDepositExchangeBinding() {
		return BindingBuilder.bind(cashierFirstDepositQueue()).to(cashierFirstDepositExchange());
	}

	/**
	 * Rabbit listener that creates a channel that binds to the queue
	 * The #{} portion is an spEL experession indicating our need to get the queueBean to inject its value into the listener
	 * @param event
	 * @throws Exception
	 */
	@RabbitListener(queues = "#{ @cashierFirstDepositQueue }")
	public void processFirstDeposit(final SuccessfulTransactionEvent event) throws Exception {
		processor.processFirstDeposit(event);
	}
}
