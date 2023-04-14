package lithium.service.games.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import lithium.service.accounting.objects.CompleteTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompletedTransactionGameEnhancer {
  private final ConsumerHandlerProxy consumerHandlerProxy;
  private final RabbitAdmin rabbitAdmin;

  public CompletedTransactionGameEnhancer(RabbitAdmin rabbitAdmin, ConsumerHandlerProxy consumerHandlerProxy)
  throws Exception
  {
    this.rabbitAdmin = rabbitAdmin;
    this.consumerHandlerProxy = consumerHandlerProxy;

    createMessageListener();
  }

  private void createMessageListener()
  throws Exception
  {
    ConsumerSimpleMessageListenerContainer container = new ConsumerSimpleMessageListenerContainer();
    container.setConnectionFactory(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
    container.setQueueNames("acc.tran.games.enhance");
    //    container.setConcurrentConsumers(this.concurrentConsumers);
    container.setMessageListener(new MessageListenerAdapter(new ConsumerHandler(), new Jackson2JsonMessageConverter()));
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

    public ConsumerHandler() {
    }

    public void handleMessage(CompleteTransaction completeTransaction)
    throws JsonProcessingException
    {
      // Quickfix for PLAT-8133
      consumerHandlerProxy.handleMessage(completeTransaction);
    }
  }
}
