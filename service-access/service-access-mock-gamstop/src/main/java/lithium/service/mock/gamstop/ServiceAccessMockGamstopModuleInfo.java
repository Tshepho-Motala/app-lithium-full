package lithium.service.mock.gamstop;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
public class ServiceAccessMockGamstopModuleInfo extends ModuleInfoAdapter {

	ServiceAccessMockGamstopModuleInfo() {
		super();
		ArrayList<ProviderConfigProperty> properties= new ArrayList<>();
	}
	

	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		http.authorizeRequests().antMatchers("/*").permitAll();
		http.authorizeRequests().antMatchers("/v2/**").permitAll();
		http.authorizeRequests().antMatchers("/swagger-ui/**").permitAll();
		http.authorizeRequests().antMatchers("/v3/**").permitAll();
	}
}
