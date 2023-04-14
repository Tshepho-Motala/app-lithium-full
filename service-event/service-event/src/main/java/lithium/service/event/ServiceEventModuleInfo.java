package lithium.service.event;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceEventModuleInfo extends ModuleInfoAdapter {
	public ServiceEventModuleInfo() {
		super();
//		Category category = Category.builder().name("Affiliate Operations").description("These are all the roles relevant to managing affiliates.").build();
//		addRole(Role.builder().category(category).name("Affiliate Modify").role("AFFILIATE_MODIFY").description("Add/Edit an affiliate").build());
//		addRole(Role.builder().category(category).name("Affiliate View").role("AFFILIATE_VIEW").description("View/List affiliate(s)").build());
//		addRole(Role.builder().category(category).name("Affiliate Player Modify").role("AFFILIATE_PLAYER_MODIFY").description("Add/Edit an affiliate player").build());
//		addRole(Role.builder().category(category).name("Affiliate Player View").role("AFFILIATE_PLAYER_VIEW").description("View/List affiliate player(s)").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// (Can't configure antMatchers after anyRequest.) There are no http endpoints in this service. Deny all by default.
//		http.authorizeRequests().anyRequest().access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}
