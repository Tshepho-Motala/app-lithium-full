package lithium.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.menu.MenuItem;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.role.client.objects.Role;
import lithium.translations.Translation;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString
@RestController
public class ModuleInfoAdapter implements ModuleInfo {
	@Getter
	@Value("${spring.application.name}")
	private String moduleName;
	
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();
	private List<Role> roles = new ArrayList<Role>();
	private List<Translation> translations = new ArrayList<Translation>();
	private HashMap<ProviderType, List<ProviderConfig>> providers = new HashMap<ProviderType, List<ProviderConfig>>();

	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**/modules/providers").access("@lithiumSecurity.hasRole(authentication, 'PROVIDER_EDIT')");
	}
	
	public void addMenuItem(MenuItem item) {
		menuItems.add(item);
	}
	
	@RequestMapping(path="modules/menu")
	public Response<List<MenuItem>> getMenuItems() {
		return Response.<List<MenuItem>>builder().data(menuItems).status(Status.OK).build();
	}
	
	public void addRole(Role item) {
		roles.add(item);
	}
	
	@RequestMapping(path="modules/roles")
	public Response<List<Role>> getRoles() {
		return Response.<List<Role>>builder().data(roles).status(Status.OK).build();
	}
	
	public void addProvider(ProviderConfig item) {
		if (!providers.containsKey(item.getType())) {
			providers.put(item.getType(), new ArrayList<ProviderConfig>());
		}
		providers.get(item.getType()).add(item);
	}
	
	@RequestMapping(path="modules/providers")
	public Response<List<ProviderConfig>> getProviders() {
		//ProviderType pt = ProviderType.valueOf(providerTypeString.toUpperCase()	);
		List<ProviderConfig> list = new ArrayList<>();
		for (List<ProviderConfig> p : providers.values()) {
			list.addAll(p);
		}
		return Response.<List<ProviderConfig>>builder().data(list).status(Status.OK).build();
	}
	
}
