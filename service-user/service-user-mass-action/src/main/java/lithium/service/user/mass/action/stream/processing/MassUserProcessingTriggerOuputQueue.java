package lithium.service.user.mass.action.stream.processing;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MassUserProcessingTriggerOuputQueue {
    @Output("massuserprocessingoutput")
    MessageChannel processUser();
}
