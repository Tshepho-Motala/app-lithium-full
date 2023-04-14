package lithium.service.accounting.stream;

import lithium.service.accounting.objects.PlayerBalanceLimitReachedEvent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public class PlayerBalanceReachedQueueSink {

	private IPlayerBalanceLimitReachedProcessor processor;

	public PlayerBalanceReachedQueueSink(IPlayerBalanceLimitReachedProcessor processor, @Value("${spring.application.name}") String applicationName) {
		this.processor = processor;
		this.queueName ="reached.pbl." + applicationName.replaceAll("-", ".");
	}

	public final static String EXCHANGE_NAME = "service.accounting.player.reached.balance.limit";
	private final String queueName;


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
	public Queue queue() {
		return new Queue(queueName, true, false, false);
	}

	/**
	 * The exchange will already be created at this time but this just serves as a backup mechanism to have an exchange.
	 */
	@Bean
	private CustomExchange customExchange() {
		Map<String, Object> args = new HashMap<>();
		args.put("x-delayed-type", "direct");
		return new CustomExchange(PlayerBalanceReachedQueueSink.EXCHANGE_NAME, "x-delayed-message", true, false, args);
	}

	/**
	 * The binding of the queue to the fanout exchange.
	 * If this was a topic exchange a routning key would need to be added in this binding to make it function.
	 * @return
	 */
	@Bean
	private Binding binding(Queue queue, CustomExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("").noargs();
	}

	/**
	 * Rabbit listener that creates a channel that binds to the queue
	 * The #{} portion is an spEL experession indicating our need to get the queueBean to inject its value into the listener
	 * @param payload
	 * @throws Exception
	 */
	@RabbitListener(queues = "#{ @queue }")
	public void processCompletedTransaction(final PlayerBalanceLimitReachedEvent payload) throws Exception {
		processor.onPlayerBalanceLimitReached(payload);
	}


}
