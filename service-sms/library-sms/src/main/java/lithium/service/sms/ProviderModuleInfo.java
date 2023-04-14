package lithium.service.sms;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;

@RestController
public class ProviderModuleInfo extends ModuleInfoAdapter {
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/callback/**").permitAll();
	}
}