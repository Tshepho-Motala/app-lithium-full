package lithium.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="service", path="/")
public interface ServiceClient {
	@RequestMapping("/name")
	public String name();
}
