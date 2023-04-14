package lithium.service.settlement;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class SettlementModuleInfo extends ModuleInfoAdapter {
	public SettlementModuleInfo() {
		Category category = Category.builder().name("Settlement Operations").description("These are all the roles relevant to managing settlements.").build();
		addRole(Role.builder().category(category).name("Settlements Manage").role("SETTLEMENTS_MANAGE").description("Manage settlements.").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/batch/settlements/**").access("@lithiumSecurity.hasRole(authentication, 'SETTLEMENTS_MANAGE')");
		http.authorizeRequests().antMatchers("/settlement/{id}/**").access("@lithiumSecurity.hasRole(authentication, 'SETTLEMENTS_MANAGE')");
		http.authorizeRequests().antMatchers("/settlements/**").access("@lithiumSecurity.hasRole(authentication, 'SETTLEMENTS_MANAGE')");
		// @formatter:on
	}
}