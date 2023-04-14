package lithium.service.notifications.client;

import org.springframework.cloud.openfeign.FeignClient;
import lithium.service.notifications.client.objects.InboxSummary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-notifications")
public interface NotificationsClient {

    @RequestMapping(value = "/system/inbox/summary", method = RequestMethod.GET)
    public InboxSummary getInboxSummary(@RequestParam("userGuid") String userGuid);
}