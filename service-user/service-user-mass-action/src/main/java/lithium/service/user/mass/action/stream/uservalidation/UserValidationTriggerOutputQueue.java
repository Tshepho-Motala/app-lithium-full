package lithium.service.user.mass.action.stream.uservalidation;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserValidationTriggerOutputQueue {
    @Output("uservalidationoutput")
    MessageChannel userValidationOutputStream();
}
