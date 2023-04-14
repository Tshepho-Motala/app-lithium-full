package lithium.service.translate.client;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class TranslationsConfiguration {
	
	@Bean
	public TranslationsService translationsService() {
		return new TranslationsService();
	}

	@Bean
	@Lazy
	public MessageSource messageSource() {
		return new MessageSourceService();
	}

	@Bean
	public LocaleResolver localeResolver() {
		final SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en", "US"));
		return localeResolver;
	}

}
