package lithium.ui.network.admin;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.tokens.JWTUser;
import lithium.tokens.LithiumTokenUtil;
import lithium.ui.network.admin.filter.ExceptionFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Optionals;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
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
@EnableTranslationsStream
@EnableCacheClearMessageSender
@EnableLithiumServiceClients
@EnableLocaleContextProcessor
@EnableDomainClient
@Slf4j
public class UiNetworkAdminApplication extends LithiumServiceApplication {
	@Autowired
	CacheClearMessageSender cacheClearMessageSender;
	@Autowired
	TokenStore tokenStore;

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
	@Order(SecurityProperties.BASIC_AUTH_ORDER - 2)
	protected static class UISecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		public void init(WebSecurity web) {
			web.ignoring().antMatchers(
				"/",
				"/manifest.json",
				"/OneSignalSDKWorker.js",
				"/OneSignalSDKUpdaterWorker.js",
				"/index.html",
				"/scripts/**",
				"/fonts/**",
				"/wro/**",
				"/webjars/**",
				"/views/**",
				"/auth/**",
				"/services/**",
				"/assets/**",
				"/css/**",
				"/js/**",
				"/images/**",
				"/favicon.*", "/*/icon-*",
				"/templates/**",
				"/version",
				"/error"
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

	/**
	 *
	 * @return
	 */
	@Bean
	public ExceptionFilter exceptionFilter() {
		return new ExceptionFilter();
	}

	@Bean
	public ZuulFilter zuulFilter(){
		return new ZuulFilter() {
			@Override
			public String filterType() {
				return "post";
			}

			@Override
			public int filterOrder() {
				return 999999;
			}

			@Override
			public boolean shouldFilter() {
				return true;
			}

			@Override
			public Object run() {
				final List<String> routingDebug = (List<String>) RequestContext.getCurrentContext().get("routingDebug");
				routingDebug.forEach(log::trace);

				Optional<String> guid = Optional.ofNullable(RequestContext.getCurrentContext().getRequest())
						.map(request -> request.getHeader(HttpHeaders.AUTHORIZATION))
						.filter(token -> token.startsWith("Bearer"))
						.map(token -> token.replace("Bearer ", ""))
						.map(token -> LithiumTokenUtil.builder(tokenStore, token).build())
						.map(LithiumTokenUtil::getJwtUser)
						.map(JWTUser::getGuid);

				Optional<String> requestURI = routingDebug.stream()
						.filter(s -> s.contains("requestURI"))
						.findFirst();

				Optionals.ifAllPresent(requestURI, guid, (r, g) -> log.debug(r + " :: User: " + g));

				return null;
			}
		};
	}

	// Need it as long as we have zuul dependencies (outdated) to avoid of creating RibbonLoadBalancerClient in RibbonAutoConfiguration,
	// as it is incompatible with the newer LoadBalancerClient interface (spring-cloud-common starting v3.0).
	//Without it our openfeign clients can't find hosts (e.g. service-translate) and throw UnknownHostException.
	//TODO: should be removed after removing zuul
	@Bean
	public LoadBalancerClient loadBalancerClient(@Autowired ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
		return new BlockingLoadBalancerClient(loadBalancerClientFactory);
	}
}
