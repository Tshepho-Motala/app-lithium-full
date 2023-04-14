package lithium.service.report.players;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceReportPlayersModuleInfo extends ModuleInfoAdapter {

	public ServiceReportPlayersModuleInfo() {
		Category reporting = Category.builder().name("Reporting").description("Reporting access.").build();
		Category gamstopReport = Category.builder().name("Gamstop Report").description("Gamstop report access.").build();
		addRole(Role.builder().category(reporting).name("Full player reports").role("REPORT_PLAYERS").description("Report on all aspects of players").build());
		addRole(Role.builder().category(gamstopReport).name("View Gamstop report").role("GAMSTOP_VIEW").description("View Gamstop report").build());
		addRole(Role.builder().category(gamstopReport).name("Export Gamstop report").role("GAMSTOP_EXPORT").description("Export Gamstop report").build());
		addRole(Role.builder().category(gamstopReport).name("Edit Gamstop report").role("GAMSTOP_EDIT").description("Edit Gamstop report").build());
	}

	@PostConstruct
	public void init() {
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/report/players/*/runs/*/xls").permitAll();
		http.authorizeRequests().antMatchers("/report/players/*/runs/*/csv").permitAll();
		http.authorizeRequests().antMatchers( "/report/players/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'REPORT_PLAYERS', 'GAMSTOP_VIEW', 'GAMSTOP_EXPORT', 'GAMSTOP_EDIT')");
		// @formatter:on
	}

}
