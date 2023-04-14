package lithium.service.user.mass.action.stream.uservalidation;

import lithium.service.user.mass.action.objects.UserValidation;
import lithium.service.user.mass.action.services.MassValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserValidationTriggerQueueProcessor {
    @Autowired
    private MassValidationService massValidationService;


    @StreamListener(UserValidationQueueSink.INPUT)
    public void onValidateMessage(UserValidation userValidation) {
        massValidationService.validate(userValidation);
    }
}
