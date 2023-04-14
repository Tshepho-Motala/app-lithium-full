package lithium.services;

//import java.util.concurrent.TimeUnit;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.CacheControl;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//import lithium.modules.ModuleInfo;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@Configuration
//@EnableWebMvc
//public class ServerCacheConfig extends WebMvcConfigurerAdapter {
//	WebMvcAutoConfiguration p;
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		log.error("***************************###################### The resource handler has been registered ##############################*********************");
//		super.addResourceHandlers(registry);
//		registry.addResourceHandler("/**")
//				.addResourceLocations("/", "/static/", "classpath:/static/")
//				.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
//	}
//}