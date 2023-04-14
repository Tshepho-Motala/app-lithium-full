package lithium.service.domain.client.util;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Locale;

@Slf4j
@Service
@AllArgsConstructor
public class LocaleContextProcessor {
    CachingDomainClientService cachingDomainClientService;
    LithiumTokenUtilService lithiumTokenUtilService;

    public void setLocaleContextHolder(final String locale) {
        try {
            if (locale != null) {
                LocaleContextHolder.setLocale(Locale.forLanguageTag(locale.substring(0, 2)));
                return;
            }
            LocaleContextHolder.setLocale(Locale.forLanguageTag("en"));
        } catch (Exception e) {
            LocaleContextHolder.setLocale(Locale.forLanguageTag("en"));
            log.debug("Locale could not be determined by setLocaleContextHolder(final String locale). Returned default 'en'");
        }
    }

    public void setLocaleContextHolder(final String locale, final String domainName) {
        try {
            if (locale != null) {
                setLocaleContextHolder(locale);
                return;
            } else if (domainName != null) {
                String domainLocale = cachingDomainClientService.retrieveDomainFromDomainService(domainName).getDefaultLocale();
                if (domainLocale != null) {
                    setLocaleContextHolder(domainLocale);
                    return;
                }
            }
            setLocaleContextHolder("en");
        } catch (Exception e) {
            setLocaleContextHolder("en");
            log.debug("Locale could not be determined by setLocaleContextHolder(final String locale, final String domainName). Returned default 'en'");
        }
    }

    public void setLocaleContextHolder(final String locale, final Principal principal) {
        try {
            if (locale != null) {
                setLocaleContextHolder(locale);
                return;
            } else if (principal != null) {
                LithiumTokenUtil util = lithiumTokenUtilService.getUtil(principal);
                if (util != null) {
                    if (util.domainName() != null) {
                        setLocaleContextHolder(null, util.domainName());
                        return;
                    }
                }
            }
            setLocaleContextHolder("en");
        } catch (Exception e) {
            setLocaleContextHolder("en");
            log.debug("Locale could not be determined by setLocaleContextHolder(final String locale, final Principal principal). Returned default 'en'");
        }
    }
}
