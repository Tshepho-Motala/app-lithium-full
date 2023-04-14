package lithium.service.user.client.stream.synchronize;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserSynchronizeOutputQueue {

    @Output("service-user-synchronize-output")
    public MessageChannel userSynchronizeChannel();
}
