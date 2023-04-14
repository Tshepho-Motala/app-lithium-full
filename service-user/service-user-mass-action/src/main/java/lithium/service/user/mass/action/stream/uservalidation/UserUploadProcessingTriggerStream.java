package lithium.service.user.mass.action.stream.uservalidation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserUploadProcessingTriggerStream {
    @Autowired
    private UserUploadProcessingTriggerOuputQueue userUploadProcessingTriggerOuputQueue;

    public void trigger(Long uploadId) {
        try {
            userUploadProcessingTriggerOuputQueue.userValidateUploadProcessing()
                    .send(MessageBuilder.withPayload(uploadId).build());
        }
        catch (Exception e) {
            log.error("Failed to trigger file processing for uploadId: " + uploadId, e);
        }
    }
}
