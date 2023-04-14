package lithium.service.translate.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.translate.client.objects.ChangeSet;
import lithium.service.translate.client.objects.Domain;
import lithium.service.translate.client.objects.Language;
import lithium.service.translate.client.stream.TranslationsStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TranslationsService {

	@Autowired
	private LithiumServiceClientFactory services;

	@Autowired
	TranslationsStream translationsStream;

	@Value("${spring.application.name}")
	private String applicationName;
	
	public void registerChangesetsFromClasspath(boolean force) throws Exception {
		if (force) {
			getV2Client().removeAllChangeSets(this.applicationName);
		}
		registerChangesets(force);
	}

	public void rerunTranslation(String changeReference, String locale2) throws Exception {
		getV2Client().removeChangeSet(locale2, this.applicationName, changeReference);
		registerChangesets(false);
	}

	public Locale resolveLocale(String localeStr) throws LithiumServiceClientFactoryException {
		Language language = getV2Client().findLanguageByLocale(localeStr);
		Locale locale = Locale.forLanguageTag(language.getLocale2());
		return locale;
	}
	
	private void registerChangesets(boolean force) throws Exception {
		HashMap<String, HashMap<String, SortedMap<String, ChangeSet>>> changeSets =
				new HashMap<String, HashMap<String, SortedMap<String, ChangeSet>>>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		
		try {
			for (Resource resource: resolver.getResources("classpath:i18n/*.*.*.json")) {
				Pattern p = Pattern.compile("([a-z\\-]+)\\.([a-z]+)\\.([a-z\\-0-9]+)\\.json");
				Matcher m = p.matcher(resource.getFilename());
				if (m.matches()) {
					String name = m.group(1);
					String lang = m.group(2);
					String changeReference = m.group(3);
					log.info("Found translation changeset " + name + " " + lang + " " + changeReference);

					HashMap<String, SortedMap<String, ChangeSet>> changeSetsByName = changeSets.get(lang);
					if (changeSetsByName == null) {
						changeSetsByName = new HashMap<String, SortedMap<String, ChangeSet>>();
						changeSets.put(lang, changeSetsByName);
					}

					SortedMap<String, ChangeSet> changeSetsByChangeReference = changeSetsByName.get(name);
					if (changeSetsByChangeReference == null) {
						changeSetsByChangeReference = new TreeMap<String, ChangeSet>();
						changeSetsByName.put(name, changeSetsByChangeReference);
					}

					ChangeSet changeSet = ChangeSet.builder().resource(resource).name(name).lang(lang).changeReference(changeReference).build();
					changeSetsByChangeReference.put(changeReference,  changeSet);
				} else {
					// No need to kill the entire process now. We will get back to the file once the naming has been fixed,
					// since we are now checking checksums on all files.
					log.error("The translation file " + resource.getFilename() + " does not comply to the naming"
							+ " convention pattern. It has been skipped.");
				}
			}
		} catch (FileNotFoundException fne) {
		}
		
		if (changeSets.size() == 0) return;
		
		for (String lang: changeSets.keySet()) {
			for (String name: changeSets.get(lang).keySet()) {

				final List<ChangeSet> latestChangeSets = force ? new ArrayList<>() : getV2Client().getChangeSets(name);
				for (ChangeSet changeSet: changeSets.get(lang).get(name).values()) {
					Optional<ChangeSet> latestChangeSet = latestChangeSets.stream()
							.filter(s -> (
								s.getLang().equalsIgnoreCase(changeSet.getLang()) &&
								s.getChangeReference().equalsIgnoreCase(changeSet.getChangeReference()))
							).findFirst();
					if ((!latestChangeSet.isPresent()) ||
							(latestChangeSet.isPresent() && !validateChecksum(latestChangeSet.get(), changeSet))) {
						if (!latestChangeSet.isPresent()) changeSet.setChecksum(calculateChecksum(changeSet));
						log.info("Applying " + changeSet);
						try {
							readChangeSetAndApply(changeSet);
							getV2Client().registerChangeSet(changeSet.getLang(), changeSet.getName(),
									changeSet.getChangeReference(), changeSet.getChecksum());
						} catch (JsonParseException jpe) {
							log.error("Unable to apply changelog: " + jpe, jpe);
							if (force) throw jpe;
						}
					}
				}
				// Apply a cache rebuild
				getV2Client().buildTranslationCache();
			}
		}
	}

	private TranslationV2Client getV2Client() throws lithium.service.client.LithiumServiceClientFactoryException {
		return services.target(TranslationV2Client.class, true);
	}

	private String calculateChecksum(ChangeSet cs) throws IOException {
		String checksum = DigestUtils.md5Hex(cs.getResource().getInputStream());
		log.trace("Calculated checksum for changeset | name: {}, lang: {}, changeRef: {}, checksum: {}", cs.getName(),
				cs.getLang(), cs.getChangeReference(), checksum);
		return checksum;
	}

	private boolean validateChecksum(ChangeSet latestChangeSet, ChangeSet cs) throws IOException {
		log.trace("Validating checksum for changeset | name: {}, lang: {}, changeRef: {}, checksum: {}",
				latestChangeSet.getName(), latestChangeSet.getLang(), latestChangeSet.getChangeReference(),
				latestChangeSet.getChecksum());
		String checksum = calculateChecksum(cs);
		if (latestChangeSet.getChecksum() == null) {
			log.trace("Checksum not present, calculating now. File will be reapplied.");
			cs.setChecksum(checksum);
			return false;
		} else {
			if (!latestChangeSet.getChecksum().contentEquals(checksum)) {
				log.trace("Checksum did not match. File will be reapplied.");
				cs.setChecksum(checksum);
				return false;
			}
		}
		log.trace("Checksum matched");
		return true;
	}
	
	private void readNamespace(ChangeSet changeSet, String currentNamespace, JsonParser parser) throws Exception {
		while(!parser.isClosed()){
			JsonToken jsonToken = parser.nextToken();
			if (JsonToken.END_OBJECT.equals(jsonToken)) return;
			if (!JsonToken.FIELD_NAME.equals(jsonToken)) throw new Exception("Expected FIELD_NAME but got " + jsonToken);
			String fieldName = parser.getText();
			jsonToken = parser.nextToken();
			if (JsonToken.START_OBJECT.equals(jsonToken)) {
				String namespaces = fieldName;
				if (currentNamespace != null) namespaces = currentNamespace + "." + namespaces;
				readNamespace(changeSet, namespaces, parser);
			} else if (JsonToken.VALUE_STRING.equals(jsonToken) && currentNamespace != null) {
				String key = currentNamespace + "." + fieldName;
				String value = parser.getText();
				String lang = changeSet.getLang();
				registerNewTranslation(lang, key, value);
			} else {
				throw new Exception("Expected either namespace or value but got " + jsonToken +" with a namespace: " + currentNamespace + " and field name: " + fieldName);
			}
		}
	}

	public void registerNewTranslation(String lang, String key, String value) throws IOException {
		//All translations are first being registered to the default domain
		registerNewTranslation("default", lang, key, value);
	}

	// This will also register a translation on the default domain if domainName != "default"
	public void registerNewTranslation(String domainName, String lang, String key, String value) throws IOException {
		try {
			translationsStream.registerTranslation(new Domain(domainName), lang, key, value);
		} catch (Exception ex) {
			log.error("The registration of the change log entry failed: domainName=" + domainName + " lang=" +
					lang + " key=" + key + ", value=" + value, ex);
			throw ex;
		}
	}

	private void readChangeSetAndApply(ChangeSet changeSet) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser parser = factory.createParser(changeSet.getResource().getInputStream());
		
		while(!parser.isClosed()){
			JsonToken jsonToken = parser.nextToken();
			if (JsonToken.START_OBJECT.equals(jsonToken)) readNamespace(changeSet, null, parser);
		}
	}

	public void deleteTranslationByCode(String code) throws Exception {
		try {
			getV2Client().deleteTranslationByCode(code);
		} catch (Exception ex) {
			log.error("Failed to remove translation with code " + code);
			throw ex;
		}
	}
}