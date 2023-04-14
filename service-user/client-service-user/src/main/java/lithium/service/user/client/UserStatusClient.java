package lithium.service.user.client;

import lithium.service.Response;
import lithium.service.user.client.objects.Status;
import lithium.service.user.client.objects.StatusReason;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name="service-user")
public interface UserStatusClient {

    @RequestMapping(value = "/system/user-status/get-all-statuses", method = RequestMethod.POST)
    public Response<List<Status>> getAllUserStatuses();

    @RequestMapping(value = "/system/user-status/get-all-status-reasons", method = RequestMethod.POST)
    public Response<List<StatusReason>> getAllStatusReasons();
}
