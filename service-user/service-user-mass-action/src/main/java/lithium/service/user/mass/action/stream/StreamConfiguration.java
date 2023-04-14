package lithium.service.user.mass.action.stream;

import lithium.service.user.mass.action.stream.processing.MassUserProcessingQueueSink;
import lithium.service.user.mass.action.stream.processing.MassUserProcessingTriggerOuputQueue;
import lithium.service.user.mass.action.stream.uservalidation.UserUploadProcessingQueueSink;
import lithium.service.user.mass.action.stream.uservalidation.UserUploadProcessingTriggerOuputQueue;
import lithium.service.user.mass.action.stream.uservalidation.UserValidationQueueSink;
import lithium.service.user.mass.action.stream.uservalidation.UserValidationTriggerOutputQueue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({
        UserUploadProcessingTriggerOuputQueue.class,
        UserUploadProcessingQueueSink.class,
        UserValidationTriggerOutputQueue.class,
        UserValidationQueueSink.class,
        MassUserProcessingTriggerOuputQueue.class,
        MassUserProcessingQueueSink.class
})
public class StreamConfiguration {
}
