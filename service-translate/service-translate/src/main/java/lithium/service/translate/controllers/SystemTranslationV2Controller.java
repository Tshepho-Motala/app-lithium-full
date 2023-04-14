package lithium.service.translate.controllers;

import lithium.service.Response;
import lithium.service.translate.client.objects.Translation;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.services.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("/apiv2/system/translation")
@Slf4j
public class SystemTranslationV2Controller {

	@Autowired LanguageRepository languageRepository;
	@Autowired TranslationService translationService;

	@GetMapping("/translate")
	public String translateV2(@RequestParam("domainName") String domainName, @RequestParam("code") String code, Locale locale) {
		log.debug("System translation v2 request: domainName: " + domainName + " code: " + code + " locale: " + locale.toString() );

		Language language = languageRepository.findByLocale2(locale.getLanguage());
		if (language == null) language = languageRepository.findByLocale3(locale.getISO3Language());
		Language defaultLanguage = languageRepository.findByLocale3("eng");
		if (language == null) {
			log.debug("No language found for locale: " + locale + " using default locale." );
			language = defaultLanguage;
		}

		return translationService.findByDomainAndCodeAndLocale(domainName, code, defaultLanguage, language);
	}
}