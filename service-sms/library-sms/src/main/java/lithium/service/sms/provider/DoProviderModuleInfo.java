package lithium.service.sms.provider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lithium.modules.ModuleInfoAdapter;

public class DoProviderModuleInfo extends ModuleInfoAdapter {
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}