package lithium.service.user.mock.vipps;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;

@RestController
public class VippsMockModuleInfo extends ModuleInfoAdapter {
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
//		super.configureHttpSecurity(http);
		http.authorizeRequests().anyRequest().permitAll();
	}
}
