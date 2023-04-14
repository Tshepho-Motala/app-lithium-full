package lithium.ui.network.admin;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.cache.CacheClearMessageSender;
import lithium.cache.EnableCacheClearMessageSender;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableZuulProxy
@RestController
@EnableCacheClearMessageSender
@EnableLithiumServiceClients
public class UiNetworkAdminApplication extends LithiumServiceApplication {
	@Autowired
	CacheClearMessageSender cacheClearMessageSender;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(UiNetworkAdminApplication.class, args);
	}
	
	@RequestMapping("/clearcache")
	public void sendClearCacheMessage(@RequestParam String cacheNameRegEx) {
		cacheClearMessageSender.clearCache(cacheNameRegEx);
	}
	
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}
	
	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class UISecurityConfig extends WebSecurityConfigurerAdapter {
		
		@Override
		public void init(WebSecurity web) {
			web.ignoring().antMatchers(
				"/**"
//				"/index.html",
//				"/scripts/**",
//				"/fonts/**",
//				"/wro/**", 
//				"/webjars/**", 
//				"/views/**",
//				"/auth/**",
//				"/services/**"
			);
		}
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
					.anyRequest().authenticated().and()
					.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
					.csrf().csrfTokenRepository(csrfTokenRepository()).ignoringAntMatchers("/auth/**");
		}
		
		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}
	}
	
	@Component
	public class UIModuleInfo extends ModuleInfoAdapter {
		@Override
		public void configureHttpSecurity(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/clearcache").access("@lithiumSecurity.authenticatedSystem(authentication)");
			http.authorizeRequests().antMatchers("/translations/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		}	
	}
}
