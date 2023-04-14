package lithium.service.user.mass.action.stream.uservalidation;

import lithium.service.user.mass.action.objects.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserValidationTriggerStream {

    @Autowired
    private UserValidationTriggerOutputQueue userValidationTriggerOutputQueue;

    public void processUploadedRecord(UserValidation userValidation)
    {
        try {
            userValidationTriggerOutputQueue.userValidationOutputStream()
                    .send(MessageBuilder.withPayload(userValidation).build());
        }
        catch (Exception exception) {
            log.error("Failed to trigger validation for user: " + userValidation.getUploadedPlayerId(), exception);
        }
    }
}
