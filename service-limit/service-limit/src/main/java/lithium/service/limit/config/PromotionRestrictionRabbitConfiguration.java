package lithium.service.limit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class PromotionRestrictionRabbitConfiguration {
    private static final String EXCHANGE = "promotion.restriction.trigger";
    public static final String QUEUE = "promotion.restriction.trigger";
    private static final String ROUTING_KEY = "promotion.restriction.trigger";
    private static final String DEAD_LETTER_EXCHANGE = "DLX";
    public static final String DEAD_LETTER_QUEUE = QUEUE + ".dlq";
    private static final String DLQ_ROUTING_KEY = ROUTING_KEY + ".dlq";
    public static final String PARKING_LOT_QUEUE = QUEUE + ".parkingLot";

    @Autowired
    private ServiceLimitConfigurationProperties configurationProperties;

    /**
     * With the current version of amqp it is not possible to declaratively setup a delayed exchange.
     * As a workaround, this implementation sets it up programmatically. I've noted
     * lithium.service.rabbit.exchange.RabbitExchangeFactory#delayedExchangeList, but choose to do this rather to also
     * add DLQ features and control the retry logic.
     */

    @Bean
    public CustomExchange promotionRestrictionTriggerExchange() {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("x-delayed-type", "direct");
        args.put("x-delay", configurationProperties.getQueues().getPromotionRestrictionTrigger().getMessageDelay());
        return new CustomExchange(EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue promotionRestrictionTriggerQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding promotionRestrictionTriggerBinding(Queue promotionRestrictionTriggerQueue,
            Exchange promotionRestrictionTriggerExchange) {
        return BindingBuilder
                .bind(promotionRestrictionTriggerQueue)
                .to(promotionRestrictionTriggerExchange)
                .with(ROUTING_KEY)
                .noargs();
    }

    @Bean
    public DirectExchange promotionRestrictionTriggerDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue promotionRestrictionTriggerDeadLetterQueue() {
        return QueueBuilder
                .durable(DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    public Binding promotionRestrictionTriggerDeadLetterBinding(Queue promotionRestrictionTriggerDeadLetterQueue,
            Exchange promotionRestrictionTriggerDeadLetterExchange) {
        return BindingBuilder
                .bind(promotionRestrictionTriggerDeadLetterQueue)
                .to(promotionRestrictionTriggerDeadLetterExchange)
                .with(DLQ_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, DEAD_LETTER_EXCHANGE, DLQ_ROUTING_KEY);
    }

    // TODO: Move properties to external yml configuration
    @Bean
    public RetryOperationsInterceptor retryInterceptor(MessageRecoverer messageRecoverer) {
        return RetryInterceptorBuilder
                .stateless()
                .backOffOptions(configurationProperties.getQueues().getPromotionRestrictionTrigger().getBackOffOptionInterval(),
                        configurationProperties.getQueues().getPromotionRestrictionTrigger().getBackOffOptionMultiplier(),
                        configurationProperties.getQueues().getPromotionRestrictionTrigger().getBackOffOptionMaxInterval())
                .maxAttempts(configurationProperties.getQueues().getPromotionRestrictionTrigger().getMaxAttempts())
                .recoverer(messageRecoverer)
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // TODO: Move properties to external yml configuration
    @Bean
    public SimpleRabbitListenerContainerFactory promotionRestrictionRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setConcurrentConsumers(configurationProperties.getQueues().getPromotionRestrictionTrigger().getConcurrentConsumers());
        factory.setDefaultRequeueRejected(configurationProperties.getQueues().getPromotionRestrictionTrigger().isDefaultRequeueRejected());

        Advice[] adviceChain = { retryInterceptor };
        factory.setAdviceChain(adviceChain);

        return factory;
    }

//    This was added as part of VB migration changes. Having this here overrode the feign decoder configuration
//    and caused problems with deserialisation of response body objects. By default the ObjectMapper used by feign
//    is configured by jackson autoconfiguration, on missing bean, and includes this: DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES is disabled.
//    Ref: https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration.java#L129
//    Ref: https://github.com/spring-projects/spring-framework/blob/main/spring-web/src/main/java/org/springframework/http/converter/json/Jackson2ObjectMapperBuilder.java#L77
//
//    I see no reason for having this here so I am removing it without further investigation.
//    @Bean
//    public ObjectMapper objectMapper(){
//        return new ObjectMapper();
//    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
