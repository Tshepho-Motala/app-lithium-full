package lithium.service.translate.controllers;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.LatestChangeSet;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.data.repositories.LatestChangeSetRepository;
import lithium.service.translate.services.TranslationService;

@RestController
@RequestMapping("/apiv1/changesets")
@Slf4j
@Deprecated
public class LatestChangeSetController {

	@Autowired LatestChangeSetRepository latestChangeSetRepository;
	@Autowired TranslationService translationService;
	@Autowired LanguageRepository languageRepository;
	@Autowired AngularController angularController;
	
	@RequestMapping("get")
	public LatestChangeSet get(@RequestParam String name, @RequestParam String lang) {
		return latestChangeSetRepository.findByNameAndLanguage_Locale2(name, lang);
	}
	
	@RequestMapping("register")
	public void register(@RequestParam String name, @RequestParam String lang, @RequestParam int changeNumber) throws Exception {
		LatestChangeSet changeSet = latestChangeSetRepository.findByNameAndLanguage_Locale2(name, lang);
		if (changeSet == null) {
			Language language = languageRepository.findByLocale2(lang);
			changeSet = LatestChangeSet.builder()
					.language(language)
					.name(name)
					.changeNumber(changeNumber)
					.applyDate(new Date())
					.build();
		} else {
			changeSet.setChangeNumber(changeNumber);
			changeSet.setApplyDate(new Date());
		}
		latestChangeSetRepository.save(changeSet);
	}

	@RequestMapping("/build-translation-cache")
	void buildTranslationCache(@RequestParam("lang") String lang, @RequestParam("namespace") String namespace) {
		try {
			angularController.angular(namespace, lang);
		} catch (Exception e) {
			log.warn("Unable to do translation cache hack rebuild on: " + namespace +  " for lang: " + lang +" msg: "  + e.getMessage(), e);
		}
	}
	
}
