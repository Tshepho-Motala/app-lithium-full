package lithium.service.translate.services;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import lithium.metrics.TimeThisMethod;
import lithium.service.translate.data.entities.TranslationValueV2;
import lithium.service.translate.data.objects.TranslationV2;
import lithium.service.translate.data.repositories.DomainRepository;
import lithium.service.translate.data.repositories.TranslationKeyV2Repository;
import lithium.service.translate.data.repositories.TranslationValueV2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.Namespace;
import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.data.repositories.NamespaceRepository;
import lithium.service.translate.data.repositories.TranslationKeyRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AngularService {
	@Autowired NamespaceRepository namespaceRepository;
	@Autowired LanguageRepository languageRepository;
	@Autowired TranslationKeyRepository translationKeyRepository;
	@Autowired TranslationService translationService;
	@Autowired DomainRepository domainRepository;
	@Autowired TranslationKeyV2Repository translationKeyV2Repository;
	@Autowired TranslationValueV2Repository translationValueV2Repository;

	@Deprecated
	private ObjectNode addNamespace(Language defaultLanguage, Language language, ObjectNode parentNode, Namespace namespace, JsonNodeFactory factory) {
		
		ObjectNode objectNode = factory.objectNode();
		
		for (TranslationKey key: translationKeyRepository.findByNamespace(namespace)) {
			String value = translationService.getTranslation(defaultLanguage, language, namespace, key.getKeyCode());
			objectNode.set(key.getKeyCode(), factory.textNode(value));
		}
		
		for (Namespace childNamespace: namespaceRepository.findByParent(namespace)) {
			addNamespace(defaultLanguage, language, objectNode, childNamespace, factory);
		}
		
		parentNode.set(namespace.getCode(), objectNode);
		
		return objectNode;
	}
	
	@Cacheable(cacheNames="lithium.service.translate.services.translations.json", unless="#result == null")
	@Deprecated
	public String getTranslations(Language language, Language defaultLanguage, String namespace) throws Exception {
		StringWriter stringWriter = new StringWriter();
		
		JsonNodeFactory factory = new JsonNodeFactory(false);
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator generator = jsonFactory.createGenerator(stringWriter);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = factory.objectNode();
		
		Namespace n = namespaceRepository.findByParentAndCode(null, "GLOBAL");
		if (n != null) addNamespace(defaultLanguage, language, root, n, factory);
		if (!namespace.toUpperCase().equals("GLOBAL")) {
			n = namespaceRepository.findByParentAndCode(null, namespace.toUpperCase());
			if (n != null) addNamespace(defaultLanguage, language, root, n, factory);
		}

		mapper.writeTree(generator, root);
		return stringWriter.toString();
	}

	@TimeThisMethod
	@Cacheable(cacheNames="lithium.service.translate.services.translationsV2.json", unless="#result == null")
	public String getTranslationsV2(Language language, Language defaultLanguage, String namespace) throws Exception {

		StringWriter stringWriter = new StringWriter();

		JsonNodeFactory factory = new JsonNodeFactory(false);
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator generator = jsonFactory.createGenerator(stringWriter);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = factory.objectNode();

		final List<TranslationV2> translationV2s = translationService.getValuesV2ByCodeStartingWith(new String[] {"GLOBAL", namespace.toUpperCase()}, "default", defaultLanguage);
		if (translationV2s.size() > 0) {
			for (TranslationV2 valueV2: translationV2s) {
				ObjectNode newObjectNode = addNamespace(defaultLanguage, language, root, valueV2, factory);
				merge(root, newObjectNode);
			}
		}
		mapper.writeTree(generator, root);
		return stringWriter.toString();
	}

	@Caching(evict = {
			@CacheEvict(value = "lithium.service.translate.services.translate2", allEntries = true), //Gets erased here, but the object type stored is the client version of the key value string
			@CacheEvict(value = "lithium.service.translate.services.translationsV2.json", allEntries = true)
	})
	public void evictAllCacheValues() {}

	public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

		Iterator<String> fieldNames = updateNode.fieldNames();
		while (fieldNames.hasNext()) {

			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			// if field exists and is an embedded object
			if (jsonNode != null && jsonNode.isObject()) {
				merge(jsonNode, updateNode.get(fieldName));
			} else {
				if (mainNode instanceof ObjectNode) {
					// Overwrite field
					JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).replace(fieldName, value);
				}
			}
		}
		return mainNode;
	}

	private ObjectNode addNamespace(Language defaultLanguage, Language language, ObjectNode parentNode, TranslationV2 valueV2, JsonNodeFactory factory) {

		String[] namespaces = valueV2.getKey().split("\\.");

		final String value = valueV2.getValue();
		ObjectNode childNode = null;
		for (int i = namespaces.length - 1; i >= 0 ; i--) {
			if (i == namespaces.length - 1) {
				childNode = (ObjectNode) factory.objectNode().set(namespaces[i], factory.textNode(value));
			} else {
				ObjectNode objectNode = (ObjectNode) factory.objectNode().set(namespaces[i], childNode);
				childNode = objectNode;
			}
		}

		return childNode;
	}
}
