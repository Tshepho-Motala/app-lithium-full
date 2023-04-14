package lithium.service.entity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class EntityModuleInfo extends ModuleInfoAdapter {

	public EntityModuleInfo() {
		Category groupCategory = Category.builder().name("Entity Operations").description("Managing of entities").build();
		addRole(Role.builder().category(groupCategory).name("Manage All Entities").role("ENTITIES_MANAGE").description("Manage all available entities").build());
		addRole(Role.builder().category(groupCategory).name("Manage User's Entities").role("USER_ENTITIES_MANAGE").description("View entities assigned to user").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
			.antMatchers("/status/all").authenticated()
			.antMatchers("/entities/**").access("@lithiumSecurity.hasRole(authentication, 'ENTITIES_MANAGE')")
			.antMatchers("/entity/**").access("@lithiumSecurity.hasRole(authentication, 'ENTITIES_MANAGE')")
			.antMatchers("/user/entities/**").access("@lithiumSecurity.hasRole(authentication, 'USER_ENTITIES_MANAGE')")
			.antMatchers("/entitytypes/**").access("@lithiumSecurity.hasRole(authentication, 'ENTITIES_MANAGE')")
		;
		// @formatter:on
	}
}