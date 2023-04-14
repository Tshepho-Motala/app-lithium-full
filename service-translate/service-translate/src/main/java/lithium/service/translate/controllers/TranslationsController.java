package lithium.service.translate.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.objects.Translation;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.data.repositories.NamespaceRepository;
import lithium.service.translate.data.repositories.TranslationKeyRepository;
import lithium.service.translate.data.repositories.TranslationValueRepository;
import lithium.service.translate.data.specifications.TranslationKeySpecification;
import lithium.service.translate.services.TranslationService;

@RestController
@RequestMapping("/apiv1/translations")
@Deprecated
public class TranslationsController {

	@Autowired NamespaceRepository namespaceRepository;
	@Autowired LanguageRepository languageRepository;
	@Autowired TranslationKeyRepository translationKeyRepository;
	@Autowired TranslationService translationService;
	@Autowired TranslationValueRepository translationValueRepository;
	
	@RequestMapping("list")
	@Deprecated
	DataTableResponse<Translation> list(@RequestParam String languageFrom, @RequestParam String languageTo, @RequestParam boolean completed, DataTableRequest request) {
		
		Specification<TranslationKey> spec = null;
		
		if (completed) {
			spec = Specification.where(TranslationKeySpecification.byLanguage(languageFrom, request.getSearchValue()));
		} else {
			spec = Specification.where(TranslationKeySpecification.byLanguageAndRefLanguageMissing(languageTo, languageFrom, request.getSearchValue()));
		}
		
		Page<TranslationKey> list = translationKeyRepository.findAll(spec, request.getPageRequest());
		List<Translation> translations = new ArrayList<>();
		list.forEach((TranslationKey tk) -> {
			translations.add(Translation.builder()
					.key(tk)
					.referenceValue(translationValueRepository.findByKeyAndLanguage_Locale2(tk, languageFrom))
					.value(translationValueRepository.findByKeyAndLanguage_Locale2(tk, languageTo))
					.build()
			);
		});
		
		return new DataTableResponse<>(request, translations, list.getTotalElements(), list.getTotalElements());
	}
}
