package lithium.service.casino.provider.roxor.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class GameplayOperationEventHandlerConfiguration {
    public static final String QUEUE = "service-accounting-roxor";
    public static final String EXCHANGE = "service-accounting-roxor";
    public static final String ROUTING_KEY = "service-accounting-roxor";
    public static final String DLQ = QUEUE + ".dlq";
    private static final String DLQ_ROUTING_KEY = ROUTING_KEY + ".dlq";
    public static final String PLQ = QUEUE + ".parkingLot";
    public static final String X_RETRIES_HEADER = "x-retries";

    private final GameplayOperationEventHandlerProperties gameplayOperationEventHandlerProperties;

    @Autowired
    public GameplayOperationEventHandlerConfiguration(@Autowired GameplayOperationEventHandlerProperties gameplayOperationEventHandlerProperties) {
        this.gameplayOperationEventHandlerProperties = gameplayOperationEventHandlerProperties;
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    Queue parkingLotQueue() {
        return new Queue(PLQ);
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .backOffOptions(gameplayOperationEventHandlerProperties.getInitialInterval(),
                        gameplayOperationEventHandlerProperties.getMultiplier(),
                        gameplayOperationEventHandlerProperties.getMaxInterval())
                .maxAttempts(gameplayOperationEventHandlerProperties.getMaxAttempts())
                .recoverer(rejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public RejectAndDontRequeueRecoverer rejectAndDontRequeueRecoverer() {
        return new RejectAndDontRequeueRecoverer();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        Advice[] adviceChain = {retryInterceptor};
        factory.setAdviceChain(adviceChain);
        factory.setConcurrentConsumers(gameplayOperationEventHandlerProperties.getListenerConcurrency());

        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
