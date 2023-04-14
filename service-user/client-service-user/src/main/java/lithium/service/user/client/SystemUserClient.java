package lithium.service.user.client;

import lithium.service.user.client.objects.ContactDetails;
import lithium.service.user.client.objects.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-user")
public interface SystemUserClient {
    @PostMapping("/system/user/set-contact-details-validated")
    public User validateContactDetails(@RequestBody ContactDetails contactDetails);
}
