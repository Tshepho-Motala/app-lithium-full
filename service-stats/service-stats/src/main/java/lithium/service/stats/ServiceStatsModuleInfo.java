package lithium.service.stats;

import javax.annotation.PostConstruct;

import lithium.service.role.client.objects.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;

@Component
public class ServiceStatsModuleInfo extends ModuleInfoAdapter {
	public ServiceStatsModuleInfo() {
		roles();
	}

	private void roles() {
		Role.Category dashboardStatsRole = Role.Category.builder().name("Dashboard Stats").description("Viewing of dashboard stats").build();
		addRole(Role.builder().category(dashboardStatsRole).name("Dashboard Stats View").role("DB_STATS_VIEW").description("View all dashboard stats").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/backoffice/dashboard/stats/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DB_STATS_VIEW', 'DASHBOARD')")
		.antMatchers(HttpMethod.GET, "/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
		;
		// @formatter:on
	}
}
