package lithium.service.cashier.processor;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lithium.modules.ModuleInfoAdapter;

public class DoProcessorModuleInfo extends ModuleInfoAdapter {

	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/frontend/**").authenticated();
	}

}
