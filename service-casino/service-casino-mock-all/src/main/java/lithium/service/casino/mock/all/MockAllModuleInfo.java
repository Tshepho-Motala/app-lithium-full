package lithium.service.casino.mock.all;

import java.util.ArrayList;

import lithium.service.casino.mock.all.controllers.MockController;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;

@RestController
public class MockAllModuleInfo extends ModuleInfoAdapter {
	MockAllModuleInfo() {
		super();
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);

		//http.authorizeRequests().antMatchers("/casino/mock/**").permitAll();//.access("@lithiumSecurity.authenticateSystem(authentication)");
		//http.authorizeRequests().antMatchers("/*").permitAll();//.access("@lithiumSecurity.authenticateSystem(authentication)");
		http.authorizeRequests().anyRequest().permitAll();
		http.headers().frameOptions().disable();
	}

	@Bean(name="mockController")
	public MockController mockController() {
		return new MockController();
	}
}
