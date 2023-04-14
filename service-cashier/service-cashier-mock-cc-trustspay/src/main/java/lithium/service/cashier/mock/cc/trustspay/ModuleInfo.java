package lithium.service.cashier.mock.cc.trustspay;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;

@RestController
public class ModuleInfo extends ModuleInfoAdapter {
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/TestTPInterface").permitAll();
	}
}
