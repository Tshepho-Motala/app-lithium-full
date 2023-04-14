package lithium.service.exchange;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;

@Component
public class ServiceExchangeModuleInfo extends ModuleInfoAdapter {
	public ServiceExchangeModuleInfo() {
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST,"/exchange").access("@lithiumSecurity.authenticatedSystem(authentication)")
				.antMatchers("/exchange/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}