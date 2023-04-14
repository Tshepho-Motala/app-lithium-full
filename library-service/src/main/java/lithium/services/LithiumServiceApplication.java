package lithium.services;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import lithium.application.IProbeController;
import lithium.cache.CacheClearMessageSink;
import lithium.config.LithiumConfiguration;
import lithium.config.LithiumConfigurationProperties;
import lithium.datatable.DataTableConfiguration;
import lithium.hazelcast.EnableHazelcastClient;
import lithium.letsencrypt.EnableLetsEncrypt;
import lithium.metrics.EnableLithiumMetrics;
import lithium.metrics.LithiumMetricsConfiguration;
import lithium.modules.ModuleInfo;
import lithium.modules.ModuleInfoAdapter;
import lithium.modules.ModuleInfoStartupNotifier;
import lithium.security.LithiumSecurityEvaluator;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.UserGuidStrategy;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.util.EnableLocaleContextProcessor;
import lithium.service.mail.client.DefaultEmailTemplateRegisterService;
import lithium.service.mail.client.EnableDefaultEmailTemplateRegistrationService;
import lithium.service.mail.client.stream.EnableDefaultEmailTemplateStream;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.stream.EnableRoleRegisterStream;
import lithium.service.role.client.stream.RoleRegisterStream;
import lithium.service.sms.client.DefaultSMSTemplateRegisterService;
import lithium.service.sms.client.EnableDefaultSMSTemplateRegistrationService;
import lithium.service.sms.client.stream.EnableDefaultSMSTemplateStream;
import lithium.service.translate.client.EnableTranslationsService;
import lithium.service.translate.client.TranslationsService;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.tokens.EnableTokenUtilServices;
import lithium.tokens.LithiumTokenUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@LithiumService
@EnableLithiumServiceClients
@EnableLithiumMetrics
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled=true, order=Ordered.LOWEST_PRECEDENCE)
@Import({ DataTableConfiguration.class, LithiumMetricsConfiguration.class, LithiumConfiguration.class })
@EnableBinding({ CacheClearMessageSink.class, ModuleInfoStartupNotifier.class } )
@EnableCaching
@EnableRetry
@EnableAsync
@EnableTranslationsService
@EnableTranslationsStream
@EnableDefaultEmailTemplateRegistrationService
@EnableDefaultEmailTemplateStream
@EnableDefaultSMSTemplateRegistrationService
@EnableDefaultSMSTemplateStream
@EnableRoleRegisterStream
@EnableLetsEncrypt
@EnableTokenUtilServices
@EnableHazelcastClient
@ControllerAdvice
@EnableLocaleContextProcessor
@EnableDomainClient
public class LithiumServiceApplication implements IProbeController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired Environment env;
	
	@Autowired(required=false)
	private ModuleInfo[] modules;

	@Value("${spring.application.name}")
	public String applicationName;
	@Value("${server.port}")
	public String port;
	
	@Value("${lithium.services.db.load-test-data:false}")
	@Getter
	private boolean loadTestData;
	
	@Autowired CacheManager cacheManager;
	@Autowired ModuleInfoStartupNotifier startupNotifier;
	@Autowired TranslationsService translationsService;
	@Autowired EurekaClient eurekaClient;
	@Autowired TokenStore tokenStore;
	@Autowired RoleRegisterStream roleRegisterStream;
	@Autowired DefaultEmailTemplateRegisterService defaultEmailTemplateRegisterService;
	@Autowired DefaultSMSTemplateRegisterService defaultSMSTemplateRegisterService;

	public static UserGuidStrategy GUID_STRATEGY;
	
	@StreamListener(CacheClearMessageSink.INPUT)
	public void clearCacheMessage(String message) {
		log.info("Received a cache clear request: " + message);
	 
		Pattern p = Pattern.compile(message);
		for (String name: cacheManager.getCacheNames()) {
			Matcher m = p.matcher(name);
			if (m.matches()) {
				log.info("Clearing cache: " + name);
				Cache cache = cacheManager.getCache(name);
				cache.clear();
			}
		}
	}
	
	@RequestMapping("/version")
	public Response<String> version() {
		Package sourcePackage = this.getClass().getPackage();
		String version = (sourcePackage == null ? null : sourcePackage.getImplementationVersion());
		if ((version == null) || (version.isEmpty())) version = "SNAPSHOT";
		return Response.<String>builder().data(version).status(Status.OK).build();
	}
	

	@RequestMapping("/user")
	@Cacheable("user")
	public Principal user(Principal user) {
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)
				SecurityContextHolder.getContext().getAuthentication().getDetails();
		log.debug("Token: " + details.getTokenValue());
		log.debug("user Thread ID " + Thread.currentThread());
		return user;
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	public LithiumSecurityEvaluator lithiumSecurity() {
		LithiumSecurityEvaluator lithiumSecurity = new LithiumSecurityEvaluator(tokenStore);
		return lithiumSecurity;
	} 
		
	@RequestMapping("/modules")
	public ModuleInfo[] listModules() {
		return modules;
	}
	
	@Bean
	public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
		OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}

	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		if (config.getGatewayPublicUrl() == null)
			throw new Exception("Gateway public url not configured. "
					+ "Please add a property to the application.yml: lithium.gateway-public-url");
		log.info("Lithium config: " + config);

		/**
		 * This needs to be saved as a static variable in LithiumTokenUtil, so that LithiumTokenUtil is able to determine the guid
		 * using the stategy defined, should the guid be null.
		 * @see lithium.tokens.LithiumTokenUtil#setUserGuidStrategy(lithium.service.UserGuidStrategy)
		 * @see lithium.tokens.LithiumTokenUtil#guid()
 		 */
		GUID_STRATEGY = env.getProperty("lithium.services.user.guid-strategy", UserGuidStrategy.class, UserGuidStrategy.ID);
		log.info("GUID_STRATEGY :: "+ GUID_STRATEGY.name());
		LithiumTokenUtil.setUserGuidStrategy(GUID_STRATEGY);

		try {
			translationsService.registerChangesetsFromClasspath(false);
		} catch (Exception ex) {
			log.error("Unable to register translations: ", ex);
		}
		
		try {
			defaultEmailTemplateRegisterService.registerDefaultEmailTemplatesFromClasspath();
		} catch (Exception ex) {
			log.error("Unable to register default email templates", ex);
		}
		
		try {
			defaultSMSTemplateRegisterService.registerDefaultSMSTemplatesFromClasspath();
		} catch (Exception ex) {
			log.error("Unable to register default sms templates", ex);
		}
			
		if (modules == null) return;
			
		try {
			for (ModuleInfo moduleInfo: modules) {
				ModuleInfoAdapter c = new ModuleInfoAdapter();
				new ModelMapper().map(moduleInfo, c);
				startupNotifier.startupSend().send(MessageBuilder.<ModuleInfo>withPayload(c).build());
			}
			
			registerRoles();
		} catch (Exception ex) {
			log.error("Module processing failed: ", ex);
		}
		
		log.info("Cache manager implementation: " + cacheManager.toString());
	}
	
	@RequestMapping("/registerroles")
	public Response<String> registerRoles() throws Exception {
		
		if (modules != null) {
			for (ModuleInfo module: modules) {
				for (Role role: module.getRoles().getData()) {
					roleRegisterStream.registerRole(role);
				}
			}
		}
		
		return Response.<String>builder().status(Status.OK).build();
	}
	
	@RequestMapping("/registertranslations/rerun/all")
	public String rerunAllTranslations() throws Exception {
		log.warn("Forcing translation registration for all");
		translationsService.registerChangesetsFromClasspath(true);
		return "OK";
	}
	
	@RequestMapping("/registertranslations/rerun")
	public String rerunTranlation(@RequestParam("changeReference") String changeReference, @RequestParam("locale2") String locale2) throws Exception {
		log.warn("Forcing translation registration for changeReference: " + changeReference + ", locale2: " + locale2 );
		translationsService.rerunTranslation(changeReference, locale2);
		return "OK";
	}

