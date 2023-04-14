package lithium.service.entity.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.Response;
import lithium.service.entity.client.objects.Entity;

@FeignClient(name="service-entity")
public interface EntityClient {
	@RequestMapping("/entities/findByUuid/{uuid}")
	Response<Entity> findByUuid(@PathVariable("uuid") String uuid);
}
