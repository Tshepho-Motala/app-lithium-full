package lithium.service.migration.stream.accounting;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface HistoricTransactionsOutputQueue {
  @Output("historic-migration-output")
  MessageChannel HistoricTransactionsOutputStream();


}
