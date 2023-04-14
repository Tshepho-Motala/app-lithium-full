package lithium.service.domain.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lithium.service.Response;
import lithium.service.domain.client.objects.DomainRole;

@FeignClient(name="service-domain", path="/domain")
public interface DomainRoleClient {
	@RequestMapping(method = RequestMethod.GET, value ="/{domainName}/roles")
	public Response<Iterable<DomainRole>> list(@PathVariable("domainName") String domainName);
}
