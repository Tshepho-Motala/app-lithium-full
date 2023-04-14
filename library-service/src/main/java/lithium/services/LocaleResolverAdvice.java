package lithium.services;

import java.util.IllformedLocaleException;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@Slf4j
public class LocaleResolverAdvice implements WebMvcConfigurer {

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor() {
			// Allow for old locale formats
			@Override
			protected Locale parseLocaleValue(String locale) {
				try {
					if (locale.contains("_") || locale.contains("-")) {
						locale = locale.replace("_", "-");
						return Locale.forLanguageTag(locale);
					}
				} catch (IllformedLocaleException lle) {
					log.warn("Illformed locale found, going to try a manual String parse:" + locale);
				}
				return super.parseLocaleValue((locale != null && locale.length() >= 2) ? locale.substring(0, 2) : locale);
			}
		};
		//lci.setParamName("lang");
		//Default is locale, we can decide what to use later on
		
		return lci;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}
