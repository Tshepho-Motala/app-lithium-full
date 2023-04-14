package lithium.service.avatar.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="service-avatar")
public interface AvatarClient {
}
