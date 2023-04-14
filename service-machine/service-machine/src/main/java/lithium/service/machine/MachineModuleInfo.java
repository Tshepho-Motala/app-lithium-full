package lithium.service.machine;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
@EnableDomainClient
public class MachineModuleInfo extends ModuleInfoAdapter {
	public MachineModuleInfo() {
		Category groupCategory = Category.builder().name("Machine Operations").description("Managing of machines").build();
		addRole(Role.builder().category(groupCategory).name("Manage All Machines").role("MACHINES_MANAGE").description("Manage all machines").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
			.antMatchers("/status/all").authenticated()
			.antMatchers("/machines/**").access("@lithiumSecurity.hasRole(authentication, 'MACHINES_MANAGE')")
			.antMatchers("/machine/**").access("@lithiumSecurity.hasRole(authentication, 'MACHINES_MANAGE')")
			.antMatchers("/{domainName}/machines/stats/{granularity}/{type}").access("@lithiumSecurity.hasRole(authentication, 'MACHINES_MANAGE')")
		;
		// @formatter:on
	}
}