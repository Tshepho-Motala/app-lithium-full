package lithium.server.oauth2;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.hazelcast.EnableHazelcastClient;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.translate.client.EnableTranslationsService;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.service.user.client.service.EnableUserPlayTimeClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Controller
@EnableTranslationsService
@EnableTranslationsStream
@EnableResourceServer
@SpringBootApplication
@EnableDiscoveryClient
@EnableDomainClient
@EnableLithiumMetrics
@EnableLithiumServiceClients
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableUserPlayTimeClientService
@EnableHazelcastClient
@SessionAttributes("authorizationRequest")
public class ServerOath2Application extends WebMvcConfigurerAdapter {
	@RequestMapping("/user")
	@ResponseBody
	public Principal user(Principal user) {
		return user;
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/oauth/confirm_access").setViewName("authorize");
	}

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServerOath2Application.class, args);
	}


    @Configuration
    @Order(-20)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

		@Override
		public void init(WebSecurity web) {
			web.ignoring().antMatchers("/health");
			web.ignoring().antMatchers("/token/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.httpBasic().disable()
				.formLogin().loginPage("/login").permitAll()
			.and()
				.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access")
			.and()
				.authorizeRequests().anyRequest().authenticated();
			// @formatter:on
		}
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManager);
		}
	}

	@Slf4j
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
		@Autowired AuthenticationManager authenticationManager;
		@Autowired LithiumServiceClientFactory services;
		@Autowired CachingDomainClientService cachingServices;
		@Autowired UserDetailsService userDetailsService;
		@Autowired ProviderAuthClientDetailsService providerAuthClientDetailsService;

		@Value("${lithium.token.jwt.private-key-content}")
		private String privateKeyContent;
		@Value("${lithium.token.jwt.public-key-content}")
		private String publicKeyContent;

		@Value("${lithium.token.jwt.access-token-validity-seconds:2678400}") //31 Days
		private Integer accessTokenValiditySeconds;
		@Value("${lithium.token.jwt.refresh-token-validity-seconds:2592000}") //30 Days
		private Integer refreshTokenValiditySeconds;
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(providerAuthClientDetailsService);
		}
		
		@Bean
		public JwtTokenStore tokenStore() {
			JwtTokenStore store = new JwtTokenStore(tokenEnhancer());
			return store;
		}

		private KeyPair keyPair() {
			try {
				privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
				publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
				log.trace("privateKeyContent:: "+privateKeyContent);
				log.trace("publicKeyContent:: "+publicKeyContent);
				KeyFactory kf = KeyFactory.getInstance("RSA");

				PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
				PrivateKey privateKey = kf.generatePrivate(keySpecPKCS8);

				X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
				RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
				KeyPair kp = new KeyPair(publicKey, privateKey);
				return kp;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return null;
		}

		@Bean
		public JwtAccessTokenConverter tokenEnhancer() {
			final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
			jwtAccessTokenConverter.setKeyPair(keyPair());
			((DefaultAccessTokenConverter) jwtAccessTokenConverter.getAccessTokenConverter()).setUserTokenConverter(userAuthenticationConverter());
			return jwtAccessTokenConverter;
		}
		
		@Bean
		public TokenEnhancerChain tokenEnhancerChain() {
			final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(
				Arrays.asList(
					LithiumTokenEnhancer.builder()
						.cachingServices(cachingServices)
						.services(services)
						.build(),
					tokenEnhancer()
				)
			);
			return tokenEnhancerChain;
		}
		
		@Bean
		public UserAuthenticationConverter userAuthenticationConverter() {
			LithiumUserAuthenticationConverter lithiumUserAuthenticationConverter = new LithiumUserAuthenticationConverter();
			lithiumUserAuthenticationConverter.setUserDetailsService(userDetailsService);
			return lithiumUserAuthenticationConverter;
		}
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			// @formatter:off
			endpoints
				.tokenServices(defaultTokenServices())
				.userDetailsService(userDetailsService)
				.authenticationManager(authenticationManager)
				.reuseRefreshTokens(false)
				.exceptionTranslator(new WebResponseExceptionTranslator());
			// @formatter:on
		}
		
		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		}
		
		@Bean
		@Primary
		public DefaultTokenServices defaultTokenServices() {
			final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
			defaultTokenServices.setTokenStore(tokenStore());
			defaultTokenServices.setTokenEnhancer(tokenEnhancerChain());
			defaultTokenServices.setSupportRefreshToken(true);
			defaultTokenServices.setReuseRefreshToken(false);
			
			log.info("Setting Token Validity : accessTokenValiditySeconds: "+accessTokenValiditySeconds+" refreshTokenValiditySeconds: "+refreshTokenValiditySeconds);
			defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
			defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
			return defaultTokenServices;
		}
	}
}
