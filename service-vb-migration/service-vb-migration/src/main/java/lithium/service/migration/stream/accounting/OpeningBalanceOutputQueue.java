package lithium.service.migration.stream.accounting;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OpeningBalanceOutputQueue {

    @Output("opening-balance-migration-output")
    MessageChannel OpeningBalanceOutputStream();

}
