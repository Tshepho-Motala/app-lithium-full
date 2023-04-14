package lithium.service.cashier.processor.callback;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lithium.modules.ModuleInfoAdapter;

public class DoProcessorCallbackModuleInfo extends ModuleInfoAdapter {
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/internal/callback/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}