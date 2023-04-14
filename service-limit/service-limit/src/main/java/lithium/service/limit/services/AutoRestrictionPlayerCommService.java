package lithium.service.limit.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.limit.objects.AutoRestrictionRuleSetResult;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_ACTIVE_FROM;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_ACTIVE_TO;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_CREATED_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_DURATION_DAYS;


@Service
@Slf4j

public class AutoRestrictionPlayerCommService {
    @Autowired private MailStream mailStream;
    @Autowired private CachingDomainClientService cachingDomainClientService;

    private static final String DEFAULT_LANG = "en";
    private static final int PRIORITY_HIGH = 1;


    public void communicateWithPlayer(User user, AutoRestrictionRuleSetResult result) throws Status500InternalServerErrorException {
        Set<Placeholder> placeholders = resolvePlaceholders(user, result);
        String templateName =  formatTemplateName(SystemRestriction.UNDERAGE_COMPS_BLOCK.restrictionName());
        sendMail(user, templateName, placeholders);
    }

    private Set<Placeholder> resolvePlaceholders(User user, AutoRestrictionRuleSetResult result) {
        Set<Placeholder> placeholders = new UserToPlaceholderBinder(user).completePlaceholders();
        if (result != null) {
            placeholders.add(LIMIT_PLAYER_RESTRICTION_CREATED_DATE.from(result.getCreatedDateDisplay()));
            placeholders.add(LIMIT_PLAYER_RESTRICTION_ACTIVE_TO.from(result.getactiveToDisplay()));
            placeholders.add(LIMIT_PLAYER_RESTRICTION_ACTIVE_FROM.from(result.getactiveFromDisplay()));
            if (result.getActiveFrom() != null && result.getActiveTo() != null) {
                Days days = calculateDays(result);
                placeholders.add(LIMIT_PLAYER_RESTRICTION_DURATION_DAYS.from(days.getDays()));
            }

        }
        return placeholders;
    }

    private Days calculateDays(AutoRestrictionRuleSetResult result) {
        DateTime activeFrom = new DateTime(result.getActiveFrom()).withTimeAtStartOfDay();
        DateTime activeTo = new DateTime(result.getActiveTo()).withTimeAtStartOfDay();
        Days days = Days.daysBetween(activeFrom, activeTo);
        return days;
    }

    private String formatTemplateName(String templateName) {
        return templateName.replaceAll(" ", ".").toLowerCase();
    }

    private void sendMail(User user, String templateName, Set<Placeholder> placeholders) throws Status500InternalServerErrorException {
        log.debug("Sending restriction notification to: u: "+user.getGuid());
        if (user.getEmail() != null) {
            mailStream.process(
                    EmailData.builder()
                            .authorSystem()
                            .emailTemplateName(templateName)
                            .emailTemplateLang(getEmailTemplateLangForDomain(user.getDomain().getName()))
                            .to(user.getEmail())
                            .priority(PRIORITY_HIGH)
                            .userGuid(user.guid())
                            .placeholders(placeholders)
                            .domainName(user.getDomain().getName())
                            .build()
            );
        }
    }

    private String getEmailTemplateLangForDomain(String domainName) {

        try {
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
            return domain.getDefaultLocale();
        } catch (Status550ServiceDomainClientException e) {
            log.error(String.format("Failed to retrieve domain locale for domain %s, now using %s as a default locale", domainName, DEFAULT_LANG), e);
        }

        return DEFAULT_LANG;
    }
}

