package lithium.service.accounting.stream;

import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;
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
@ConditionalOnBean( annotation = EnableAccountingSummaryAccountTransactionTypeCompletedEvent.class )
public class CompletedSummaryAccountTransactionTypeEventService {

  private static final String TOPIC_EXCHANGE = "completed.summary.att.transactions";
  private static final String QUEUE_NAME = "acc.sum.att.tran.complete";

  public static final String FANOUT_EXCHANGE = "service.accounting.completed.sum.att.transactions";

  public static final String ROUTING_KEY_PRE = "transaction.type.";

  @Autowired
  private RabbitAdmin rabbitAdmin;

  private ICompletedSummaryAccountTransactionTypeProcessor processor;

  @Value( "${spring.application.name}" )
  protected String applicationName;

  public CompletedSummaryAccountTransactionTypeEventService(ICompletedSummaryAccountTransactionTypeProcessor eventProcessor) {
    processor = eventProcessor;
  }

  protected void addQueue(String transactionTypeCode)
  throws Exception
  {
    String logStr = transactionTypeCode + " :: ";
    String topicExchangeName =
        TOPIC_EXCHANGE + "." + ((!transactionTypeCode.isEmpty()) ? transactionTypeCode.toLowerCase().replaceAll("\\*", "all") : "all");
    String fanoutExchangeName = FANOUT_EXCHANGE;
    String queueName = QUEUE_NAME + "." + applicationName.replaceAll("-", ".");

    logStr += "fanoutExchange(" + fanoutExchangeName + ") ";
    Exchange fanoutExchange = ExchangeBuilder.fanoutExchange(fanoutExchangeName).durable(true).build();

    logStr += "topicExchange(" + topicExchangeName + ") ";
    Exchange topicExchange = ExchangeBuilder.topicExchange(topicExchangeName).durable(true).build();

    String routingKey =
        "transaction.type." + ((!transactionTypeCode.isEmpty()) ? transactionTypeCode.toLowerCase().replaceAll("_", ".").replaceAll("\\*", "#")
            : "#");

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
    container.setMessageListener(new MessageListenerAdapter(new ConsumerHandler(processor), new Jackson2JsonMessageConverter()));
    container.startConsumers();
    log.info("ConsumerSimpleMessageListenerContainer started: " + container);
  }

  private class ConsumerSimpleMessageListenerContainer extends SimpleMessageListenerContainer {

    public void startConsumers()
    throws Exception
    {
      super.doStart();
    }
  }

  private class ConsumerHandler {

    private ICompletedSummaryAccountTransactionTypeProcessor processor;

    public ConsumerHandler(ICompletedSummaryAccountTransactionTypeProcessor processor) {
      this.processor = processor;
    }

    public void handleMessage(CompleteSummaryAccountTransactionType completeSummaryAccountTransactionType)
    throws Exception
    {
      log.trace("Received CompleteSummaryAccountTransactionType: " + completeSummaryAccountTransactionType);
      processor.processCompletedSummaryAccountTransactionType(completeSummaryAccountTransactionType);
    }
  }
}
