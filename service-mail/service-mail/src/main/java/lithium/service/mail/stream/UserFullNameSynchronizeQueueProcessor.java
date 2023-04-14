package lithium.service.mail.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.mail.services.UserService;
import lithium.service.user.client.objects.UserSynchronizeData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
@EnableBinding(UserFullNameSynchronizeQueueSink.class)
public class UserFullNameSynchronizeQueueProcessor {
    private final UserService userService;
    private final ObjectMapper mapper;

    @StreamListener(UserFullNameSynchronizeQueueSink.INPUT)
    public void trigger(String request) {
        log.debug("A request has been received by service mail to update user:" + request);
        try {
            UserSynchronizeData incomeUser = mapper.readValue(request, UserSynchronizeData.class);
            lithium.service.mail.data.entities.User innerUser = userService.findOrCreate(incomeUser.getGuid());
            innerUser.setLastName(incomeUser.getLastName());
            innerUser.setFirstName(incomeUser.getFirstName());
            userService.saveUser(innerUser);

        } catch (IOException e) {
            log.error("Got user synchronize data, but can't update user because:" + e + " skipped");
        }
    }
}
