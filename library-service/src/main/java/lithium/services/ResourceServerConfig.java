package lithium.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

import lithium.modules.ModuleInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Autowired(required=true)
	private ModuleInfo[] modules;
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private OAuth2WebSecurityExpressionHandler expressionHandler;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.httpBasic().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		// For debugging only, never commit enabled
		// http.authorizeRequests().antMatchers("/trace").permitAll();
		// http.authorizeRequests().antMatchers("/menu").permitAll();

		// The Actuator Endpoints (health, metrics, env, info etc) are published on local dev machines under the same
		// port as main services. On the cluster, they are published on a different port, so that they are not
		// world accessible. Thus we can open them all up if needed for local testing.
		// A nice command to use locally to see connection pool usage for instance:
		// watch 'curl -s http://localhost:9300/metrics | jq | grep gauge.lithiumexporter.datasource.primary.active'
		http.authorizeRequests().antMatchers("/health").permitAll();
		http.authorizeRequests().antMatchers("/metrics/**").permitAll();
		http.authorizeRequests().antMatchers("/prometheus").permitAll();
		http.authorizeRequests().antMatchers("/env").permitAll();
		http.authorizeRequests().antMatchers("/info").permitAll();

		http.authorizeRequests().antMatchers("/403").permitAll();
		http.authorizeRequests().antMatchers("/500").permitAll();
		http.authorizeRequests().antMatchers("/version").permitAll();
		http.authorizeRequests().antMatchers("/templates/**").permitAll();
		http.authorizeRequests().antMatchers("/public").permitAll();
		http.authorizeRequests().antMatchers("/users/auth").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/oauth/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/roles/*").authenticated();
		http.authorizeRequests().antMatchers("/user").authenticated();
		http.authorizeRequests().antMatchers("/modules").authenticated();
		http.authorizeRequests().antMatchers("/roles").authenticated();
		http.authorizeRequests().antMatchers("/providers").authenticated();
		http.authorizeRequests().antMatchers("/registertranslations/rerun/all").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/registertranslations/rerun").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/registerroles").access("@lithiumSecurity.authenticatedSystem(authentication)");

		for (ModuleInfo module: modules) {
			log.info("Delegating to " + module.getClass().getName() + " for security configurations");
			module.configureHttpSecurity(http);
		}
		
		http.authorizeRequests().antMatchers("/**").denyAll();
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.expressionHandler(expressionHandler);
		resources.tokenStore(tokenStore);
	}
}
