package lithium.service.cashier;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;

@RestController
public class ProcessorModuleInfo extends ModuleInfoAdapter {
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/frontend/**").authenticated();
		http.authorizeRequests().antMatchers("/callback/**").permitAll();
		http.authorizeRequests().antMatchers("/public/**").permitAll();
	}
}
