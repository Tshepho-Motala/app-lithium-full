package lithium.service.stats.client.stream.event;

import lithium.service.stats.client.objects.StatSummaryBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnBean( annotation = EnableStatsCompletedEvent.class )
public class CompletedStatsEventService {

  private static final String TOPIC_EXCHANGE = "completed.stats";
  private static final String QUEUE_NAME = "stats.complete";

  public static final String FANOUT_EXCHANGE = "service.stats.completed.events";
  public static final String ROUTING_KEY_PRE = "stats.event.";

  @Autowired
  private RabbitAdmin rabbitAdmin;
  @Autowired
  private Jackson2JsonMessageConverter jackson2JsonMessageConverter;

  private ICompletedStatsProcessor processor;

  @Value( "${spring.application.name}" )
  protected String applicationName;

  public CompletedStatsEventService(ICompletedStatsProcessor eventProcessor) {
    processor = eventProcessor;
  }

  protected void addQueue(String events) throws Exception {
    String logStr = events + " :: ";
    String topicExchangeName = TOPIC_EXCHANGE + "." + ((!events.isEmpty()) ? events.toLowerCase().replaceAll("\\*", "all") : "all");
    String fanoutExchangeName = FANOUT_EXCHANGE;
    String queueName = QUEUE_NAME + "." + applicationName.replaceAll("-", ".");

    logStr += "fanoutExchange(" + fanoutExchangeName + ") ";
    Exchange fanoutExchange = ExchangeBuilder.fanoutExchange(fanoutExchangeName).durable(true).build();

    logStr += "topicExchange(" + topicExchangeName + ") ";
    Exchange topicExchange = ExchangeBuilder.topicExchange(topicExchangeName).durable(true).build();

    String routingKey = ROUTING_KEY_PRE + ((!events.isEmpty()) ? events.toLowerCase().replaceAll("_", ".").replaceAll("\\*", "#") : "#");

    logStr += "bind(" + fanoutExchange + ").to(" + topicExchange + ").with(" + routingKey + ") ";
    Binding binding = BindingBuilder.bind(topicExchange).to(fanoutExchange).with(routingKey).noargs();

    logStr += "queue(" + queueName + ") ";
    Queue queue = new Queue(queueName, true, false, false);
    logStr += "bind(" + queue + ").to(" + topicExchange + ").with(" + routingKey + ") ";
    Binding binding1 = BindingBuilder.bind(queue).to(topicExchange).with(routingKey).noargs();

    log.info(logStr);
    rabbitAdmin.declareExchange(fanoutExchange);
    rabbitAdmin.declareExchange(topicExchange);
    rabbitAdmin.declareBinding(binding);
    rabbitAdmin.declareQueue(queue);
    rabbitAdmin.declareBinding(binding1);

    ConsumerSimpleMessageListenerContainer container = new ConsumerSimpleMessageListenerContainer();
    container.setConnectionFactory(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
    container.setQueueNames(queueName);
    //    container.setConcurrentConsumers(this.concurrentConsumers);
    MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new ConsumerHandler(processor), jackson2JsonMessageConverter);
    //    messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
    container.setMessageListener(messageListenerAdapter);
    container.startConsumers();
    log.info("ConsumerSimpleMessageListenerContainer started: " + container);
  }

  private class ConsumerSimpleMessageListenerContainer extends SimpleMessageListenerContainer {

    public void startConsumers() throws Exception {
      super.doStart();
    }
  }

  private class ConsumerHandler {

    private ICompletedStatsProcessor processor;

    public ConsumerHandler(ICompletedStatsProcessor processor) {
      this.processor = processor;
    }

    public void handleMessage(StatSummaryBatch statSummaryBatch) throws Exception {
      log.trace("Received StatSummaryBatch: " + statSummaryBatch);
      processor.processCompletedStats(statSummaryBatch);
    }
  }
}
