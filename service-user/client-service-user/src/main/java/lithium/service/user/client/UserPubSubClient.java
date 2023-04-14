package lithium.service.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-user")
public interface UserPubSubClient {
    @RequestMapping(method=RequestMethod.POST, value="/system/user/pub-sub/push")
    ResponseEntity<String> pushToPubSub(@RequestParam(value = "guid") String guid);
}
