package lithium.service.accounting.client.stream.event;

import lithium.service.accounting.objects.CompleteTransaction;
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
@ConditionalOnBean( annotation = EnableAccountingTransactionCompletedEvent.class )
public class CompletedTransactionEventService {

  private static final String TOPIC_EXCHANGE = "completed.transactions";
  private static final String QUEUE_NAME = "acc.tran.complete";
  private static final String QUEUE_GAME_ENHANCE_NAME = "acc.tran.games.enhance";

  public static final String FANOUT_EXCHANGE = "service.accounting.completed.transactions";
  public static final String FANOUT_EXCHANGE_GAMES_ENHANCED = "service.accounting.completed.transactions_enhanced";

  public static final String ROUTING_KEY_PRE = "transaction.type.";

  @Autowired
  private RabbitAdmin rabbitAdmin;

  private ICompletedTransactionProcessor processor;

  @Value( "${spring.application.name}" )
  protected String applicationName;

  public CompletedTransactionEventService(ICompletedTransactionProcessor eventProcessor) {
    processor = eventProcessor;
  }

  protected void addQueue(String transactionTypeCode, boolean enhanceGameData) throws Exception {
    String logStr = transactionTypeCode + " (GameDataEnhanced: "+enhanceGameData+") :: ";
    String topicExchangeName = TOPIC_EXCHANGE + "." + ((!transactionTypeCode.isEmpty()) ? transactionTypeCode.toLowerCase().replaceAll("\\*", "all") : "all");
    String topicExchangeGameEnhanceName = TOPIC_EXCHANGE + ".games_enhance";

    String fanoutExchangeName = FANOUT_EXCHANGE;
    String fanoutExchangeGameEnhanceName = FANOUT_EXCHANGE_GAMES_ENHANCED;

    String queueName = QUEUE_NAME + "." + applicationName.replaceAll("-", ".");
    String queueGameEnhanceName = QUEUE_GAME_ENHANCE_NAME;


    logStr += "fanoutExchange(" + fanoutExchangeName + ") ";
    Exchange fanoutExchange = ExchangeBuilder.fanoutExchange(fanoutExchangeName).durable(true).build();
    if (!enhanceGameData) {
      String routingKey = ROUTING_KEY_PRE + ((!transactionTypeCode.isEmpty()) ? transactionTypeCode.toLowerCase().replaceAll("_", ".").replaceAll("\\*", "#") : "#");

      logStr += "topicExchange(" + topicExchangeName + ") ";
      Exchange topicExchange = ExchangeBuilder.topicExchange(topicExchangeName).durable(true).build();

      logStr += "queue(" + queueName + ") ";
      Queue queue = new Queue(queueName, true, false, false);

      logStr += "bind(" + fanoutExchange + ").to(" + topicExchange + ").with(" + routingKey + ") ";
      Binding bindingExchanges = BindingBuilder.bind(topicExchange).to(fanoutExchange).with(routingKey).noargs();

      logStr += "bind(" + queue + ").to(" + topicExchange + ").with(" + routingKey + ") ";
      Binding bindingQueueToTopicExchange = BindingBuilder.bind(queue).to(topicExchange).with(routingKey).noargs();

      log.info(logStr);
      rabbitAdmin.declareExchange(fanoutExchange);
      rabbitAdmin.declareExchange(topicExchange);
      rabbitAdmin.declareBinding(bindingExchanges);
      rabbitAdmin.declareQueue(queue);
      rabbitAdmin.declareBinding(bindingQueueToTopicExchange);

      ConsumerSimpleMessageListenerContainer container = new ConsumerSimpleMessageListenerContainer();
      container.setConnectionFactory(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
      container.setQueueNames(queueName);
      //    container.setConcurrentConsumers(this.concurrentConsumers);
      container.setMessageListener(new MessageListenerAdapter(new ConsumerHandler(processor), new Jackson2JsonMessageConverter()));
      container.startConsumers();
      log.info("ConsumerSimpleMessageListenerContainer started: " + container);
    } else {
      String routingKey = ROUTING_KEY_PRE + ((!transactionTypeCode.isEmpty()) ? transactionTypeCode.toLowerCase().replaceAll("_", ".").replaceAll("\\*", "#") : "#");
      String routingKeyGameEnhance = ROUTING_KEY_PRE +"enhanced."+ ((!transactionTypeCode.isEmpty()) ? transactionTypeCode.toLowerCase().replaceAll("_", ".").replaceAll("\\*", "#") : "#");

      Exchange fanoutExchangeGameEnhance = ExchangeBuilder.fanoutExchange(fanoutExchangeGameEnhanceName).durable(true).build();

      logStr += "topicExchange(" + topicExchangeName + ") ";
      Exchange topicExchange = ExchangeBuilder.topicExchange(topicExchangeName).durable(true).build();
      Exchange topicExchangeGameEnhance = ExchangeBuilder.topicExchange(topicExchangeGameEnhanceName).durable(true).build();

      logStr += "queue(" + queueName + ") ";
      Queue queue = new Queue(queueName, true, false, false);
      Queue queueGameEnhance = new Queue(queueGameEnhanceName, true, false, false);

      logStr += "bind(" + fanoutExchange + ").to(" + topicExchangeGameEnhance + ").with(" + routingKey + ") ";
      Binding bindingExchanges = BindingBuilder.bind(topicExchangeGameEnhance).to(fanoutExchange).with(routingKey).noargs();

      logStr += "bind(" + queueGameEnhance + ").to(" + topicExchangeGameEnhance + ").with(" + routingKey + ") ";
      Binding bindingQueueToTopicExchange = BindingBuilder.bind(queueGameEnhance).to(topicExchangeGameEnhance).with(routingKey).noargs();


      logStr += "bind(" + topicExchange + ").to(" + fanoutExchangeGameEnhance + ").with(" + routingKeyGameEnhance + ") ";
      Binding bindingExchanges2 = BindingBuilder.bind(topicExchange).to(fanoutExchangeGameEnhance).with(routingKeyGameEnhance).noargs();

      logStr += "bind(" + queue + ").to(" + topicExchange + ").with(" + routingKeyGameEnhance + ") ";
      Binding bindingQueueToTopicExchange2 = BindingBuilder.bind(queue).to(topicExchange).with(routingKeyGameEnhance).noargs();

      log.info(logStr);
      rabbitAdmin.declareExchange(fanoutExchange);
      rabbitAdmin.declareExchange(fanoutExchangeGameEnhance);
      rabbitAdmin.declareExchange(topicExchange);
      rabbitAdmin.declareExchange(topicExchangeGameEnhance);
      rabbitAdmin.declareBinding(bindingExchanges);
      rabbitAdmin.declareBinding(bindingExchanges2);
      rabbitAdmin.declareQueue(queue);
      rabbitAdmin.declareQueue(queueGameEnhance);
      rabbitAdmin.declareBinding(bindingQueueToTopicExchange);
      rabbitAdmin.declareBinding(bindingQueueToTopicExchange2);

      ConsumerSimpleMessageListenerContainer container = new ConsumerSimpleMessageListenerContainer();
      container.setConnectionFactory(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
      container.setQueueNames(queueName);
      //    container.setConcurrentConsumers(this.concurrentConsumers);
      container.setMessageListener(new MessageListenerAdapter(new ConsumerHandler(processor), new Jackson2JsonMessageConverter()));
      container.startConsumers();
      log.info("ConsumerSimpleMessageListenerContainer started: " + container);
    }
  }

  private class ConsumerSimpleMessageListenerContainer extends SimpleMessageListenerContainer {

    public void startConsumers() throws Exception {
      super.doStart();
    }
  }

  private class ConsumerHandler {

    private ICompletedTransactionProcessor processor;

    public ConsumerHandler(ICompletedTransactionProcessor processor) {
      this.processor = processor;
    }

    public void handleMessage(CompleteTransaction completeTransaction) throws Exception {
      log.trace("Received CompleteTransaction: " + completeTransaction);
      processor.processCompletedTransaction(completeTransaction);
    }
  }
}
