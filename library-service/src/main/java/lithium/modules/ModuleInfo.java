package lithium.modules;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lithium.menu.MenuItem;
import lithium.service.Response;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.role.client.objects.Role;

@FeignClient(name="modules", path="/modules")
public interface ModuleInfo {
	public void configureHttpSecurity(HttpSecurity http) throws Exception;
	
	@RequestMapping(path="/menu", method=RequestMethod.GET)
	public Response<List<MenuItem>> getMenuItems();
	
	@RequestMapping(path="/roles", method=RequestMethod.GET)
	public Response<List<Role>> getRoles();
	
	@RequestMapping(path="/providers", method=RequestMethod.GET)
	public Response<List<ProviderConfig>> getProviders();

	public String getModuleName();
}