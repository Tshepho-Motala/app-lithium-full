package lithium.service.report.games;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceReportGamesModuleInfo extends ModuleInfoAdapter {
	public ServiceReportGamesModuleInfo() {
		Category c = Category.builder().name("Reporting").description("Reporting access.").build();
		addRole(Role.builder().category(c).name("Full games reporting").role("REPORT_GAMES").description("Report on all aspects of games").build());
	}
	
	@PostConstruct
	public void init() {
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/report/games/*/runs/*/xls").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/report/games/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'REPORT_GAMES')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/report/games/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'REPORT_GAMES')");
		// @formatter:on
	}
}