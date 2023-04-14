package lithium.service.limit.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.client.objects.placeholders.PlaceholderBuilder;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.enums.AutoRestrictionRuleSetOutcome;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Set;
@Service
@Slf4j
public class RestrictionPlayerCommService {
    @Autowired private MailStream mailStream;
    private static final int PRIORITY_HIGH = 1;


    public void communicateWithPlayer(User user, UserRestrictionSet userRestrictionSet, String restrictionMethod) throws Status500InternalServerErrorException {
        if (userRestrictionSet != null) {
            Set<Placeholder> placeholders = resolvePlaceholders(user, userRestrictionSet);
            sendStaticTemplate(user, userRestrictionSet, restrictionMethod, placeholders);
        }
    }

    private void sendStaticTemplate(User user, UserRestrictionSet userRestrictionSet, String restrictionMethod, Set<Placeholder> placeholders) throws Status500InternalServerErrorException {
        DomainRestrictionSet domainRestrictionSet = userRestrictionSet.getSet();
        if (domainRestrictionSet == null || StringUtils.isEmpty(restrictionMethod)) {
            return;
        }
        String templateName = restrictionMethod.equalsIgnoreCase(AutoRestrictionRuleSetOutcome.PLACE.name()) ?
                domainRestrictionSet.getPlaceMailTemplate() :
                domainRestrictionSet.getLiftMailTemplate();

        if (!StringUtils.isEmpty(templateName)) {
            sendMail(user, templateName, placeholders);
        }
    }

    private Set<Placeholder> resolvePlaceholders(User user, UserRestrictionSet userRestrictionSet) {
        Set<Placeholder> placeholders = new UserToPlaceholderBinder(user).completePlaceholders();
        if (userRestrictionSet != null) {
            // Adding placeholders for email templates
            if (userRestrictionSet.getSubType() != null ) {
                placeholders.add(PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_SUB_TYPE.from(userRestrictionSet.getSubType()));
            }
            placeholders.add(PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_CREATED_DATE.from(userRestrictionSet.getCreatedDateDisplay()));
            placeholders.add(PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_ACTIVE_TO.from(userRestrictionSet.getActiveToDisplay()));
            if (userRestrictionSet.getActiveFrom() != null && userRestrictionSet.getActiveTo() != null) {
                Days days = calculateDays(userRestrictionSet);
                placeholders.add(PlaceholderBuilder.LIMIT_PLAYER_RESTRICTION_DURATION_DAYS.from(days.getDays()));
            }
        }
        return placeholders;
    }

    private Days calculateDays(UserRestrictionSet userRestrictionSet) {
        DateTime activeFrom = new DateTime(userRestrictionSet.getActiveFrom()).withTimeAtStartOfDay();
        DateTime activeTo = new DateTime(userRestrictionSet.getActiveTo()).withTimeAtStartOfDay();
        Days days = Days.daysBetween(activeFrom, activeTo);
        return Days.daysBetween(activeFrom, activeTo);
    }

    private String formatTemplateName(String templateName) {
        String restrictionName = templateName;
        SystemRestriction restriction = SystemRestriction.findByName(templateName);
        if (!ObjectUtils.isEmpty(restriction)){
            //if the restriction is found on the default system restrictions we use that one
            restrictionName = restriction.restrictionName();
        }

        return restrictionName.replaceAll("_", ".").toLowerCase();
    }
    private void sendMail(User user, String templateName, Set<Placeholder> placeholders) {
        log.debug("Sending restriction notification to: u: "+user.getGuid()+" with template name: "+templateName);
        if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(user.getEmail())) {
            //Only when both the user is found, and they have an email stored do we attempt to process the email
            mailStream.process(
                    EmailData.builder()
                            .authorSystem()
                            .emailTemplateName(templateName)
                            .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
                            .to(user.getEmail())
                            .priority(PRIORITY_HIGH)
                            .userGuid(user.guid())
                            .placeholders(placeholders)
                            .domainName(user.getDomain().getName())
                            .build()
            );
        }

    }
}
