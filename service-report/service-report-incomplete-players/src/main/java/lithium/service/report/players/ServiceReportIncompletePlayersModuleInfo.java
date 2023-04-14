package lithium.service.report.players;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceReportIncompletePlayersModuleInfo extends ModuleInfoAdapter {
	
	public ServiceReportIncompletePlayersModuleInfo() {
		Category c = Category.builder().name("Reporting").description("Reporting access.").build();
		addRole(Role.builder().category(c).name("Full incomplete player reports").role("REPORT_INCOMPLETE_PLAYERS").description("Report on all aspects of incomplete players").build());
	}

	@PostConstruct
	public void init() {
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/report/players/*/runs/*/xls").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/report/players/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'REPORT_INCOMPLETE_PLAYERS')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/report/players/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'REPORT_INCOMPLETE_PLAYERS')");
		// @formatter:on
	}

}
