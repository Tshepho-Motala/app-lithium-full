package lithium.service.user.mass.action.stream.uservalidation;

import lithium.service.user.mass.action.services.MassValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
class UserUploadProcessingTriggerProcessor {

    @Autowired
    private MassValidationService massValidationService;

    @StreamListener(UserUploadProcessingQueueSink.INPUT)
    public void onProcessUploadMessage(Long uploadId)
    {
        massValidationService.run(uploadId);
    }
}
