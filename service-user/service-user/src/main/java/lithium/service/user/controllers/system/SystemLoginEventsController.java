package lithium.service.user.controllers.system;

import java.util.Locale;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEventBO;
import lithium.service.user.client.objects.LoginEventFE;
import lithium.service.user.client.objects.LoginEventQuery;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.LoginEventService;
import lithium.service.user.services.UserService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
@RequestMapping("/system/loginevents")
public class SystemLoginEventsController {
    @Autowired LoginEventService loginEventService;
    @Autowired UserService userService;
    @Autowired @Setter MessageSource messageSource;

    @GetMapping(value = "/from-session-key")
    public LoginEvent getLoginEventForSessionKey(@RequestParam("sessionKey") String sessionKey) throws Status412LoginEventNotFoundException {
        Locale locale = Locale.US;  //TODO: This needs to be updated to retrieve from user/domain.
        LoginEvent loginEvent = loginEventService.loginEventForSessionKey(sessionKey);
        if (loginEvent == null) {
            throw new Status412LoginEventNotFoundException(messageSource.getMessage("SERVICE_USER.SYSTEM.LOGINEVENT404", null, locale));
        }

        return loginEvent;
    }

    @GetMapping(value = "/last-login-event")
    public LoginEvent getLastLoginEventForUser(@RequestParam("userGuid") String userGuid) throws Status411UserNotFoundException {
        Locale locale = Locale.US;  //TODO: This needs to be updated to retrieve from user/domain.
        User user = userService.findFromGuid(userGuid);
        if (user == null) {
            throw new Status411UserNotFoundException(messageSource.getMessage("SERVICE_USER.SYSTEM.USERGUID404", null, locale));
        }

        return user.getLastLogin();
    }

    @PostMapping(value = "/search")
    public DataTableResponse<LoginEventBO> getLoginForUser(@RequestBody LoginEventQuery loginEventQuery) {
      return loginEventService.search(loginEventQuery);
    }
}
