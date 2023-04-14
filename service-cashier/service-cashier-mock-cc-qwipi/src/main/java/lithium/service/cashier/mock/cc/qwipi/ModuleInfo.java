package lithium.service.cashier.mock.cc.qwipi;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;

@RestController
public class ModuleInfo extends ModuleInfoAdapter {
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/universal3DS/**").permitAll();
		http.authorizeRequests().antMatchers("/universalS2S/**").permitAll();
		http.authorizeRequests().antMatchers("/manual/**").permitAll();
	}
}
