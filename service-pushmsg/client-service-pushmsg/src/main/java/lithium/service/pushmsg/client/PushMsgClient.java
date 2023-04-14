package lithium.service.pushmsg.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.pushmsg.client.objects.User;

@FeignClient(name="service-pushmsg")
public interface PushMsgClient {
	@RequestMapping("/{domainName}/pushmsgusers/toggleoptout")
	public Response<User> toggleOptOut(
		@PathVariable("domainName") String domainName,
		@RequestParam("guid") String guid
	);
}