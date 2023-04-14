package lithium.service.accounting.client.stream.event;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean( annotation = EnableAccountingTransactionCompletedEvent.class )
public class CompletedTransactionEventConfig {

  @Autowired
  private ConnectionFactory connectionFactory;

  @Bean
  public RabbitAdmin rabbitAdmin() {
    return new RabbitAdmin(connectionFactory);
  }
}
