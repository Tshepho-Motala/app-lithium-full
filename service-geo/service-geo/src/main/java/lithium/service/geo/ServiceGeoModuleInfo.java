package lithium.service.geo;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;

@Component
public class ServiceGeoModuleInfo extends ModuleInfoAdapter {
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
			.antMatchers("/openapi.yaml").permitAll()
			.antMatchers("/geo/cities/*").permitAll()
			.antMatchers("/geo/level1s/*").permitAll()
			.antMatchers("/geo/countries").permitAll()
			.antMatchers("/geo/countries/**").permitAll()
			.antMatchers("/geo/locationv4").permitAll()
			.antMatchers("/geo/locationFromRequest").permitAll()
			.antMatchers("/neustar/geodirectory/v1/ipinfo/*").permitAll()
			;
		// @formatter:on
	}
}
