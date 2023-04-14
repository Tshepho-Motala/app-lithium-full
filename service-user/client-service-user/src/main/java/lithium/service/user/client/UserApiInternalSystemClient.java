package lithium.service.user.client;

import lithium.service.Response;
import lithium.service.user.client.objects.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-user")
public interface UserApiInternalSystemClient {

    @RequestMapping(method = RequestMethod.POST, value = "/system/user-api-internal/user/create")
    Response<User> createStub(@RequestParam("domainName") String domainName, @RequestParam("userName") String userName) throws Exception;

}
