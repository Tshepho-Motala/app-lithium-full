package lithium.service.report.player.trans;

import lithium.modules.ModuleInfoAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceReportPlayerTransModuleInfo extends ModuleInfoAdapter {
	
	public ServiceReportPlayerTransModuleInfo() {
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
//		// @formatter:off
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/findByDateRangeAndUserGuid").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/findByDateRangeAndUserGuid").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/generateByDateRangeAndUserGuid").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/generateByDateRangeAndUserGuid").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_ACCOUNTING_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/xls").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/xls").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW')");
		// @formatter:on
	}
}
