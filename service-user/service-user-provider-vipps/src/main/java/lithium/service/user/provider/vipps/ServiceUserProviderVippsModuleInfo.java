package lithium.service.user.provider.vipps;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.user.provider.vipps.config.Config;

@RestController
public class ServiceUserProviderVippsModuleInfo extends ModuleInfoAdapter {
//	@Autowired
//	private OAuth2ClientContext oauth2ClientContext;
//	@Autowired
//	private AuthorizationCodeResourceDetails vipps;
//	@Autowired
//	private ResourceServerProperties vippsResource;
	
	public ServiceUserProviderVippsModuleInfo() {
		addProvider(ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.AUTH)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.BASE_URL.property())
				.tooltip("Base URL for the endpoint to get the access token (that needs to be passed in every Vipps api call) and login requests.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CLIENT_ID.property())
				.tooltip("Login/Signup Client ID received when merchant registered the application.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CLIENT_SECRET.property())
				.tooltip("Login/Signup Client Secret received when merchant registered the application.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_SERIAL_NUMBER.property())
				.tooltip("Login/Signup Serial Number. Found under Applications.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIPTION_KEY_ACCESS_TOKEN.property())
				.tooltip("Subscription key which provides access to this API (Access Token). Found in your Profile.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIPTION_KEY_LOGIN.property())
				.tooltip("Subscription key which provides access to this API (login / Signup). Found in your Profile.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CALLBACK_PREFIX.property())
				.tooltip("URL prefix for Vipps to send the request status with user details.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CONSENT_REMOVE_PREFIX.property())
				.tooltip("This callback will be used for informing merchant about consent removal from Vipps user.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_FALLBACK_URL.property())
				.tooltip("Vipps will use the fall back URL to redirect Merchant Page once Payment is completed in Vipps System")
				.build()
			)
			.build()
		);
		
		addProvider(ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.USER)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.BASE_URL.property())
				.tooltip("Base URL for the endpoint to get the access token (that needs to be passed in every Vipps api call) and login requests.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CLIENT_ID.property())
				.tooltip("Login/Signup Client ID received when merchant registered the application.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CLIENT_SECRET.property())
				.tooltip("Login/Signup Client Secret received when merchant registered the application.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_SERIAL_NUMBER.property())
				.tooltip("Login/Signup Serial Number. Found under Applications.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIPTION_KEY_ACCESS_TOKEN.property())
				.tooltip("Subscription key which provides access to this API (Access Token). Found in your Profile.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.SUBSCRIPTION_KEY_LOGIN.property())
				.tooltip("Subscription key which provides access to this API (login / Signup). Found in your Profile.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CALLBACK_PREFIX.property())
				.tooltip("URL prefix for Vipps to send the request status with user details.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_CONSENT_REMOVE_PREFIX.property())
				.tooltip("This callback will be used for informing merchant about consent removal from Vipps user.")
				.build()
			)
			.property(
				ProviderConfigProperty.builder()
				.name(Config.LOGIN_FALLBACK_URL.property())
				.tooltip("Vipps will use the fall back URL to redirect Merchant Page once Payment is completed in Vipps System")
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