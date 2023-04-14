package lithium.service.user.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.user.client.objects.Group;

@FeignClient(name="service-user")
public interface GroupClient {
	@RequestMapping(path = "/groups/roles/{domainName}")
	public Response<List<Group>> listRolesByDomain(@PathVariable("domainName") String domainName) throws Exception;
	@RequestMapping(path = "/groups/list")
	public Response<List<Group>> list() throws Exception;
}