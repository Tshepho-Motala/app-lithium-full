package lithium.service.xp.client;

import lithium.service.xp.client.objects.Level;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.Response;
import lithium.service.xp.client.objects.Scheme;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-xp")
public interface XPClient {
	@RequestMapping(path="/scheme/{domainName}/get")
	public Response<Scheme> getActiveScheme(@PathVariable("domainName") String domainName);

	@RequestMapping(path="/xp/level")
	public Response<Level> getLevel(@RequestParam("userGuid") String userGuid, @RequestParam("domainName") String domainName);
}