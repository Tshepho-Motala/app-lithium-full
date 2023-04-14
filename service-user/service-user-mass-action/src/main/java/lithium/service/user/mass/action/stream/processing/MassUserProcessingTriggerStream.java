package lithium.service.user.mass.action.stream.processing;

import lithium.service.user.mass.action.data.entities.FileData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MassUserProcessingTriggerStream {

    @Autowired
    private MassUserProcessingTriggerOuputQueue massUserProcessingTriggerOuputQueue;

    public void trigger(FileData fileData)
    {
        try {
            massUserProcessingTriggerOuputQueue.processUser()
                    .send(MessageBuilder.withPayload(fileData.getId()).build());
        }
        catch (Exception exception) {
            log.error("Failed to trigger processing for user: " + fileData.getUploadedPlayerId(), exception);
        }
    }
}
