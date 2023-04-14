package lithium.service.accounting.stream;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean( annotation = EnableAccountingSummaryAccountTransactionTypeCompletedEvent.class )
public class CompletedSummaryAccountTransactionTypeEventConfig {

  @Autowired
  private ConnectionFactory connectionFactory;

  @Bean
  public RabbitAdmin rabbitAdmin() {
    return new RabbitAdmin(connectionFactory);
  }
}
