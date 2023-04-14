package lithium.service.user.client.stream.synchronize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserSynchronizeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSynchronizeStream {
    @Autowired
    private UserSynchronizeOutputQueue outputQueue;

    public void announceUserChanges(User user) {
        UserSynchronizeData data = UserSynchronizeData.builder()
                .guid(user.guid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        try {
            outputQueue.userSynchronizeChannel().send(MessageBuilder.withPayload(new ObjectMapper().writeValueAsString(data)).build());
        } catch (JsonProcessingException e) {
            log.error("Can't send user synchronize data for user:" + user + ". Because of:" + e.getMessage());
        }
    }
}
