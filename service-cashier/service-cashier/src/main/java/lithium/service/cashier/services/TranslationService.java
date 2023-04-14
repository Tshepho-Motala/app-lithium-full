package lithium.service.cashier.services;

import lithium.service.domain.client.CachingDomainClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TranslationService {

    private final MessageSource messageSource;
    private final CachingDomainClientService cachingDomainClientService;

    public String translate(String domainName, String key) {
        return Optional.ofNullable(key)
                .map(k -> translate(domainName, k, null))
                .orElse("");
    }
    public String translate(final String domainName, final String key, Object[] args) {
        try {
            // TODO: Do user specific locale lookup if we want that at some point
            // FIXME: Keep local cache of keys on this service to speed up calls
            log.debug("Translation: domain method name:" + domainName);
            String defaultLocale = cachingDomainClientService.getDomainClient()
                    .findByName(domainName).getData().getDefaultLocale();
            log.debug("Translation: locale:" + defaultLocale);
            return messageSource.getMessage(key, args, key, Locale.forLanguageTag(defaultLocale));
        } catch (Exception e) {
            log.warn("Error performing translation: " + key + " domain: " + domainName, e);
            return key;
        }
    }
}
