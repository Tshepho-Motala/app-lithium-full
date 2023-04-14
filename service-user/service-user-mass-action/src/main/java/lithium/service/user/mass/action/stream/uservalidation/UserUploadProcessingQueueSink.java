package lithium.service.user.mass.action.stream.uservalidation;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface UserUploadProcessingQueueSink {
    String INPUT = "useruploadprocessinginput";

    @Input(INPUT)
    SubscribableChannel inputChannel();
}
