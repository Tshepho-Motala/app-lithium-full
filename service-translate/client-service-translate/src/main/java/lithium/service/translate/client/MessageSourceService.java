package lithium.service.translate.client;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lithium.service.translate.client.objects.Domain;
import lithium.service.translate.client.objects.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageSourceService implements MessageSource {
	@Autowired
	private LithiumServiceClientFactory services;

	private String findTranslation(Domain domain, String code, String locale) {

		TranslationV2Client v2Client;
		try {
			v2Client = services.target(TranslationV2Client.class, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting translation service instance.");
			return code;
		}
		String translation = code;
		if (Module.hasModule(code)) {
			translation = v2Client.findByDomainAndCodeAndLocale(domain.getName(), code, locale);
			if (translation.equalsIgnoreCase(code) && !domain.getName().equalsIgnoreCase("default")) //Checking default domain, if original check was not for default
				translation = v2Client.findByDomainAndCodeAndLocale("default", code, locale);
			if (translation.equalsIgnoreCase(code)) { //If still not found then check in old translation structure
				TranslationClient client;
				try {
					client = services.target(TranslationClient.class, true);
				} catch (LithiumServiceClientFactoryException e) {
					log.error("Problem getting translation service instance.");
					return code;
				}

				translation = client.findByCodeAndLocale(code, locale);
			}
		}
		return translation; //if no translation are found, then the key/code will be returned
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		log.debug("Translation client: " + code + " locale: " + locale.toString() + " iso3: " + locale.getISO3Language());
		List<Object> arguments = new ArrayList<>();
		Domain domain = new Domain("default");
		if (args != null) {
			for (int i = 0; i < args.length; i++) { // Extracts the domain object from the object array
				if (args[i] instanceof Domain) domain = (Domain) args[i];
				else {
					arguments.add(args[i]);
				}
			}
			args = arguments.toArray();
		}
		if (code.trim().length() == 0) return "";
		String localeString = "eng";
		if (locale != null)
			localeString = locale.getISO3Language();
		String translation = findTranslation(domain, code, localeString);
		if (translation != null) {
			if (code.contentEquals(translation))
				return getFormattedString(defaultMessage, args, locale);
			return getFormattedString(translation, args, locale);
		}
		return defaultMessage;
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		List<Object> arguments = new ArrayList<>();
		Domain domain = new Domain("default");
		if (args != null) {
			for (int i = 0; i < args.length; i++) { // Extracts the domain object from the object array
				if (args[i] instanceof Domain) domain = (Domain) args[i];
				else {
					arguments.add(args[i]);
				}
			}
			args = arguments.toArray();
		}
		String translation = findTranslation(domain, code, locale.getISO3Language());
		if (translation != null)
			return getFormattedString(translation, args, locale);
		throw new NoSuchMessageException(code);
	}

	public MessageFormat getFormatter(String format, Locale locale) {
		return new MessageFormat(format, locale);
	}

	public String getFormattedString(String format, Object[] args, Locale locale) {
		if (ObjectUtils.isEmpty(args))
			return format;
		return getFormatter(format, locale).format(args);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		String lastCode = "unknown";
		for (String code : resolvable.getCodes()) {
			String translation = findTranslation(new Domain("default"), code, locale.getISO3Language());
			if (translation != null)
				return getFormattedString(translation, resolvable.getArguments(), locale);
			lastCode = code;
		}
		if (resolvable.getDefaultMessage() != null)
			return getFormattedString(resolvable.getDefaultMessage(), resolvable.getArguments(), locale);
		throw new NoSuchMessageException(lastCode);
	}
}