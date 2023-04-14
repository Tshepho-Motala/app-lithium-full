package lithium.service.role.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@FeignClient(name = "service-user", path = "/roles")
public interface RoleClient {
	@RequestMapping(path="/addrole", method=RequestMethod.POST)
	public Response<Role> addRole(@RequestParam("name") String name, @RequestParam("role") String role, @RequestParam("description") String description, @RequestParam("categoryName") String categoryName);
	@RequestMapping(path="/addcategory", method=RequestMethod.POST)
	public Response<Category> addCategory(@RequestParam("name") String name, @RequestParam("description") String description);
}