package lithium.service.translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.services.TranslationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class TranslateModuleInfo extends ModuleInfoAdapter {
	@Autowired LanguageRepository languageRepository;
	@Autowired TranslationService translationService;
	
	public TranslateModuleInfo() {
		Category translateCategory = Category.builder().name("Translation Operations").description("Operations related to translations.").build();
		addRole(Role.builder().category(translateCategory).name("Translation View").role("VIEW_TRANSLATE").description("View All Translations").build());
		addRole(Role.builder().category(translateCategory).name("Translation Edit").role("EDIT_TRANSLATE").description("Edit All Translations").build());
		addRole(Role.builder().category(translateCategory).name("Language Edit").role("LANGUAGE_EDIT").description("Edit All Languages").build());
		addRole(Role.builder().category(translateCategory).name("Error Messages View").role("ERROR_MESSAGES_VIEW").description("View All Error Message Translations").build());
		addRole(Role.builder().category(translateCategory).name("Error Messages Edit").role("ERROR_MESSAGES_EDIT").description("Edit All Error Message Translations").build());
	}
	
	@PostConstruct
	public void init() {
		registerLanguages();
	}

	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/apiv1/system/translation/translate").access("@lithiumSecurity.authenticatedSystem(authentication)"); //Still being used if translation is not found in V2 structure
		http.authorizeRequests().antMatchers("/apiv1/languages/enabled").permitAll();
		http.authorizeRequests().antMatchers("/apiv1/languages/all").permitAll();
		http.authorizeRequests().antMatchers("/apiv1/languages/list").permitAll();
		http.authorizeRequests().antMatchers("/apiv1/language/{id}/toggle").access("@lithiumSecurity.hasRoleInTree(authentication, 'LANGUAGE_EDIT')");
		http.authorizeRequests().antMatchers("/apiv1/translations/list").access("@lithiumSecurity.hasRoleInTree(authentication, 'VIEW_TRANSLATE')");
		http.authorizeRequests().antMatchers("/apiv1/translation/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'EDIT_TRANSLATE')");

		http.authorizeRequests().antMatchers("/apiv2/system/translation/translate").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/apiv2/translations/angular/**").anonymous();
		http.authorizeRequests().antMatchers("/apiv2/system/translation/translate").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/apiv2/changesets/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/apiv2/translations/{domainName}/{subModule}/list").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ERROR_MESSAGES_VIEW')");
		http.authorizeRequests().antMatchers("/apiv2/translations/{domainName}/get/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ERROR_MESSAGES_VIEW')");
		http.authorizeRequests().antMatchers("/apiv2/translations/{domainName}/changelogs").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ERROR_MESSAGES_VIEW')");
		http.authorizeRequests().antMatchers("/apiv2/translations/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ERROR_MESSAGES_EDIT')");
		http.authorizeRequests().antMatchers("/apiv2/translations/delete").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/apiv2/system/languages/find-language-by-locale").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/external/translations/**").permitAll();
	}

	private List<Language> getAvailableLanguages() {
		ArrayList<Language> languages = new ArrayList<>();
		for (String isolang: Locale.getISOLanguages()) {
			Language language = new Language();
			language.setLocale2(isolang);
			language.setLocale3(new Locale(isolang).getISO3Language());
			language.setDescription(new Locale(isolang).getDisplayName());
			languages.add(language);
		}
		return languages;
	}
	
	private void registerLanguages() {
		for (Language availableLanguage: getAvailableLanguages()) {
			Language language = languageRepository.findByLocale3(availableLanguage.getLocale3());
			if (language == null) 
			languageRepository.save(availableLanguage);
		}
	}
}
