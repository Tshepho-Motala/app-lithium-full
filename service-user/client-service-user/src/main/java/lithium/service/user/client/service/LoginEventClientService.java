package lithium.service.user.client.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.LoginEventBO;
import lithium.service.user.client.objects.LoginEventQuery;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Slf4j
@Service
public class LoginEventClientService {
    @Autowired LithiumServiceClientFactory factory;
    @Autowired MessageSource messageSource;

    public LoginEvent findLastLoginEventByUserGuidOrNull(
        String userGuid
    ) {
        try {
            return findLastLoginEventByUserGuid(userGuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public LoginEvent findLastLoginEventByUserGuid(
        String userGuid
    )
    throws
    Status500InternalServerErrorException {
        try {
            LoginEvent loginEvent = getClient().getLastLoginEventForUser(userGuid);

            if (loginEvent.getLogout() != null && loginEvent.getLogout().before(Calendar.getInstance().getTime())) {
                throw new Status500InternalServerErrorException(
                    "Player Guid : ".concat(userGuid)
                    .concat(" ended : ")
                    .concat(loginEvent.getLogout().toString())
                );
            }
            return loginEvent;
        } catch (Status500InternalServerErrorException e) {
            log.error("Player already logged out : " + e);
            throw e;
        } catch (Exception e) {
            log.error("Could not retrieve session info.", e);
            throw new Status500InternalServerErrorException("Could not retrieve session for Player Guid : ".concat(userGuid));
        }
    }

    private SystemLoginEventsClient getClient()
    throws
    LithiumServiceClientFactoryException {
        return factory.target(
                SystemLoginEventsClient.class,
                true
        );
    }

    public DataTableResponse<LoginEventBO> search(LoginEventQuery loginEventQuery) throws Status500InternalServerErrorException {
        try {
            return getClient().search(loginEventQuery);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Failed to retrieve login events for user {}, {}", loginEventQuery.getUserGuid(), e);
            throw new Status500InternalServerErrorException("Failed to retrieve login events for user "+ loginEventQuery.getUserGuid());
        }
    }
}
