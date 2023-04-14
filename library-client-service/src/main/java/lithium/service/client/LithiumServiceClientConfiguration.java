package lithium.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.LithiumFeignClientsRegistrar;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestHeaderParameterProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lithium.systemauth.SystemAuthService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableFeignClients(clients=LithiumServiceClient.class)
@Import(EurekaDiscoveryClientConfig.class)
public class LithiumServiceClientConfiguration {
	
	@Autowired
	SystemAuthService systemAuthService;

	private static final ThreadLocal<Boolean> systemAuthThreadLocal = new ThreadLocal<Boolean>();
	
	public static void useSystemAuthForThisThread(boolean useSystemAuth) {
		systemAuthThreadLocal.set(new Boolean(useSystemAuth));
	}
	
	@Bean
	public LithiumServiceClientFactory lithiumServiceClientFactory() {
		return new LithiumServiceClientFactory();
	}
	
	@Bean
	public LithiumFeignClientsRegistrar registrar() {
		return new LithiumFeignClientsRegistrar();
	}
	
	@Bean
	public RequestInterceptor requestTokenBearerInterceptor() {
		return new RequestInterceptor() {
			public void apply(RequestTemplate requestTemplate) {

				log.debug("RequestInterceptor: "+requestTemplate.toString().trim());

				if (requestTemplate.headers().containsKey("Authorization")) {
					log.debug("Request already contains authorization headers. Not meddling. " + requestTemplate);
					return;
				}
				
				if (systemAuthThreadLocal.get() != null && systemAuthThreadLocal.get()) {
					try {
						requestTemplate.header("Authorization", "bearer " + systemAuthService.getTokenValue());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}	
				} else {
					if (SecurityContextHolder.getContext() != null)
					if (SecurityContextHolder.getContext().getAuthentication() != null)
					if (SecurityContextHolder.getContext().getAuthentication().getDetails() != null)
					if (SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof OAuth2AuthenticationDetails) {
						OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
						String token = details.getTokenValue();
						if (token != null) {
							requestTemplate.header("Authorization", "bearer " + token);
						}
					}
				}
				
				if (!requestTemplate.headers().containsKey("Authorization")) {
					log.debug("No authorization headers added " + requestTemplate);
				} else {
					log.debug("Request authorization header: " + requestTemplate.headers().get("Authorization"));
				}
			}
		};
	}
	
	@Bean
	public RequestParamParameterProcessor requestParamParameterProcessor() {
		return new RequestParamParameterProcessor();
	}
	
	@Bean
	public PathVariableParameterProcessor pathVariableParameterProcessor() {
		return new PathVariableParameterProcessor();
	}
	
	@Bean
	public RequestHeaderParameterProcessor requestHeaderParameterProcessor() {
		return new RequestHeaderParameterProcessor();
	}
}
