package lithium.service.translate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.services.AngularService;

@RestController
@RequestMapping("/apiv1/translations/angular")
@Deprecated
public class AngularController {

	@Autowired LanguageRepository languageRepository;
	@Autowired AngularService angularService;
	
	@RequestMapping("/{namespace}/get")
	public String angular(@PathVariable("namespace") String namespace, 
			@RequestParam("lang") String lang2) throws Exception {
		
		Language language = languageRepository.findByLocale2(lang2);
		Language defaultLanguage = language;
		if (!lang2.equals("en")) defaultLanguage = languageRepository.findByLocale2("en");
		
		return angularService.getTranslations(language, defaultLanguage, namespace);
	}
}
