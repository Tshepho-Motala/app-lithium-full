package lithium.service.migration.stream.accounting;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UpdatingBalanceOutputQueue {

    @Output("updating-balance-migration-output")
    MessageChannel UpdatingBalanceOutputStream();

}