//  This was not adding any value. Once it is in, there is no way for other API services to override it.
//  If you need custom exception handling for bad requests, simply add a bean of type ResponseEntityExceptionHandler
//  and override the responses for those scenarios that you need.
//  See lithium.service.cashier.processor.mvend.MvendExceptionHandler
//
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	public void handle(HttpMessageNotReadableException e) {
//		log.warn("Returning HTTP 400 Bad Request", e);
//		throw e;
//	}
	
	@RequestMapping("/roles")
	public Response<Iterable<Role>> roles() throws Exception {
		List<Role> roles = new ArrayList<>();
		if (modules != null) {
			for (ModuleInfo module: modules) {
				roles.addAll(module.getRoles().getData());
			}
		}
		return Response.<Iterable<Role>>builder().data(roles).status(Status.OK).build();
	}
	
	@RequestMapping("/providers")
	public Response<Iterable<ProviderConfig>> providers() throws Exception {
		Applications registeredApps = eurekaClient.getApplications();
		List<Application> appList = registeredApps.getRegisteredApplications();
		List<ProviderConfig> providers = new ArrayList<ProviderConfig>();
		for(Application app : appList) {
			if (app.getName().contains("PROVIDER")) {
				providers.add(ProviderConfig.builder().name(app.getName().toLowerCase()).build());
			}
		}
		return Response.<Iterable<ProviderConfig>>builder().data(providers).build();
	}

	/**
	 * This Method will be called when the Springboot App gets a SIGTERM Signal
	 *
	 * @param ready given false to set the readynessprobe to false
	 */

	@Override
	public void setReady(boolean ready) {
		eurekaClient.shutdown();
	}
}
