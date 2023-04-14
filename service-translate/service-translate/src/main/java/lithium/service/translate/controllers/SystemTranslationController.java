package lithium.service.translate.controllers;

import java.util.Locale;

import lithium.service.translate.data.repositories.TranslationKeyV2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.services.TranslationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/apiv1/system/translation")
@Slf4j
@Deprecated
public class SystemTranslationController {

	@Autowired LanguageRepository languageRepository;
	@Autowired TranslationService translationService;
	
	@GetMapping("/translate")
	@Deprecated
	public String translate(@RequestParam("code") String code, Locale locale) throws Exception {
		log.debug("System translation request: code: " + code + " locale: " + locale.toString() );
		//locale = Locale.forLanguageTag("no");
		Language language = languageRepository.findByLocale2(locale.getLanguage());
		if (language == null) language = languageRepository.findByLocale3(locale.getISO3Language());
		Language defaultLanguage = languageRepository.findByLocale3("eng");
		if (language == null) {
			log.debug("No language found for locale: " + locale + " using default locale." );
			language = defaultLanguage;
		}
		
		return translationService.getTranslationByCannonicalName(defaultLanguage, language, code);
	}
}
