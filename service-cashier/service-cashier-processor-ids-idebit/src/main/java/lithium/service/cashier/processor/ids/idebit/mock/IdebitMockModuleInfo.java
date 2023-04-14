package lithium.service.cashier.processor.ids.idebit.mock;

import lithium.modules.ModuleInfoAdapter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.stereotype.Component;

@Configuration
class IdebitMockSecurity extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/consumer/**").permitAll();
		http.authorizeRequests().antMatchers("/service/**").permitAll();
		// @formatter:on
	}

}
