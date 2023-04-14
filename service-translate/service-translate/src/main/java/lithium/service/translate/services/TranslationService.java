package lithium.service.translate.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.translate.client.objects.Module;
import lithium.service.translate.client.objects.SubModule;
import lithium.service.translate.data.entities.Domain;
import lithium.service.translate.data.entities.TranslationKeyV2;
import lithium.service.translate.data.entities.TranslationValueV2;
import lithium.service.translate.data.objects.ErrorMessageResponse;
import lithium.service.translate.data.objects.TranslationKeyRequest;
import lithium.service.translate.data.objects.TranslationV2;
import lithium.service.translate.data.repositories.DomainRepository;
import lithium.service.translate.data.repositories.TranslationKeyV2Repository;
import lithium.service.translate.data.repositories.TranslationValueV2Repository;
import lithium.service.translate.data.specifications.TranslationV2Specification;
import lithium.service.translate.exceptions.Status400BadRequestException;
import lithium.service.translate.exceptions.Status409DuplicateMessageCodeException;
import lithium.service.translate.exceptions.Status422InvalidLanguageException;
import lithium.tokens.LithiumTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.Namespace;
import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.entities.TranslationValue;
import lithium.service.translate.data.entities.TranslationValueDefault;
import lithium.service.translate.data.entities.TranslationValueRevision;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.data.repositories.NamespaceRepository;
import lithium.service.translate.data.repositories.TranslationKeyRepository;
import lithium.service.translate.data.repositories.TranslationValueDefaultRepository;
import lithium.service.translate.data.repositories.TranslationValueRepository;
import lithium.service.translate.data.repositories.TranslationValueRevisionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TranslationService {

	@Autowired ModelMapper mapper;
	@Autowired ChangeLogService changeLogService;
	@Autowired DomainRepository domainRepository;
	@Autowired LanguageRepository languageRepository;
	@Autowired NamespaceRepository namespaceRepository;
	@Autowired TranslationKeyRepository translationKeyRepository;
	@Autowired TranslationKeyV2Repository translationKeyV2Repository;
	@Autowired TranslationValueRepository translationValueRepository;
	@Autowired TranslationValueV2Repository translationValueV2Repository;
	@Autowired TranslationValueDefaultRepository translationValueDefaultRepository;
	@Autowired TranslationValueRevisionRepository translationValueRevisionRepository;
	@Autowired DomainService domainService;

	@Deprecated
	public Namespace findOrCreateNamespace(String namespaces) {
		Namespace namespace = null;
		Namespace parent = null;
		for (String code: namespaces.toUpperCase().split("\\.")) {
			namespace = namespaceRepository.findByParentAndCode(parent, code);
			if (namespace == null) {
				namespace = Namespace.builder().code(code).parent(parent).build();
				namespaceRepository.save(namespace);
			}
			parent = namespace;
		}
		return namespace;
	}

	@Deprecated
	public TranslationKey findOrCreateKey(String namespaces, String key) {
		Namespace namespace = findOrCreateNamespace(namespaces);
		return findOrCreateKey(namespace, key);
	}

	@Deprecated
	public TranslationKey findOrCreateKey(Namespace namespace, String key) {
		TranslationKey translationKey = translationKeyRepository.findByNamespaceAndKeyCode(namespace, key);
		if (translationKey == null) {
			translationKey = TranslationKey.builder()
					.namespace(namespace)
					.keyCode(key)
					.build();
			translationKeyRepository.save(translationKey);
		}
		return translationKey;
	}

	@Deprecated
	public TranslationValue findOrCreateValue(TranslationKey key, Language language) {
		TranslationValue value = null;
		value = translationValueRepository.findByKeyAndLanguage(key, language);
		if (value == null) {
			value = TranslationValue.builder()
					.key(key)
					.language(language)
					.build();
			translationValueRepository.save(value);
		}
		return value;
	}

	@Deprecated
	public TranslationValueDefault saveDefault(String lang2, String namespaces, String key, String value) {
		TranslationKey translationKey = findOrCreateKey(namespaces, key);
		Language language = languageRepository.findByLocale2(lang2);
		TranslationValue translationValue = findOrCreateValue(translationKey, language);
		if (translationValue.getDefaultValue() != null)
			if (translationValue.getDefaultValue().getValue().equals(value))
				return translationValue.getDefaultValue();
		TranslationValueDefault newDefault = TranslationValueDefault.builder()
				.translationValueId(translationValue.getId())
				.value(value)
				.createdDate(new Date())
				.build();
		translationValueDefaultRepository.save(newDefault);
		translationValue.setDefaultValue(newDefault);
		translationValueRepository.save(translationValue);
		return newDefault;
	}

	@Deprecated
	public TranslationValueRevision save(String lang2, String namespaces, String key, String value, String author) {
		TranslationKey translationKey = findOrCreateKey(namespaces, key);
		return saveByKey(lang2, translationKey, value, author);
	}

	@Deprecated
	public TranslationValueRevision saveByKeyId(String lang2, Long keyId, String value, String author) {
		TranslationKey translationKey = translationKeyRepository.findOne(keyId);
		if (translationKey == null) throw new RuntimeException("Invalid key id");
		return saveByKey(lang2, translationKey, value, author);
	}

	@Deprecated
	public TranslationValueRevision saveByKey(String lang2, TranslationKey translationKey, String value, String author) {
		Language language = languageRepository.findByLocale2(lang2);
		TranslationValue translationValue = findOrCreateValue(translationKey, language);
		if (translationValue.getCurrent() != null)
			if (translationValue.getCurrent().getValue().equals(value))
				return translationValue.getCurrent();
		TranslationValueRevision newRevision = TranslationValueRevision.builder()
				.translationValueId(translationValue.getId())
				.value(value)
				.createdDate(new Date())
				.author(author)
				.build();
		translationValueRevisionRepository.save(newRevision);
		translationValue.setCurrent(newRevision);
		translationValueRepository.save(translationValue);
		return newRevision;
	}

	@Deprecated
	public String getTranslation(Language defaultLanguage, Language language, Namespace namespace, String key) {
		TranslationKey translationKey = translationKeyRepository.findByNamespaceAndKeyCode(namespace, key);
		if (translationKey == null) return key;
		if (language != null) {
			TranslationValue translationValue = translationValueRepository.findByKeyAndLanguage(translationKey, language);
			if (translationValue != null) {
				if (translationValue.getCurrent() != null) return translationValue.getCurrent().getValue();
				if (translationValue.getDefaultValue() != null) return translationValue.getDefaultValue().getValue();
			}
		}
		if (defaultLanguage != null) {
			if (language != null && language.getId() == defaultLanguage.getId()) return key;
			TranslationValue translationValue = translationValueRepository.findByKeyAndLanguage(translationKey, defaultLanguage);
			if (translationValue != null) {
				if (translationValue.getCurrent() != null) return translationValue.getCurrent().getValue();
				if (translationValue.getDefaultValue() != null) return translationValue.getDefaultValue().getValue();
			}
		}
		return key;
	}

	@Caching(cacheable = {
			@Cacheable(cacheNames = "lithium.service.translate.services.language.key", key = "#root.args[1].getLocale2() + #root.args[2]", unless = "#result == #root.args[2]"),
			@Cacheable(cacheNames = "lithium.service.translate.services.language.key", key = "#root.args[1].getLocale3() + #root.args[2]", unless = "#result == #root.args[2]")
			})
	@Deprecated
	public String getTranslationByCannonicalName(Language defaultLanguage, Language language, String cannonicalTranslationKey) {
		String[] splitKey = cannonicalTranslationKey.split("\\.");
		log.debug("No cache found for: " + cannonicalTranslationKey + " with language: " + language);
		//Lookup all the namespaces to make sure they key exists
		Namespace[] nsLookup = new Namespace[splitKey.length-1];
		Namespace tmp = null;
		for (int p=0; p <= splitKey.length-2; ++p) {
			tmp = namespaceRepository.findByParentAndCode(tmp, splitKey[p]);
			if (tmp == null) {
				log.debug("Problem looking up translation namespace: " + splitKey[p] + " in key: " + cannonicalTranslationKey);
				return cannonicalTranslationKey;
			}
			nsLookup[p] = tmp;
		}

		TranslationKey translationKey = null;
		//Return non-namespace values as their original values
		if (nsLookup.length > 0) {
			translationKey = translationKeyRepository.findByNamespaceAndKeyCode(nsLookup[nsLookup.length-1], splitKey[splitKey.length-1]);
		} else {
			log.debug("Receved a value to translate but it does not have a namespace:" + cannonicalTranslationKey);
			return cannonicalTranslationKey;
		}

		if (translationKey == null) {
			log.debug("Problem looking up translation key: ns:" + nsLookup[nsLookup.length-1] +" keyCode:"+ splitKey[splitKey.length-1] + " in key: " + cannonicalTranslationKey);
			return cannonicalTranslationKey;
		}

		if (language != null) {
			TranslationValue translationValue = translationValueRepository.findByKeyAndLanguage(translationKey, language);
			if (translationValue != null) {
				if (translationValue.getCurrent() != null) return translationValue.getCurrent().getValue();
				if (translationValue.getDefaultValue() != null) return translationValue.getDefaultValue().getValue();
			} else {
				log.debug("Problem getting value for translation key: " + translationKey + " with language: " + language);
			}
		}
		if (defaultLanguage != null) {
			if (language != null && language.getId() == defaultLanguage.getId()) return cannonicalTranslationKey;
			TranslationValue translationValue = translationValueRepository.findByKeyAndLanguage(translationKey, defaultLanguage);
			if (translationValue != null) {
				if (translationValue.getCurrent() != null) return translationValue.getCurrent().getValue();
				if (translationValue.getDefaultValue() != null) return translationValue.getDefaultValue().getValue();
			} else {
				log.debug("Problem getting value for translation key: " + translationKey + " with language: " + defaultLanguage);
			}
		}
		return cannonicalTranslationKey;
	}

	public String findByDomainAndCodeAndLocale(String domainName, String code, Language defaultLanguage, Language language) {
		Optional<TranslationValueV2> byLanguage = translationValueV2Repository.findTopByDomainNameAndKeyCodeAndAndLanguage(domainName, code, language);
		if (!byLanguage.isPresent()) {
			Optional<TranslationValueV2> byDefaultLanguage = translationValueV2Repository.findTopByDomainNameAndKeyCodeAndAndLanguage(domainName, code, defaultLanguage);
			if (byDefaultLanguage.isPresent())
				return byDefaultLanguage.get().getValue();
			else {
				return code;
			}
		} else {
			return byLanguage.get().getValue();
		}
	}

	public Domain findOrCreateDomain(String domainName) {
		Optional<Domain> domain = domainRepository.findByName(domainName);
		if (domain.isPresent())
			return domain.get();
		else
			return domainRepository.save(Domain.builder().name(domainName).build());
	}

	public Domain findDomain(String domainName) {
		Optional<Domain> domain = domainRepository.findByName(domainName);
		if (domain.isPresent())
			return domain.get();
		else
			return null;
	}

	public TranslationKeyV2 findOrCreateKeyV2(String code) {
		Optional<TranslationKeyV2> byCode = translationKeyV2Repository.findByCode(code);
		if (byCode.isPresent())
			return byCode.get();
		else {
			TranslationKeyV2 keyV2 = TranslationKeyV2.builder().code(code).build();
			return translationKeyV2Repository.save(keyV2);
		}
	}

	public TranslationValueV2 findOrCreateValueV2(TranslationKeyV2 keyV2, Domain domain, Language language, String value) {
		Optional<TranslationValueV2> valueV2 = translationValueV2Repository.findByDomainAndKeyAndLanguage(domain, keyV2, language);
		if (valueV2.isPresent())
			return valueV2.get();
		else {
			return translationValueV2Repository.save(TranslationValueV2.builder()
																		.key(keyV2)
																		.value(value)
																		.language(language)
																		.domain(domain)
																		.build());
		}
	}

	public void saveTranslationV2(String domainName, String lang, String code, String value) {
		Domain domain = findOrCreateDomain(domainName);
		Language language = findLanguageByLocale(lang);
		TranslationKeyV2 keyV2 = findOrCreateKeyV2(code);
		createOrUpdateValueV2(keyV2, domain, language, value);
	}

	public Language findLanguageByLocale(String lang) {
		Language language = languageRepository.findByLocale2(lang);
		if (language != null) {
			return language;
		}
		language = languageRepository.findByLocale3(lang);
		if (language != null) {
			return language;
		}
		// Fail-safe
		return languageRepository.findByLocale2("en");
	}

	public TranslationValueV2 createOrUpdateValueV2(TranslationKeyV2 keyV2, Domain domain, Language language, String value) {
		Optional<TranslationValueV2> findValuev2 = translationValueV2Repository.findByDomainAndKeyAndLanguage(domain, keyV2, language);
		if (findValuev2.isPresent()) {
			TranslationValueV2 valueV2 = findValuev2.get();
			valueV2.setValue(value);
			return translationValueV2Repository.save(valueV2);
		}
		else {
			return translationValueV2Repository
					.save(TranslationValueV2.builder()
					.key(keyV2)
					.value(value)
					.language(language)
					.domain(domain)
					.build());
		}
	}

	public Optional<TranslationKeyV2> findKeyByCode(String code) {
		return translationKeyV2Repository.findByCode(code);
	}

	public Page<TranslationKeyV2> findCodeStartingWith(String domainName, String namespace, Pageable pageable) {
		Specification<TranslationKeyV2> spec = null;
		spec = addToSpec(namespace,  spec, TranslationV2Specification::startingWith);
		spec = spec.and(TranslationV2Specification.keysForDomainOrDefault(domainName));

		return translationKeyV2Repository.findAll(spec, pageable);
	}

	private Specification<TranslationKeyV2> addToSpec(final String aString, Specification<TranslationKeyV2> spec,
												Function<String, Specification<TranslationKeyV2>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<TranslationKeyV2> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	public List<TranslationV2> getValuesV2ByCodeStartingWith(String[] namespaceArr, String domainName, Language language) {
		return getValuesV2ByCodeStartingWithAndLastUpdatedSince(namespaceArr, new String[] {domainName}, new String[] {language.getLocale2()}, null);
	}

	public List<TranslationV2> getValuesV2ByCodeStartingWithAndLastUpdatedSince(String[] namespaceArr, String[] domainNames, String[] locale2s, Date lastUpdatedSince) {
		List<TranslationV2> translationV2s = new ArrayList<>();
		for (String namespace: namespaceArr) {
			List<Object> translations;
			if (lastUpdatedSince == null) {
				translations = translationValueV2Repository.findTranslations(domainNames[0], locale2s[0], namespace + "%");
			} else {
				translations = translationValueV2Repository.findTranslationsLastUpdatedSince(domainNames, locale2s, namespace + "%", lastUpdatedSince);
			}

			for (int i = 0; i < translations.size(); i++) {
				Object[] tranlsation = (Object[]) translations.get(i);

				TranslationV2 translationV2 = TranslationV2.builder()
						.domainName((String) tranlsation[0])
						.language((String) tranlsation[1])
						.key((String) tranlsation[2])
						.value((String) tranlsation[3])
						.build();
				translationV2s.add(translationV2);
			}
		}
		return translationV2s;
	}

	public Page<ErrorMessageResponse> getErrorTranslations(DataTableRequest request, String domainName, Module errorDictionary, SubModule subModule, Boolean domainSpecific) {
		String searchKey = errorDictionary.name() + "." + subModule.name();
		Page<TranslationKeyV2> keyV2s = findCodeStartingWith(domainName, searchKey, request.getPageRequest());

		List<ErrorMessageResponse> errorMessageResponseList = new ArrayList<>();
		for (TranslationKeyV2 keyV2 : keyV2s) {
			String defaultValue = null;
			String domainNameValue = null;
			List<String> languages = new ArrayList<>();
			languages.add("en");
			if (keyV2.getValues().stream().anyMatch(value -> value.getDomain().getName().equalsIgnoreCase(domainName)) || !domainSpecific) {
				for (TranslationValueV2 value : keyV2.getValues().stream().filter(value -> value.getDomain().getName().equalsIgnoreCase("default") || value.getDomain().getName().equalsIgnoreCase(domainName)).collect(Collectors.toList())) {

					if ((value.getDomain().getName().equalsIgnoreCase("default") || (value.getKey().getUserDefined() != null && value.getKey().getUserDefined())) && value.getLanguage().getLocale2().equals("en"))
						defaultValue = value.getValue();
					if (value.getDomain().getName().equalsIgnoreCase(domainName) && value.getLanguage().getLocale2().equals("en"))
						domainNameValue = value.getValue();
					if (value.getDomain().getName().equalsIgnoreCase(domainName)) {
						if (!languages.stream().filter(s -> s.equalsIgnoreCase(value.getLanguage().getLocale2())).findAny().isPresent())
							languages.add(value.getLanguage().getLocale2());
					}
				}
				String value = domainNameValue == null ? defaultValue : domainNameValue;

				errorMessageResponseList.add(ErrorMessageResponse.builder()
						.id(keyV2.getId())
						.code(keyV2.getCode())
						.description(keyV2.getDescription() == null ? "" : keyV2.getDescription())
						.value(value)
						.languages(languages)
						.userDefined(keyV2.getUserDefined() == null || !keyV2.getUserDefined() ? "false" : "true")
						.build());
			}
		}
		return new SimplePageImpl<>(errorMessageResponseList, request.getPageRequest().getPageNumber(), request.getPageRequest().getPageSize(), keyV2s.getTotalElements());
	}

	public List<TranslationV2> getCodeValues(Long id, String domainName) {
		final TranslationKeyV2 keyV2 = translationKeyV2Repository.findOne(id);
		List<TranslationV2> translationV2s = new ArrayList<>();
		for (TranslationValueV2 value : keyV2.getValues()) {
			if (value.getDomain().getName().equalsIgnoreCase("default") || value.getDomain().getName().equalsIgnoreCase(domainName))
			translationV2s.add(TranslationV2.builder()
											.domainName(value.getDomain().getName())
											.language(value.getLanguage().getLocale2())
											.keyId(id)
											.key(keyV2.getCode())
											.valueId(value.getId())
											.value(value.getValue()).build());
		}
		return translationV2s;
	}

	public TranslationV2 addTranslation(String domainName, String locale2, Long keyId, String value, LithiumTokenUtil tokenUtil) {
		Domain domain = findOrCreateDomain(domainName);
		Language language = languageRepository.findByLocale2(locale2);
		TranslationKeyV2 keyV2 = translationKeyV2Repository.findOne(keyId);
		TranslationValueV2 save = translationValueV2Repository.save(TranslationValueV2.builder()
				.domain(domain)
				.key(keyV2)
				.language(language)
				.value(value)
				.build());

		try {
			String entity = "domain.errormessage" + "." + SubModule.fromName(keyV2.getCode().split("\\.")[1]).name().toLowerCase().replace("_","");
			List<ChangeLogFieldChange> clfc = changeLogService.copy(save, new TranslationValueV2(), new String[]{"value", "description"});
			List<ChangeLogFieldChange> clfcLang = changeLogService.copy(save.getLanguage(), new Language(), new String[]{"locale2"});
			List<ChangeLogFieldChange> clfcDomain = changeLogService.copy(save.getDomain(), new Domain(), new String[]{"name"});
			clfcLang.forEach(changeLogFieldChange -> clfc.add(changeLogFieldChange));
			clfcDomain.forEach(changeLogFieldChange -> clfc.add(changeLogFieldChange));
			changeLogService.registerChangesForNotesWithFullNameAndDomain(entity, "create", domain.getId(), tokenUtil.guid(), tokenUtil, "", "", clfc, Category.SUPPORT, SubCategory.TRANSLATIONS, 0, domainName);
		} catch (Exception ex) {
			log.warn("Problem adding changelog on addTranslation: TranslationValueV2 -> {}, exception -> {}", save, ex);
		}

		return TranslationV2.builder()
				.domainName(save.getDomain().getName())
				.language(save.getLanguage().getLocale2())
				.keyId(save.getKey().getId())
				.key(keyV2.getCode())
				.valueId(save.getId())
				.value(save.getValue()).build();
	}

	public void editTranslation(Long valueId, String value, LithiumTokenUtil tokenUtil) {
		Optional<TranslationValueV2> byId = translationValueV2Repository.findById(valueId);
		if (byId.isPresent()) {
			TranslationValueV2 valueV2 = byId.get();
			valueV2.setValue(value);
			TranslationValueV2 save = translationValueV2Repository.save(valueV2);

			try {
				String entity = "domain.errormessage" + "." + SubModule.fromName(valueV2.getKey().getCode().split("\\.")[1]).name().toLowerCase().replace("_","");
				List<ChangeLogFieldChange> clfc = changeLogService.copy(save, new TranslationValueV2(), new String[]{"value", "description"});
				List<ChangeLogFieldChange> clfcLang = changeLogService.copy(save.getLanguage(), new Language(), new String[]{"locale2"});
				List<ChangeLogFieldChange> clfcDomain = changeLogService.copy(save.getDomain(), new Domain(), new String[]{"name"});
				clfcLang.forEach(changeLogFieldChange -> clfc.add(changeLogFieldChange));
				clfcDomain.forEach(changeLogFieldChange -> clfc.add(changeLogFieldChange));
				changeLogService.registerChangesForNotesWithFullNameAndDomain(entity, "edit", valueV2.getDomain().getId(), tokenUtil.guid(), tokenUtil, "", "", clfc, Category.SUPPORT, SubCategory.TRANSLATIONS, 0, valueV2.getDomain().getName());
			} catch (Exception ex) {
				log.warn("Problem editing changelog on editTranslation: TranslationValueV2 -> {}, exception -> {}", save, ex);
			}
		}
	}

	public void deleteTranslation(Long valueId, LithiumTokenUtil tokenUtil) {
		Optional<TranslationValueV2> byId = translationValueV2Repository.findById(valueId);
		if (byId.isPresent()) {
			TranslationValueV2 valueV2 = byId.get();
			String domainName = valueV2.getDomain().getName();
			//If only one more translation value is available in the system then delete the key
			if (valueV2.getKey().getValues().size() == 1) {
				deleteUserDefinedByKeyId(valueV2.getKey().getId(), domainName, tokenUtil);
				return;
			}

			translationValueV2Repository.delete(valueV2);

			try {
				String entity = "domain.errormessage" + "." + SubModule.fromName(valueV2.getKey().getCode().split("\\.")[1]).name().toLowerCase().replace("_", "");
				List<ChangeLogFieldChange> clfc = changeLogService.copy(new TranslationValueV2(), valueV2, new String[]{"value", "description"});
				List<ChangeLogFieldChange> clfcLang = changeLogService.copy(new Language(), valueV2.getLanguage(), new String[]{"locale2"});
				List<ChangeLogFieldChange> clfcDomain = changeLogService.copy(new Domain(), valueV2.getDomain(), new String[]{"name"});
				clfc.addAll(clfcLang);
				clfc.addAll(clfcDomain);
				changeLogService.registerChangesForNotesWithFullNameAndDomain(entity, "delete", valueV2.getDomain().getId(), tokenUtil.guid(), tokenUtil, "", "", clfc, Category.SUPPORT, SubCategory.TRANSLATIONS, 0, domainName);
            } catch (Exception ex) {
				log.warn("Problem deleting changelog on deleteTranslation: TranslationValueV2 -> {}, exception -> {}", valueV2, ex);
			}
		}
	}

	public boolean deleteUserDefinedByKeyId(Long valueId, String domainName, LithiumTokenUtil tokenUtil) {
		Optional<TranslationKeyV2> translationKeyV2 = translationKeyV2Repository.findById(valueId);
		if (translationKeyV2.isPresent()) {
			if (translationKeyV2.get().getUserDefined()) {
				TranslationKeyV2 keyV2 = translationKeyV2.get();
				Optional<TranslationValueV2> valueV2 = translationValueV2Repository.findByKey_id(keyV2.getId());
				valueV2.ifPresent(translationValueV2 -> translationValueV2Repository.delete(translationValueV2));
				translationKeyV2Repository.delete(keyV2);
				try {
					String entity = "domain.errormessage" + "." + SubModule.fromName(keyV2.getCode().split("\\.")[1]).name().toLowerCase().replace("_", "");
					List<ChangeLogFieldChange> clfc = changeLogService.copy(new TranslationKeyV2(), keyV2, new String[]{"code", "description"});
					changeLogService.registerChangesWithDomain(entity, "delete", valueId, tokenUtil.guid(), "", "", clfc, Category.SUPPORT, SubCategory.TRANSLATIONS, 0, domainName);
					return true;
				} catch (Exception ex) {
					log.warn("Problem deleting changelog on deleteTranslation: TranslationKeyV2 -> {}, exception -> {}", keyV2, ex);
				}
			}
		}
		return false;
	}

    public boolean deleteByCode(String code) {
		Optional<TranslationKeyV2> byCode = translationKeyV2Repository.findByCode(code);
		if (byCode.isPresent()) {
			TranslationKeyV2 keyV2 = byCode.get();
            translationValueV2Repository.deleteAll(keyV2.getValues());
			translationKeyV2Repository.delete(keyV2);
			return true;
		}
		return false;
	}

	public TranslationKeyV2 createKeyV2andDefaultTranslation(TranslationKeyRequest translationKeyRequest, LithiumTokenUtil tokenUtil)
			throws Status409DuplicateMessageCodeException, Status422InvalidLanguageException, Status550ServiceDomainClientException, Status400BadRequestException {

		validateTranslationKeyCreationRequest(translationKeyRequest);

		String errorCode = "ERROR_DICTIONARY." + translationKeyRequest.getMessageType().toUpperCase().replace(" ", "_") + "."
				+ translationKeyRequest.getMessageKey().toUpperCase().replace( " ",".");

		Optional<TranslationKeyV2> byCode = translationKeyV2Repository.findByCode(errorCode);
		if (byCode.isPresent()) throw new Status409DuplicateMessageCodeException("A key with similar details already exists.");

		Domain domain = domainService.findDomainByName(translationKeyRequest.getDomainName());

		Language language = languageRepository.findByLocale2(translationKeyRequest.getMessageLanguage());
		if (language == null) throw new Status422InvalidLanguageException("Invalid language selected.");

		TranslationKeyV2 translationKeyV2saved = translationKeyV2Repository.save(TranslationKeyV2.builder().
				code(errorCode).userDefined(true).build());

		TranslationValueV2 translationValueV2 = translationValueV2Repository.save(TranslationValueV2.builder()
				.key(translationKeyV2saved)
				.value(translationKeyRequest.getDescription())
				.language(language)
				.domain(domain)
				.build());

		return translationKeyV2saved;
	}

	private void validateTranslationKeyCreationRequest(TranslationKeyRequest translationKeyRequest)
			throws Status400BadRequestException {
		if (translationKeyRequest == null || translationKeyRequest.getMessageKey() == null ||
				translationKeyRequest.getDescription() == null || translationKeyRequest.getMessageType() == null ||
				translationKeyRequest.getDomainName() == null || translationKeyRequest.getMessageLanguage() == null)
			throw new Status400BadRequestException("Invalid error message creation request");
		if (translationKeyRequest.getMessageKey().trim().isEmpty() ||
				translationKeyRequest.getDescription().trim().isEmpty() ||
				translationKeyRequest.getMessageType().trim().isEmpty() ||
				translationKeyRequest.getDomainName().trim().isEmpty() ||
				translationKeyRequest.getMessageLanguage().trim().isEmpty())
			throw new Status400BadRequestException("Invalid error message creation request");
	}
}
