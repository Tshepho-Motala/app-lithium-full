package lithium.service.translate.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status412DomainNotFoundException;
import lithium.exceptions.Status413EcosystemNotFoundException;
import lithium.exceptions.Status414LocaleNotFoundOrDisabledException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.objects.ExternalErrorDictionary;
import lithium.service.translate.data.objects.TranslationV2;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.services.ExternalApiAuthenticationService;
import lithium.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ExternalTranslationsService {
    @Autowired
    TranslationService translationService;
    @Autowired
    CachingDomainClientService cachingDomainClientService;
    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    ExternalApiAuthenticationService externalApiAuthenticationService;

    public List<ExternalErrorDictionary> getAllErrorDictionaryMessages(String domainName, String ecosystemName, String locale, Date lastUpdatedSince,
                                                                       String hash, String apiAuthorizationId)
            throws Status470HashInvalidException,
            Status413EcosystemNotFoundException,
            Status550ServiceDomainClientException,
            Status414LocaleNotFoundOrDisabledException, Status412DomainNotFoundException {
        //First we need to validate the hash by use of a payload calculated using a shared key; sourced from the application.yml
        try {
            String payload = formatToStringDate(lastUpdatedSince) + "|" + apiAuthorizationId + "|";
            externalApiAuthenticationService.validate(apiAuthorizationId, payload, hash);
        } catch (Status470HashInvalidException | Status500InternalServerErrorException | Status401UnAuthorisedException e) {
            throw new Status470HashInvalidException("Invalid hash supplied.");
        }

        List<String> domainNames = new ArrayList<>();
        if (!StringUtil.isEmpty(ecosystemName)) {
            try {
                domainNames = cachingDomainClientService.listDomainNamesInEcosystemByEcosystemName(ecosystemName);
            } catch (Status469InvalidInputException | Status550ServiceDomainClientException e) {
                throw new Status413EcosystemNotFoundException("Ecosystem not found.");
            }

            if (domainNames.isEmpty()) {
                throw new Status412DomainNotFoundException("Supplied ecosystem does not contain any domains.");
            }

        } else if (!StringUtil.isEmpty(domainName)) {
            if (cachingDomainClientService.isDomainName(domainName)) {
                domainNames.add(domainName);
            } else {
                throw new Status412DomainNotFoundException("Domain not found.");
            }
        } else {
            domainNames = cachingDomainClientService.retrieveEnabledDomains();
        }

        List<String> locale2s = new ArrayList<>();
        if (locale == null) {
            List<Language> languages = languageRepository.findByEnabled(true);
            if (languages.isEmpty()) {
                throw new Status414LocaleNotFoundOrDisabledException("Locales not enabled.");
            }
            languages.stream().forEach(language -> locale2s.add(language.getLocale2()));
        } else {
            Language language = languageRepository.findByLocale2(locale);
            if (language == null) {
                throw new Status414LocaleNotFoundOrDisabledException("Locale not found.");
            } else if (!language.isEnabled()) {
                throw new Status414LocaleNotFoundOrDisabledException("Locale not enabled.");
            }
            locale2s.add(language.getLocale2());
        }

        List<TranslationV2> translationsList = translationService.getValuesV2ByCodeStartingWithAndLastUpdatedSince(new String[]{"ERROR_DICTIONARY"}, domainNames.toArray(new String[0]), locale2s.toArray(new String[0]), lastUpdatedSince);

        List<ExternalErrorDictionary> externalErrorDictionaryList = new ArrayList<>();
        for (TranslationV2 translation : translationsList) {
            ExternalErrorDictionary externalErrorDictionary = ExternalErrorDictionary.builder()
                    .errorMessageCode(translation.getKey())
                    .domainName(translation.getDomainName())
                    .errorMessageValue(translation.getValue())
                    .locale(translation.getLanguage()).build();
            externalErrorDictionaryList.add(externalErrorDictionary);
        }
        return externalErrorDictionaryList;
    }

    private String formatToStringDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }
}
