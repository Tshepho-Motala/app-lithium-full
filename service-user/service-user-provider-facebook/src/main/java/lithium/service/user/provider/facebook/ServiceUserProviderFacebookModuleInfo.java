package lithium.service.user.provider.facebook;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.user.provider.facebook.config.FacebookConfigurationProperties.Config;

@RestController
public class ServiceUserProviderFacebookModuleInfo extends ModuleInfoAdapter {
//	@Autowired
//	private OAuth2ClientContext oauth2ClientContext;
//	@Autowired
//	private AuthorizationCodeResourceDetails vipps;
//	@Autowired
//	private ResourceServerProperties vippsResource;
	
	public ServiceUserProviderFacebookModuleInfo() {
		addProvider(ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.AUTH)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.APP_ID.property())
				.tooltip("")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.APP_SECRET.property())
				.tooltip("")
				.build()
			)
			.build()
		);
		
		addProvider(ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.USER)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.APP_ID.property())
				.tooltip("")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.APP_SECRET.property())
				.tooltip("")
				.build()
			)
			.build()
		);
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		// @formatter:off
		http.authorizeRequests().anyRequest().permitAll();
//			.antMatchers(HttpMethod.GET, "/users/user").access("@lithiumSecurity.authenticatedSystem(authentication)")
//			.and()
//			.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
		// @formatter:on
	}
	
//	private Filter ssoFilter() {
//		OAuth2ClientAuthenticationProcessingFilter vippsFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/vipps");
//		OAuth2RestTemplate vippsTemplate = new OAuth2RestTemplate(vipps, oauth2ClientContext);
//		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
//		interceptors.add(new LoggingRequestInterceptor());
//		vippsTemplate.setInterceptors(interceptors);
//		vippsFilter.setRestTemplate(vippsTemplate);
//		UserInfoTokenServices tokenServices = new UserInfoTokenServices(
//			vippsResource.getUserInfoUri(),
//			vipps.getClientId()
//		);
//		tokenServices.setRestTemplate(vippsTemplate);
//		vippsFilter.setTokenServices(tokenServices);
//		return vippsFilter;
//	}
}