package lithium.service.user.client.system;

import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.LoginEventBO;
import lithium.service.user.client.objects.LoginEventQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-user")
public interface SystemLoginEventsClient {
    @RequestMapping(path = "/system/loginevents/from-session-key")
    LoginEvent findBySessionKey(@RequestParam("sessionKey") String sessionKey) throws Status412LoginEventNotFoundException;

    @RequestMapping(value = "/system/loginevents/last-login-event")
    LoginEvent getLastLoginEventForUser(@RequestParam("userGuid") String userGuid) throws Status411UserNotFoundException;

    @RequestMapping(value = "/system/loginevents/search", method = RequestMethod.POST)
    DataTableResponse<LoginEventBO> search(LoginEventQuery loginEventQuery);
}
