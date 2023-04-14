package lithium.service.limit.services;


import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.ExceptionMessageUtil;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PubSubUserAccountChangeProxy {
    private final UserApiInternalClientService internalClientService;

    public void listenAccountChanges(String guid) {
        try {
            internalClientService.pushUserUpdateToPubSubUserService(guid);
        } catch (UserClientServiceFactoryException | UserNotFoundException e) {
            log.warn("can't push user updates to pub sub service, cause by - " + ExceptionMessageUtil.allMessages(e));
        }
    }
}
